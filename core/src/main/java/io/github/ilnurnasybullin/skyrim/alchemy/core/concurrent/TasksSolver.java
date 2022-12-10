package io.github.ilnurnasybullin.skyrim.alchemy.core.concurrent;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class TasksSolver<X, Y> {

    private final Queue<X> tasks;
    private Executor executor;
    private Function<X, List<Y>> solver;
    private UnaryOperator<List<Y>> maximizer;
    private Function<Y, X> nextTasks;
    private Predicate<X> hasNextSolution;

    public TasksSolver() {
        tasks = new ArrayDeque<>();
    }

    public TasksSolver<X, Y> hasNextSolution(Predicate<X> hasNextSolution) {
        this.hasNextSolution = hasNextSolution;
        return this;
    }

    public TasksSolver<X, Y> nextTask(Function<Y, X> nextDepth) {
        this.nextTasks = nextDepth;
        return this;
    }

    public TasksSolver<X, Y> maximizer(UnaryOperator<List<Y>> maximizer) {
        this.maximizer = maximizer;
        return this;
    }

    public TasksSolver<X, Y> executor(Executor executor) {
        this.executor = executor;
        return this;
    }

    public TasksSolver<X, Y> solver(Function<X, List<Y>> solver) {
        this.solver = solver;
        return this;
    }

    public List<Y> solve(X task) {
        if (hasNextSolution.test(task)) {
            tasks.add(task);
        }

        List<Y> lastAnswers = new ArrayList<>();

        while (!tasks.isEmpty()) {
            @SuppressWarnings("unchecked")
            var collectedTasks = (CompletableFuture<List<Y>>[]) tasks.stream()
                    .map(this::toSupplier)
                    .map(supplier -> CompletableFuture.supplyAsync(supplier, executor))
                    .toArray(CompletableFuture[]::new);

            tasks.clear();

            CompletableFuture.allOf(
                collectedTasks
            );

            lastAnswers = new ArrayList<>();
            for (CompletableFuture<List<Y>> solvedTask: collectedTasks) {
                List<Y> answer;
                try {
                    answer = solvedTask.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }

                lastAnswers.addAll(answer);
            }

            lastAnswers = maximizer.apply(lastAnswers);
            lastAnswers.stream()
                    .map(nextTasks)
                    .filter(hasNextSolution)
                    .forEach(tasks::add);
        }

        return lastAnswers;
    }

    private Supplier<List<Y>> toSupplier(X task) {
        return () -> solver.apply(task);
    }

}
