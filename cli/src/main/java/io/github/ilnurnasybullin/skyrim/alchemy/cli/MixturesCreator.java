package io.github.ilnurnasybullin.skyrim.alchemy.cli;

import io.github.ilnurnasybullin.skyrim.alchemy.cli.effects.EffectsReader;
import io.github.ilnurnasybullin.skyrim.alchemy.cli.ingredients.IngredientsReader;
import io.github.ilnurnasybullin.skyrim.alchemy.cli.mixtures.MixturesWriter;
import io.github.ilnurnasybullin.skyrim.alchemy.core.mixture.MostCountEffectMixtureCreator;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.nio.file.Path;

@Command(
        name = "mixtures-create"
)
public class MixturesCreator implements Runnable {

    @Option(names = {"-i", "--ingredients"}, required = true)
    private Path ingredientsFile;

    @Option(names = {"-ae", "--activating-effects"}, required = true)
    private Path activatingEffectsFile;

    @Option(names = {"-de", "--desired-effects"}, required = true)
    private Path desiredEffectsFile;

    @Option(names = {"-mw", "--max-weight"}, required = true)
    private Double maxWeight;

    @Option(names = {"-of", "--output-file"}, required = true)
    private Path outputFile;

    @Override
    public void run() {
        try {
            var ingredients = IngredientsReader.getInstance()
                    .ingredients(ingredientsFile);

            var activatingEffects = EffectsReader.getInstance()
                    .effects(activatingEffectsFile);

            var desiredEffects = EffectsReader.getInstance()
                    .effects(desiredEffectsFile);

            var mixtures = new MostCountEffectMixtureCreator()
                    .maxWeight(maxWeight)
                    .desiredEffects(desiredEffects)
                    .ingredients(ingredients)
                    .activatingEffects(activatingEffects)
                    .createMixturesForNpc();

            MixturesWriter.getInstance()
                    .file(outputFile)
                    .writeMixtures(mixtures);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
