package io.github.ilnurnasybullin.skyrim.alchemy.cli.mixtures;

import io.github.ilnurnasybullin.skyrim.alchemy.core.effect.Effect;
import io.github.ilnurnasybullin.skyrim.alchemy.core.ingredient.Ingredient;
import io.github.ilnurnasybullin.skyrim.alchemy.core.mixture.Mixture;
import io.github.ilnurnasybullin.skyrim.alchemy.core.repository.Bag;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.stream.Collectors;

public class SimpleMixtureWriter implements MixturesWriter {

    private BufferedWriter writer;
    private final static String templateForMixture = "%d (%s) = %s";

    @Override
    public MixturesWriter outputStream(OutputStream stream) {
        this.writer = new BufferedWriter(new OutputStreamWriter(stream));
        return this;
    }

    @Override
    public void writeMixtures(Bag<Mixture> mixtures) throws IOException {
        for (Map.Entry<Mixture, Long> mixtureWithCount: mixtures.map().entrySet()) {
            var mixture = mixtureWithCount.getKey();
            var count = mixtureWithCount.getValue();

            writeMixture(mixture, count);
        }
        writer.flush();
    }

    private void writeMixture(Mixture mixture, long count) throws IOException {
        var ingredients = mixture.ingredients()
                .stream()
                .map(Ingredient::name)
                .map(name -> String.format("'%s'", name))
                .collect(Collectors.joining(" + "));

        var effects = mixture.effects()
                .stream()
                .map(Effect::name)
                .map(name -> String.format("'%s'", name))
                .collect(Collectors.joining(" + "));

        writer.write(String.format(templateForMixture, count, ingredients, effects));
        writer.newLine();
    }

}
