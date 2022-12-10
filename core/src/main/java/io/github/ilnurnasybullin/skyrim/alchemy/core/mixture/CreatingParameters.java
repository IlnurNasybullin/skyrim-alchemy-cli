package io.github.ilnurnasybullin.skyrim.alchemy.core.mixture;

import io.github.ilnurnasybullin.skyrim.alchemy.core.ingredient.Ingredient;
import io.github.ilnurnasybullin.skyrim.alchemy.core.repository.Bag;

import java.util.Objects;

public class CreatingParameters {

    private final Bag<Ingredient> ingredients;
    private final MixtureWeight weight;
    private final LevelMixtureTemplates templates;
    private final Bag<Mixture> createdMixtures;

    public CreatingParameters(Bag<Ingredient> ingredients, MixtureWeight weight, LevelMixtureTemplates templates, Bag<Mixture> createdMixtures) {
        this.ingredients = ingredients;
        this.weight = weight;
        this.templates = templates;
        this.createdMixtures = createdMixtures;
    }

    public Bag<Ingredient> ingredients() {
        return ingredients;
    }

    public MixtureWeight weight() {
        return weight;
    }

    public LevelMixtureTemplates templates() {
        return templates;
    }

    public Bag<Mixture> createdMixtures() {
        return createdMixtures;
    }

    public CreatingParameters recalculate(Bag<Mixture> mixtures) {
        var recalculatedWeight = weight.recalculateMaxWeight(mixtures);

        var recalculatedIngredients = recalculateIngredients(mixtures);
        var recalculatedTemplates = templates.retainAll(recalculatedIngredients.items());
        var createdMixtures = this.createdMixtures.sum(mixtures);

        return new CreatingParameters(recalculatedIngredients, recalculatedWeight, recalculatedTemplates, createdMixtures);
    }

    private Bag<Ingredient> recalculateIngredients(Bag<Mixture> mixtures) {
        var ingredients = this.ingredients.modifiableMap();
        mixtures.map().forEach((item, count) -> {
            item.ingredients().forEach(ingredient -> {
                ingredients.computeIfPresent(ingredient, (key, oldValue) -> oldValue - count);
            });
        });
        ingredients.values()
                .removeIf(value -> value < 1);

        return new Bag<>(ingredients);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreatingParameters that = (CreatingParameters) o;
        return Objects.equals(ingredients, that.ingredients) && Objects.equals(weight, that.weight)
                && Objects.equals(templates, that.templates) && Objects.equals(createdMixtures, that.createdMixtures);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ingredients, weight, templates, createdMixtures);
    }

    public static class Builder {
        private Bag<Ingredient> ingredients;
        private MixtureWeight weight;
        private LevelMixtureTemplates templates;

        public Builder ingredients(Bag<Ingredient> ingredients) {
            this.ingredients = ingredients;
            return this;
        }

        public Builder weight(double weight) {
            return weight(new MixtureWeight(weight));
        }

        public Builder weight(MixtureWeight weight) {
            this.weight = weight;
            return this;
        }

        public Builder templates(LevelMixtureTemplates templates) {
            this.templates = templates;
            return this;
        }

        public CreatingParameters build() {
            return new CreatingParameters(ingredients, weight, templates, Bag.empty());
        }

    }

}
