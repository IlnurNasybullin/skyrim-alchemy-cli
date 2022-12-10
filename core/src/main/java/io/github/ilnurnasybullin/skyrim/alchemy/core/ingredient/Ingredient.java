package io.github.ilnurnasybullin.skyrim.alchemy.core.ingredient;

import io.github.ilnurnasybullin.skyrim.alchemy.core.effect.Effect;
import lombok.Builder;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

public record Ingredient(int id, String name, Set<Effect> effects) {

    public static final int EFFECTS_COUNT_IN_INGREDIENT = 4;

    @Builder
    public static Ingredient of(int id, String name, Set<Effect> effects) {
        Objects.requireNonNull(name, "Name of ingredient is cannot be null!");
        Objects.requireNonNull(effects, "Effects of ingredient is cannot be null!");

        checkEffectsOnLength(effects, id);

        return new Ingredient(id, name, Set.copyOf(effects));
    }

    private static void checkEffectsOnLength(Collection<Effect> effects, int id) {
        int effectsCount = effects.size();
        if (effectsCount != EFFECTS_COUNT_IN_INGREDIENT) {
            throw new IllegalArgumentException(
                    String.format("Ingredient with id = %d must have exactly 4 effects (current is %d)!", id, effectsCount)
            );
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ingredient that = (Ingredient) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean hasAnyEffect(Set<Effect> effects) {
        return !Collections.disjoint(this.effects, effects);
    }
}
