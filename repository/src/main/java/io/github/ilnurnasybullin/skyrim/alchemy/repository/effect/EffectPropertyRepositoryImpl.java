package io.github.ilnurnasybullin.skyrim.alchemy.repository.effect;

import io.github.ilnurnasybullin.skyrim.alchemy.repository.MapPropertyRepository;
import io.github.ilnurnasybullin.skyrim.alchemy.repository.config.RepositoryProperties;
import io.github.ilnurnasybullin.skyrim.alchemy.repository.csv.CsvReader;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

public class EffectPropertyRepositoryImpl extends MapPropertyRepository<Integer, String> implements EffectPropertyRepository {

    private final static EffectPropertyRepositoryImpl INSTANCE = new EffectPropertyRepositoryImpl();

    public EffectPropertyRepositoryImpl() {
        this(defaultEffectRepositoryFileName());
    }

    private static String defaultEffectRepositoryFileName() {
        return RepositoryProperties.getInstance()
                .keys()
                .key("file")
                .value("effects")
                .orElseThrow();
    }

    public EffectPropertyRepositoryImpl(String filename) {
        super(filename);
    }

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

    public static EffectPropertyRepository provider() {
        return INSTANCE;
    }

}
