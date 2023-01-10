package io.github.ilnurnasybullin.skyrim.alchemy.core.concurrent;

import java.util.Optional;
import java.util.function.Function;

public class TasksSolver<X, Y> {

    private Function<X, Optional<Y>> solver;
    private Function<Y, X> nextTask;

    public TasksSolver<X, Y> nextTask(Function<Y, X> nextTask) {
        this.nextTask = nextTask;
        return this;
    }

    public TasksSolver<X, Y> solver(Function<X, Optional<Y>> solver) {
        this.solver = solver;
        return this;
    }

    public Optional<Y> solve(X task) {
        X nextTask = task;
        Optional<Y> lastAnswer = Optional.empty();
        while (true) {
            Optional<Y> answer = solver.apply(nextTask);
            if (answer.isEmpty()) {
                return lastAnswer;
            }
            lastAnswer = answer;
            nextTask = this.nextTask.apply(answer.get());
        }
    }
}
