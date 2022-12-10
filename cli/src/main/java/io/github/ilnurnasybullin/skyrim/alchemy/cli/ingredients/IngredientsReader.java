package io.github.ilnurnasybullin.skyrim.alchemy.cli.ingredients;

import io.github.ilnurnasybullin.skyrim.alchemy.core.ingredient.Ingredient;
import io.github.ilnurnasybullin.skyrim.alchemy.core.repository.Bag;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ServiceLoader;

public interface IngredientsReader {
    Bag<Ingredient> ingredients(Path file) throws IOException;

    static IngredientsReader getInstance() {
        return ServiceLoader.load(IngredientsReader.class)
                .findFirst()
                .orElseThrow();
    }
}
