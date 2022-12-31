package io.github.ilnurnasybullin.skyrim.alchemy.cli.mixtures;

import io.github.ilnurnasybullin.skyrim.alchemy.core.effect.Effect;
import io.github.ilnurnasybullin.skyrim.alchemy.core.ingredient.Ingredient;
import io.github.ilnurnasybullin.skyrim.alchemy.core.mixture.Mixture;
import io.github.ilnurnasybullin.skyrim.alchemy.core.repository.Bag;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SimpleMixtureWriter implements MixturesWriter {

    private BufferedWriter writer;
    private int counter;

    private final static String templateForCase = "# Case №%d";
    private final static String templateForMixture = "%d (%s) = %s";

    @Override
    public MixturesWriter outputStream(OutputStream stream) {
        this.writer = new BufferedWriter(new OutputStreamWriter(stream));
        return this;
    }

    @Override
    public void writeMixtures(List<Bag<Mixture>> mixtures) throws IOException {
        counter = 1;
        for (Bag<Mixture> mixtureBag: mixtures) {
            writeMixtures(mixtureBag);
            counter++;
        }
        writer.flush();
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

}
