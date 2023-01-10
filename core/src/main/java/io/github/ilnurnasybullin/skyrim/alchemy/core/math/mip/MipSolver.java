package io.github.ilnurnasybullin.skyrim.alchemy.core.math.mip;

import java.util.ServiceLoader;

public interface MipSolver {

    MipSolver c(double[] c);
    MipSolver functionType(FunctionType type);
    MipSolver a(double[][] a);
    MipSolver b(double[] b);
    MipSolver inequalities(Inequality[] inequalities);

    MipSolution solve();

    static MipSolver getInstance() {
        return ServiceLoader.load(MipSolver.class)
                .findFirst()
                .orElseThrow();
    }

}
