package io.github.ilnurnasybullin.skyrim.alchemy.core.mixture;

import io.github.ilnurnasybullin.skyrim.alchemy.core.effect.Effect;
import io.github.ilnurnasybullin.skyrim.alchemy.core.effect.EffectType;
import io.github.ilnurnasybullin.skyrim.alchemy.core.ingredient.Ingredient;

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Mixture {

    public static final double DEFAULT_WEIGHT = 0.5;
    public static final int MIN_INGREDIENTS_COUNT = 2;
    public static final int MAX_INGREDIENTS_COUNT = 3;

    private final Set<Ingredient> ingredients;
    private final MixtureType type;
    private final Set<Effect> effects;

    private Mixture(Set<Ingredient> ingredients, MixtureType type, Set<Effect> effects) {
        this.ingredients = ingredients;
        this.type = type;
        this.effects = effects;
    }

    public Set<Ingredient> ingredients() {
        return ingredients;
    }

    public MixtureType type() {
        return type;
    }

    public Set<Effect> effects() {
        return effects;
    }

    public static Mixture of(Set<Ingredient> ingredients, MixtureType type) {
        checkIngredientsCount(ingredients);
        var commonEffects = Set.copyOf(commonEffects(ingredients, type));

        return new Mixture(Set.copyOf(ingredients), type, commonEffects);
    }

    private static void checkOnNull(Object object, String what) {
        if (object == null) {
            throw new IllegalArgumentException(String.format(
                    "%s is null!", what
            ));
        }
    }

    public static Set<Effect> tryCommonEffects(Set<Ingredient> ingredients, MixtureType type) {
        checkOnNull(type, "Mixture type");
        var effectType = type == MixtureType.POTION ? EffectType.POSITIVE : EffectType.NEGATIVE;
        var effectsWithCount = ingredients.stream()
                .map(Ingredient::effects)
                .flatMap(Set::stream)
                .filter(effect -> effect.type() == effectType)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        effectsWithCount.values().removeIf(value -> value < 2);
        return effectsWithCount.keySet();
    }

    private static Set<Effect> commonEffects(Set<Ingredient> ingredients, MixtureType type) {
        var effects = tryCommonEffects(ingredients, type);
        if (effects.isEmpty()) {
            throw new IllegalArgumentException(String.format(
                    "Mixture with type %s is cannot be created by ingredients %s", type, ingredients
            ));
        }

        return effects;
    }

    private static void checkIngredientsCount(Set<Ingredient> ingredients) {
        var count = ingredients.size();

        if (count < MIN_INGREDIENTS_COUNT || count > MAX_INGREDIENTS_COUNT) {
            throw new IllegalArgumentException(String.format(
                    "Ingredients count (%d not include in [%d, %d]) is illegal for creating mixture!",
                    count, MIN_INGREDIENTS_COUNT, MAX_INGREDIENTS_COUNT
            ));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mixture mixture = (Mixture) o;
        return Objects.equals(ingredients, mixture.ingredients) && type == mixture.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ingredients, type);
    }

    @Override
    public String toString() {
        return String.format(
                "{\"Mixture\": {\"ingredients\": %s, \"type\": %s, \"effects\": %s}}",
                ingredients, type, effects
        );
    }

    public static class Builder {

        private Set<Ingredient> ingredients;
        private MixtureType type;

        public Builder ingredients(Set<Ingredient> ingredients) {
            this.ingredients = ingredients;
            return this;
        }

        public Builder type(MixtureType type) {
            this.type = type;
            return this;
        }

        public Mixture build() {
            return Mixture.of(ingredients, type);
        }

    }

}
