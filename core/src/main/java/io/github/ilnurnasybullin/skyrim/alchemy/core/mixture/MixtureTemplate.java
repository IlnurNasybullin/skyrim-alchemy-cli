package io.github.ilnurnasybullin.skyrim.alchemy.core.mixture;

import io.github.ilnurnasybullin.skyrim.alchemy.core.effect.Effect;
import io.github.ilnurnasybullin.skyrim.alchemy.core.ingredient.Ingredient;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public class MixtureTemplate {

    private final Set<Ingredient> ingredients;
    private final MixtureType type;

    public MixtureTemplate(Set<Ingredient> ingredients, MixtureType type) {
        this.ingredients = Set.copyOf(ingredients);
        this.type = type;
    }

    public Set<Ingredient> ingredients() {
        return ingredients;
    }

    public MixtureType type() {
        return type;
    }

    public Optional<MixtureTemplate> retainAll(Set<Ingredient> ingredients) {
        var retained = new HashSet<>(this.ingredients);

        if (retained.retainAll(ingredients)) {
            return Optional.empty();
        }

        return Optional.of(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MixtureTemplate template = (MixtureTemplate) o;
        return Objects.equals(ingredients, template.ingredients) && type == template.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ingredients, type);
    }

    public static UsefulEffectsBuilder commonEffectsBuilder() {
        return new UsefulEffectsBuilder();
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

        public MixtureTemplate build() {
            return new MixtureTemplate(ingredients, type);
        }

    }

    public static class UsefulEffectsBuilder {

        private Set<Ingredient> ingredients;
        private Predicate<Set<Effect>> isUsefulEffects;
        private Predicate<Effect> isUsefulEffect;
        private MixtureType mixtureType;

        public UsefulEffectsBuilder ingredients(Set<Ingredient> ingredients) {
            this.ingredients = ingredients;
            return this;
        }

        public UsefulEffectsBuilder isUsefulEffects(Predicate<Set<Effect>> isUsefulEffects) {
            this.isUsefulEffects = isUsefulEffects;
            return this;
        }

        public UsefulEffectsBuilder isUsefulEffect(Predicate<Effect> isUsefulEffect) {
            this.isUsefulEffect = isUsefulEffect;
            return this;
        }

        public UsefulEffectsBuilder mixtureType(MixtureType mixtureType) {
            this.mixtureType = mixtureType;
            return this;
        }

        private Set<Ingredient> ingredientWithoutThat(Ingredient that) {
            var ingredients = new HashSet<>(this.ingredients);
            ingredients.remove(that);
            return ingredients;
        }

        private static <T> Set<T> setsDiff(Set<T> set1, Set<T> set2) {
            Set<T> diff = new HashSet<>(set1);
            diff.removeAll(set2);
            return diff;
        }

        public Set<Effect> tryBuild() {
            var effects = Mixture.tryCommonEffects(ingredients, mixtureType);
            if (!isUsefulEffects.test(effects)) {
                return Set.of();
            }

            if (ingredients.size() == Mixture.MIN_INGREDIENTS_COUNT) {
                return effects;
            }

            // check that every ingredient gives benefit
            for (var ingredient: ingredients) {
                var ingredientsWithoutX = ingredientWithoutThat(ingredient);
                var newEffects = Mixture.tryCommonEffects(ingredientsWithoutX, mixtureType);

                var diffEffects = setsDiff(effects, newEffects);

                if (!isUsefulEffects(diffEffects)) {
                    return Set.of();
                }
            }

            return effects;
        }

        private boolean isUsefulEffects(Set<Effect> effects) {
            return effects.stream()
                    .anyMatch(isUsefulEffect);
        }

    }
}
