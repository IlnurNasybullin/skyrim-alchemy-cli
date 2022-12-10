package io.github.ilnurnasybullin.skyrim.alchemy.core.ingredient;

import io.github.ilnurnasybullin.skyrim.alchemy.core.repository.Repository;

import java.util.ServiceLoader;

/**
 * @author Ilnur Nasybullin
 * @since 08.11.2022
 */
public interface IngredientRepository extends Repository<Ingredient, Integer> {

    static IngredientRepository getInstance() {
        return ServiceLoader.load(IngredientRepository.class)
                .findFirst()
                .orElseThrow();
    }

}
