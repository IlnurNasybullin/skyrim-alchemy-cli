package io.github.ilnurnasybullin.skyrim.alchemy.core.math.mip;

import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.Executor;

public interface MipSolver {

    MipSolver executor(Executor executor);
    MipSolver c(double[] c);
    MipSolver functionType(FunctionType type);
    MipSolver a(double[][] a);
    MipSolver b(double[] b);
    MipSolver inequalities(Inequality[] inequalities);

    List<MipSolution> solve();

    static MipSolver getInstance() {
        return ServiceLoader.load(MipSolver.class)
                .findFirst()
                .orElseThrow();
    }

}
