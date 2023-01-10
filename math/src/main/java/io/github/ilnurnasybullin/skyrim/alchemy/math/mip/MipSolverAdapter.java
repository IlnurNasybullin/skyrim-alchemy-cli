package io.github.ilnurnasybullin.skyrim.alchemy.math.mip;

import io.github.ilnurnasybullin.math.mip.MipSolver;
import io.github.ilnurnasybullin.math.simplex.Simplex;
import io.github.ilnurnasybullin.math.simplex.SimplexAnswer;
import io.github.ilnurnasybullin.skyrim.alchemy.core.math.mip.FunctionType;
import io.github.ilnurnasybullin.skyrim.alchemy.core.math.mip.Inequality;
import io.github.ilnurnasybullin.skyrim.alchemy.core.math.mip.MipSolution;

import java.util.Arrays;

public class MipSolverAdapter implements io.github.ilnurnasybullin.skyrim.alchemy.core.math.mip.MipSolver {

    private final MipSolver mipSolver;
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
    public MipSolution solve() {
        SimplexAnswer answer = mipSolver.findAny(builder.build());
        return new MipSolutionAdapter(answer);
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MipSolutionAdapter that = (MipSolutionAdapter) o;
            return Arrays.equals(x, that.x);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(x);
        }
    }
}
