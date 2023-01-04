package test.io.github.ilnurnasybullin.skyrim.alchemy.core;

import io.github.ilnurnasybullin.skyrim.alchemy.core.effect.Effect;
import io.github.ilnurnasybullin.skyrim.alchemy.core.effect.EffectRepository;
import io.github.ilnurnasybullin.skyrim.alchemy.core.ingredient.Ingredient;
import io.github.ilnurnasybullin.skyrim.alchemy.core.ingredient.IngredientRepository;
import io.github.ilnurnasybullin.skyrim.alchemy.core.mixture.Mixture;
import io.github.ilnurnasybullin.skyrim.alchemy.core.mixture.MostCountEffectMixtureCreator;
import io.github.ilnurnasybullin.skyrim.alchemy.core.repository.Bag;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CreatingAllMixturesTest {

    private static class IngredientsReader {

        private final static Pattern itemPattern = Pattern.compile("^(?<count>[0-9]+)\s-\s(?<name>.+)\s\\((?<id>[0-9A-Fa-f]{8})\\)$");

        private final IngredientRepository repository;

        public IngredientsReader() {
            repository = IngredientRepository.getInstance();
        }

        public Bag<Ingredient> ingredients(Path file) throws IOException {
            try(var stream = Files.lines(file, StandardCharsets.UTF_8)) {
                return ingredients(stream);
            }
        }

        public Bag<Ingredient> ingredients(InputStream inputStream) throws IOException {
            try(InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                BufferedReader bufferedReader = new BufferedReader(reader);
                Stream<String> lines = bufferedReader.lines()) {
                return ingredients(lines);
            }
        }

        private Bag<Ingredient> ingredients(Stream<String> lines) {
            Map<Ingredient, Long> ingredientsWithCount = lines.map(this::ingredientWithCount)
                    .filter(IngredientCountBuilder::hasIngredient)
                    .collect(Collectors.toMap(
                            IngredientCountBuilder::ingredient, IngredientCountBuilder::count,
                            Long::sum
                    ));

            return new Bag<>(ingredientsWithCount);
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

    @Test
    public void test() {
        Set<Effect> effects = Set.of(EffectRepository.getInstance()
                .findById(0x3eb1d).orElseThrow()); // smith

        Bag<Ingredient> ingredients = null;
        try(InputStream stream = CreatingAllMixturesTest.class.getClassLoader().getResourceAsStream("./ingredients.txt")) {
            ingredients = new IngredientsReader().ingredients(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<Bag<Mixture>> mixtures = new MostCountEffectMixtureCreator()
                .activatingEffects(effects)
                .desiredEffects(effects)
                .ingredients(ingredients)
                .maxWeight(0.5)
                .createMixturesForNpc();
        Assertions.assertThat(mixtures)
                .hasSize(6);
    }

}
