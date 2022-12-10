import io.github.ilnurnasybullin.skyrim.alchemy.core.effect.EffectRepository;
import io.github.ilnurnasybullin.skyrim.alchemy.core.ingredient.IngredientRepository;
import io.github.ilnurnasybullin.skyrim.alchemy.core.math.combinatoric.CombinatorialService;
import io.github.ilnurnasybullin.skyrim.alchemy.core.math.mip.MipSolver;
import io.github.ilnurnasybullin.skyrim.alchemy.core.mixture.MostCountEffectMixtureCreator;
import io.github.ilnurnasybullin.skyrim.alchemy.core.mixture.MixtureCreator;

module io.github.ilnurnasybullin.skyrim.alchemy.core {
    requires static lombok;

    exports io.github.ilnurnasybullin.skyrim.alchemy.core.effect;
    exports io.github.ilnurnasybullin.skyrim.alchemy.core.mixture;
    exports io.github.ilnurnasybullin.skyrim.alchemy.core.ingredient;
    exports io.github.ilnurnasybullin.skyrim.alchemy.core.repository;
    exports io.github.ilnurnasybullin.skyrim.alchemy.core.math.combinatoric;
    exports io.github.ilnurnasybullin.skyrim.alchemy.core.math.mip;

    provides MixtureCreator with MostCountEffectMixtureCreator;
    uses EffectRepository;
    uses IngredientRepository;
    uses CombinatorialService;
    uses MipSolver;
}