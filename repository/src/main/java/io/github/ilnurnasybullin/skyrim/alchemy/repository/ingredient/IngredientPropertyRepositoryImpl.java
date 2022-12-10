package io.github.ilnurnasybullin.skyrim.alchemy.repository.ingredient;

import io.github.ilnurnasybullin.skyrim.alchemy.repository.MapPropertyRepository;
import io.github.ilnurnasybullin.skyrim.alchemy.repository.config.RepositoryProperties;
import io.github.ilnurnasybullin.skyrim.alchemy.repository.csv.CsvReader;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Ilnur Nasybullin
 * @since 08.11.2022
 */
public class IngredientPropertyRepositoryImpl extends MapPropertyRepository<Integer, String> implements IngredientPropertyRepository {

    private final static IngredientPropertyRepository INSTANCE = new IngredientPropertyRepositoryImpl();

    public IngredientPropertyRepositoryImpl() {
        this(defaultIngredientPropertiesFilename());
    }

    private static String defaultIngredientPropertiesFilename() {
        return RepositoryProperties.getInstance()
                .keys()
                .key("file")
                .value("ingredients")
                .orElseThrow();
    }

    public IngredientPropertyRepositoryImpl(String filename) {
        super(filename);
    }

    @Override
    protected Map<Integer, String> readProperties(String filename) {
        try(var csvReader = CsvReader.getInstance();
            var inputStream = getClass().getResourceAsStream(filename)) {
            return csvReader.inputStream(inputStream)
                    .separator(",")
                    .headers("id", "name")
                    .charset(StandardCharsets.UTF_8)
                    .values()
                    .map(row -> {
                        var stringId = row.value("id");
                        var id = Integer.parseUnsignedInt(stringId, 16);

                        var property = row.value("name")
                                .replaceAll("\s+", "_");

                        return Map.entry(id, property);
                    })
                    .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static IngredientPropertyRepository provider() {
        return INSTANCE;
    }
}
