package io.github.ilnurnasybullin.skyrim.alchemy.cli.effects;

import io.github.ilnurnasybullin.skyrim.alchemy.core.effect.Effect;
import io.github.ilnurnasybullin.skyrim.alchemy.core.effect.EffectRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SimpleEffectsReader implements EffectsReader {

    private final static Pattern linePattern = Pattern.compile("^(?<id>[0-9A-Fa-f]{1,8})");

    private final EffectRepository repository;

    public SimpleEffectsReader() {
        repository = EffectRepository.getInstance();
    }

    @Override
    public Set<Effect> effects(Path file) throws IOException {
        try(var stream = Files.lines(file)) {
            return stream.map(this::effect)
                    .flatMap(Optional::stream)
                    .collect(Collectors.toSet());
        }
    }

    private Optional<Effect> effect(String line) {
        var matcher = linePattern.matcher(line);
        if (!matcher.find()) {
            return Optional.empty();
        }

        var stringId = matcher.group("id");
        var id = Integer.parseUnsignedInt(stringId, 16);
        return repository.findById(id);
    }

}
