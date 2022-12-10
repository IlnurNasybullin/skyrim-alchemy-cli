import io.github.ilnurnasybullin.skyrim.alchemy.core.math.combinatoric.CombinatorialService;
import io.github.ilnurnasybullin.skyrim.alchemy.core.math.mip.MipSolver;
import io.github.ilnurnasybullin.skyrim.alchemy.math.combinatorial.Combinatorial;
import io.github.ilnurnasybullin.skyrim.alchemy.math.mip.MipSolverAdapter;

module io.github.ilnurnasybullin.skyrim.alchemy.math {
    requires io.github.ilnurnasybullin.skyrim.alchemy.core;
    requires io.github.ilnurnasybullin.math.combinations;
    requires io.github.ilnurnasybullin.math.mip;
    requires io.github.ilnurnasybullin.math.simplex;

    provides CombinatorialService with Combinatorial;
    provides MipSolver with MipSolverAdapter;
}