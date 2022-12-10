package io.github.ilnurnasybullin.skyrim.alchemy.repository.ingredient;

import java.util.Optional;
import java.util.ServiceLoader;

public interface IngredientPropertyRepository {
    Optional<String> property(Integer id);

    static IngredientPropertyRepository getInstance() {
        return ServiceLoader.load(IngredientPropertyRepository.class)
                .findFirst()
                .orElseThrow();
    }
}
