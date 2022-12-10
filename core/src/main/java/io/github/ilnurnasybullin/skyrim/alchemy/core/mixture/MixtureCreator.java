package io.github.ilnurnasybullin.skyrim.alchemy.core.mixture;

import io.github.ilnurnasybullin.skyrim.alchemy.core.effect.Effect;
import io.github.ilnurnasybullin.skyrim.alchemy.core.ingredient.Ingredient;
import io.github.ilnurnasybullin.skyrim.alchemy.core.repository.Bag;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

public interface MixtureCreator {
    MixtureCreator ingredients(Bag<Ingredient> ingredients);
    MixtureCreator activatingEffects(Set<Effect> effects);
    MixtureCreator desiredEffects(Set<Effect> effects);
    MixtureCreator maxWeight(double maxWeight);
    List<Bag<Mixture>> createMixturesForNpc();

    MixtureCreator executor(Executor executor);
}
