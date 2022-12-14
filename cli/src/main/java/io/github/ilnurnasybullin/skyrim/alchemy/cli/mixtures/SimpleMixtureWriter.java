package io.github.ilnurnasybullin.skyrim.alchemy.cli.mixtures;

import io.github.ilnurnasybullin.skyrim.alchemy.core.effect.Effect;
import io.github.ilnurnasybullin.skyrim.alchemy.core.ingredient.Ingredient;
import io.github.ilnurnasybullin.skyrim.alchemy.core.mixture.Mixture;
import io.github.ilnurnasybullin.skyrim.alchemy.core.repository.Bag;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SimpleMixtureWriter implements MixturesWriter {

    private Path file;
    private BufferedWriter writer;
    private int counter;

    private final static String templateForCase = "# Case №%d";
    private final static String templateForMixture = "%d (%s) = %s";

    @Override
    public MixturesWriter file(Path file) {
        this.file = file;
        return this;
    }

    @Override
    public void writeMixtures(List<Bag<Mixture>> mixtures) throws IOException {
        try (var writer = newWriter()) {
            this.writer = writer;
            counter = 1;
            for (Bag<Mixture> mixtureBag: mixtures) {
                writeMixtures(mixtureBag);
                counter++;
            }
        }
    }

    private void writeMixtures(Bag<Mixture> mixtures) throws IOException {
        writer.newLine();
        writer.write(String.format(templateForCase, counter));
        writer.newLine();

        for (Map.Entry<Mixture, Long> mixtureWithCount: mixtures.map().entrySet()) {
            var mixture = mixtureWithCount.getKey();
            var count = mixtureWithCount.getValue();

            writeMixture(mixture, count);
        }
    }

    private void writeMixture(Mixture mixture, long count) throws IOException {
        var ingredients = mixture.ingredients()
                .stream()
                .map(Ingredient::name)
                .collect(Collectors.joining(" + "));

        var effects = mixture.effects()
                .stream()
                .map(Effect::name)
                .collect(Collectors.joining(" + "));

        writer.write(String.format(templateForMixture, count, ingredients, effects));
        writer.newLine();
    }

    private BufferedWriter newWriter() throws IOException {
        return Files.newBufferedWriter(
                file, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING
        );
    }
}
