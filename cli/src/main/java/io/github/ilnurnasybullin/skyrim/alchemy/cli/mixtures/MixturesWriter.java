package io.github.ilnurnasybullin.skyrim.alchemy.cli.mixtures;

import io.github.ilnurnasybullin.skyrim.alchemy.core.mixture.Mixture;
import io.github.ilnurnasybullin.skyrim.alchemy.core.repository.Bag;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.ServiceLoader;

public interface MixturesWriter {

    /**
     * This method <b>is not close</b> given stream
     */
    MixturesWriter outputStream(OutputStream stream);
    void writeMixtures(List<Bag<Mixture>> mixtures) throws IOException;

    static MixturesWriter getInstance() {
        return ServiceLoader.load(MixturesWriter.class)
                .findFirst()
                .orElseThrow();
    }
}
