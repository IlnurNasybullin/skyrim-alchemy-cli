package io.github.ilnurnasybullin.skyrim.alchemy.cli.mixtures;

import io.github.ilnurnasybullin.skyrim.alchemy.core.mixture.Mixture;
import io.github.ilnurnasybullin.skyrim.alchemy.core.repository.Bag;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.ServiceLoader;

public interface MixturesWriter {
    MixturesWriter file(Path path);
    void writeMixtures(List<Bag<Mixture>> mixtures) throws IOException;

    static MixturesWriter getInstance() {
        return ServiceLoader.load(MixturesWriter.class)
                .findFirst()
                .orElseThrow();
    }
}
