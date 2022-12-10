package io.github.ilnurnasybullin.skyrim.alchemy.repository.effect;

import io.github.ilnurnasybullin.skyrim.alchemy.core.effect.Effect;
import io.github.ilnurnasybullin.skyrim.alchemy.core.effect.EffectRepository;
import io.github.ilnurnasybullin.skyrim.alchemy.core.effect.EffectType;
import io.github.ilnurnasybullin.skyrim.alchemy.repository.MapRepository;
import io.github.ilnurnasybullin.skyrim.alchemy.repository.config.RepositoryProperties;
import io.github.ilnurnasybullin.skyrim.alchemy.repository.csv.CsvReader;
import io.github.ilnurnasybullin.skyrim.alchemy.repository.locale.DefaultResourceBundle;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EffectRepositoryImpl extends MapRepository<Integer, Effect> implements EffectRepository {

    private final static EffectRepository INSTANCE = new EffectRepositoryImpl();

    private final String effectsFilename;
    private final EffectPropertyRepository effectPropertyRepository;

    public EffectRepositoryImpl() {
        this(defaultEffectsFilename(), EffectPropertyRepository.getInstance());
    }

    private static String defaultEffectsFilename() {
        return RepositoryProperties.getInstance()
                .keys()
                .key("file")
                .value("effects")
                .orElseThrow();
    }

    public EffectRepositoryImpl(String effectsFilename, EffectPropertyRepository effectPropertyRepository) {
        this.effectsFilename = effectsFilename;
        this.effectPropertyRepository = effectPropertyRepository;
        init(new DefaultResourceBundle());
    }

    @Override
    public void init(ResourceBundle resourceBundle) {
        var effectTypes = Arrays.stream(EffectType.values())
                .collect(Collectors.toUnmodifiableMap(
                        EffectType::type, Function.identity()
                ));

        try(var reader = CsvReader.getInstance();
            var inputStream = getClass().getResourceAsStream(effectsFilename)) {
            store = reader.inputStream(inputStream)
                    .separator(",")
                    .headers("id", "name", "type")
                    .charset(StandardCharsets.UTF_8)
                    .values()
                    .map(row -> {
                        var stringId = row.value("id");
                        var id = Integer.parseUnsignedInt(stringId, 16);

                        var propertyName = effectPropertyRepository.property(id)
                                .orElseThrow();

                        var name = row.value("name");
                        var mappedName = resourceBundle.getString(propertyName);

                        if (mappedName.equals(propertyName)) {
                            mappedName = name;
                        }

                        var stringType = row.value("type");
                        var type = effectTypes.get(stringType);

                        return Effect.of(id, mappedName, type);
                    })
                    .collect(Collectors.toUnmodifiableMap(Effect::id, Function.identity()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static EffectRepository provider() {
        return INSTANCE;
    }
}
