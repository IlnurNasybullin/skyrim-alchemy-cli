package io.github.ilnurnasybullin.skyrim.alchemy.core.mixture;

import io.github.ilnurnasybullin.skyrim.alchemy.core.effect.Effect;
import io.github.ilnurnasybullin.skyrim.alchemy.core.ingredient.Ingredient;
import io.github.ilnurnasybullin.skyrim.alchemy.core.repository.Bag;

import java.util.Set;

public interface MixtureCreator {
    MixtureCreator ingredients(Bag<Ingredient> ingredients);
    MixtureCreator activatingEffects(Set<Effect> effects);
    MixtureCreator desiredEffects(Set<Effect> effects);
    MixtureCreator maxWeight(double maxWeight);
    Bag<Mixture> createMixturesForNpc();
}
