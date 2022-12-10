package io.github.ilnurnasybullin.skyrim.alchemy.repository.ingredient;

import io.github.ilnurnasybullin.skyrim.alchemy.core.effect.Effect;
import io.github.ilnurnasybullin.skyrim.alchemy.core.ingredient.Ingredient;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Ilnur Nasybullin
 * @since 08.11.2022
 */
public class IngredientBuilder {

    private Integer id;
    private String name;
    private final Set<Effect> effects;

    public IngredientBuilder() {
        this.effects = new HashSet<>();
    }

    public Integer id() {
        return id;
    }

    public IngredientBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public IngredientBuilder name(String name) {
        this.name = name;
        return this;
    }

    public IngredientBuilder addEffect(Effect effect) {
        this.effects.add(effect);
        return this;
    }

    public Ingredient toIngredient() {
        return Ingredient.of(id, name, effects);
    }

}
