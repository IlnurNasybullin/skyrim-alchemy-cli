package io.github.ilnurnasybullin.skyrim.alchemy.cli.effects;

import io.github.ilnurnasybullin.skyrim.alchemy.core.effect.Effect;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ServiceLoader;
import java.util.Set;

public interface EffectsReader {
    Set<Effect> effects(Path file) throws IOException;

    static EffectsReader getInstance() {
        return ServiceLoader.load(EffectsReader.class)
                .findFirst()
                .orElseThrow();
    }
}
