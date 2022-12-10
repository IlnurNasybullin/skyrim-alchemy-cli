package io.github.ilnurnasybullin.skyrim.alchemy.repository.ingredient;

import io.github.ilnurnasybullin.skyrim.alchemy.core.effect.EffectRepository;
import io.github.ilnurnasybullin.skyrim.alchemy.core.ingredient.Ingredient;
import io.github.ilnurnasybullin.skyrim.alchemy.core.ingredient.IngredientRepository;
import io.github.ilnurnasybullin.skyrim.alchemy.repository.MapRepository;
import io.github.ilnurnasybullin.skyrim.alchemy.repository.config.RepositoryProperties;
import io.github.ilnurnasybullin.skyrim.alchemy.repository.csv.CsvReader;
import io.github.ilnurnasybullin.skyrim.alchemy.repository.locale.DefaultResourceBundle;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Ilnur Nasybullin
 * @since 08.11.2022
 */
public class IngredientRepositoryImpl extends MapRepository<Integer, Ingredient> implements IngredientRepository {

    private final static IngredientRepository INSTANCE = new IngredientRepositoryImpl();

    private final String ingredientsFilename;
    private final String effectsToIngredientsFilename;
    private final IngredientPropertyRepository ingredientPropertyRepository;
    private final EffectRepository effectRepository;

    public IngredientRepositoryImpl() {
        this(
                IngredientPropertyRepository.getInstance(), EffectRepository.getInstance(),
                defaultIngredientsFilename(), defaultEffectsToIngredientsFilename()
        );
    }

    private static String defaultEffectsToIngredientsFilename() {
        return RepositoryProperties.getInstance()
                .keys()
                .key("file")
                .value("effects_to_ingredients")
                .orElseThrow();
    }

    private static String defaultIngredientsFilename() {
        return RepositoryProperties.getInstance()
                .keys()
                .key("file")
                .value("ingredients")
                .orElseThrow();
    }

    public IngredientRepositoryImpl(IngredientPropertyRepository ingredientPropertyRepository,
                                    EffectRepository effectRepository,
                                    String ingredientsFilename,
                                    String effectsToIngredientsFilename) {
        this.ingredientPropertyRepository = ingredientPropertyRepository;
        this.effectRepository = effectRepository;
        this.ingredientsFilename = ingredientsFilename;
        this.effectsToIngredientsFilename = effectsToIngredientsFilename;
        init(new DefaultResourceBundle());
    }

    @Override
    public void init(ResourceBundle resourceBundle) {
        var ingredientBuilders = readIngredients(resourceBundle);

        try(var reader = CsvReader.getInstance();
            var inputStream = getClass().getResourceAsStream(effectsToIngredientsFilename)) {
            reader.inputStream(inputStream)
                    .separator(",")
                    .headers("effect_id", "ingredient_id")
                    .charset(StandardCharsets.UTF_8)
                    .values()
                    .forEach(row -> {
                        var stringEffectId = row.value("effect_id");
                        var effectId = Integer.parseUnsignedInt(stringEffectId, 16);

                        var stringIngredientId = row.value("ingredient_id");
                        var ingredientId = Integer.parseUnsignedInt(stringIngredientId, 16);

                        var effect = effectRepository.findById(effectId)
                                .orElseThrow();

                        ingredientBuilders.get(ingredientId)
                                .addEffect(effect);
                    });
            store = ingredientBuilders.values()
                    .stream()
                    .map(IngredientBuilder::toIngredient)
                    .collect(Collectors.toUnmodifiableMap(Ingredient::id, Function.identity()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Map<Integer, IngredientBuilder> readIngredients(ResourceBundle resourceBundle) {
        try(var reader = CsvReader.getInstance();
            var inputStream = getClass().getResourceAsStream(ingredientsFilename)) {
            return reader.charset(StandardCharsets.UTF_8)
                    .separator(",")
                    .headers("id", "name")
                    .inputStream(inputStream)
                    .values()
                    .map(row -> {
                        var stringId = row.value("id");
                        var id = Integer.parseUnsignedInt(stringId, 16);

                        var propertyName = ingredientPropertyRepository.property(id)
                                .orElseThrow();
                        var name = row.value("name");
                        var ingredientName = resourceBundle.getString(propertyName);
                        if (ingredientName.equals(propertyName)) {
                            ingredientName = name;
                        }

                        return new IngredientBuilder()
                                .id(id)
                                .name(ingredientName);
                    })
                    .collect(Collectors.toUnmodifiableMap(IngredientBuilder::id, Function.identity()));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static IngredientRepository provider() {
        return INSTANCE;
    }
}
