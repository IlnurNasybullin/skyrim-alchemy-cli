package io.github.ilnurnasybullin.skyrim.alchemy.math.mip;

import io.github.ilnurnasybullin.skyrim.alchemy.core.math.mip.FunctionType;
import io.github.ilnurnasybullin.skyrim.alchemy.core.math.mip.Inequality;
import io.github.ilnurnasybullin.skyrim.alchemy.core.math.mip.MipSolution;
import io.github.ilnurnasybullin.math.mip.MipSolver;
import io.github.ilnurnasybullin.math.simplex.Simplex;
import io.github.ilnurnasybullin.math.simplex.SimplexAnswer;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public class MipSolverAdapter implements io.github.ilnurnasybullin.skyrim.alchemy.core.math.mip.MipSolver {

    private final MipSolver mipSolver;
    private Executor executor = Runnable::run;
    private final Simplex.Builder builder;
    private final FunctionTypeAdapter functionTypeAdapter;
    private final InequalityAdapter inequalityAdapter;

    public MipSolverAdapter() {
        mipSolver = new MipSolver();
        builder = new Simplex.Builder();
        functionTypeAdapter = new FunctionTypeAdapter();
        inequalityAdapter = new InequalityAdapter();
    }

    @Override
    public MipSolverAdapter executor(Executor executor) {
        this.executor = executor;
        return this;
    }

    @Override
    public MipSolverAdapter c(double[] c) {
        builder.setC(c);
        return this;
    }

    @Override
    public MipSolverAdapter functionType(FunctionType type) {
        builder.setFunctionType(functionTypeAdapter.adapt(type));
        return this;
    }

    @Override
    public MipSolverAdapter a(double[][] a) {
        builder.setA(a);
        return this;
    }

    @Override
    public MipSolverAdapter b(double[] b) {
        builder.setB(b);
        return this;
    }

    @Override
    public MipSolverAdapter inequalities(Inequality[] inequalities) {
        var adaptedInequalities = Arrays.stream(inequalities)
                .map(inequalityAdapter::adapt)
                .toArray(io.github.ilnurnasybullin.math.simplex.Inequality[]::new);
        builder.setInequalities(adaptedInequalities);
        return this;
    }

    @Override
    public List<MipSolution> solve() {
        return mipSolver.executor(executor)
                .solve(builder)
                .stream()
                .map(MipSolutionAdapter::new)
                .collect(Collectors.toList());
    }

    static class MipSolutionAdapter implements MipSolution {

        private final long[] x;
        private final double fx;

        MipSolutionAdapter(SimplexAnswer answer) {
            x = Arrays.stream(answer.X())
                    .mapToLong(x -> (long) x)
                    .toArray();
            fx = answer.fx();
        }

        @Override
        public long[] x() {
            return Arrays.copyOf(x, x.length);
        }

        @Override
        public double fx() {
            return fx;
        }
    }
}
