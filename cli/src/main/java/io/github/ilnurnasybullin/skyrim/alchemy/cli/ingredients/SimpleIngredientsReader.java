package io.github.ilnurnasybullin.skyrim.alchemy.cli.ingredients;

import io.github.ilnurnasybullin.skyrim.alchemy.core.ingredient.Ingredient;
import io.github.ilnurnasybullin.skyrim.alchemy.core.ingredient.IngredientRepository;
import io.github.ilnurnasybullin.skyrim.alchemy.core.repository.Bag;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SimpleIngredientsReader implements IngredientsReader {

    private final static Pattern itemPattern = Pattern.compile("^(?<count>[0-9]+)\s-\s(?<name>.+)\s\\((?<id>[0-9A-Fa-f]{8})\\)$");

    private final IngredientRepository repository;

    public SimpleIngredientsReader() {
        repository = IngredientRepository.getInstance();
    }

    @Override
    public Bag<Ingredient> ingredients(Path file) throws IOException {
        try(var stream = Files.lines(file)) {
            var ingredientsWithCount = stream.map(this::ingredientWithCount)
                    .filter(IngredientCountBuilder::hasIngredient)
                    .collect(Collectors.toMap(
                            IngredientCountBuilder::ingredient, IngredientCountBuilder::count,
                            Long::sum
                    ));

            return new Bag<>(ingredientsWithCount);
        }
    }

    private IngredientCountBuilder ingredientWithCount(String line) {
        var matcher = itemPattern.matcher(line);

        if (!matcher.find()) {
            return IngredientCountBuilder.EMPTY;
        }

        return new IngredientCountBuilder()
                .id(matcher.group("id"))
                .count(matcher.group("count"))
                .tryMapToIngredient(repository::findById);
    }

    private static class IngredientCountBuilder {

        private int id;
        private long count;
        private Optional<Ingredient> ingredient = Optional.empty();

        private static final IngredientCountBuilder EMPTY = new IngredientCountBuilder();

        public IngredientCountBuilder id(String id) {
            this.id = Integer.parseUnsignedInt(id, 16);
            return this;
        }

        public IngredientCountBuilder count(String count) {
            this.count = Long.parseLong(count);
            return this;
        }

        public IngredientCountBuilder tryMapToIngredient(Function<Integer, Optional<Ingredient>> mapper) {
            ingredient = mapper.apply(id);
            return this;
        }

        public boolean hasIngredient() {
            return ingredient.isPresent();
        }

        public Ingredient ingredient() {
            return ingredient.orElseThrow();
        }

        public long count() {
            return count;
        }

    }
}
