package io.github.ilnurnasybullin.skyrim.alchemy.cli;

import io.github.ilnurnasybullin.skyrim.alchemy.cli.effects.EffectsReader;
import io.github.ilnurnasybullin.skyrim.alchemy.cli.ingredients.IngredientsReader;
import io.github.ilnurnasybullin.skyrim.alchemy.cli.mixtures.MixturesWriter;
import io.github.ilnurnasybullin.skyrim.alchemy.core.mixture.MostCountEffectMixtureCreator;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.nio.file.*;
import java.util.Scanner;

@Command(
        name = "mixtures-create"
)
public class MixturesCreator implements Runnable {

    @Option(names = {"-i", "--ingredients"}, interactive = true)
    private Path ingredientsFile;

    @Option(names = {"-ae", "--activating-effects"}, interactive = true)
    private Path activatingEffectsFile;

    @Option(names = {"-de", "--desired-effects"}, interactive = true)
    private Path desiredEffectsFile;

    @Option(names = {"-mw", "--max-weight"}, interactive = true)
    private Double maxWeight;

    @Option(names = {"-of", "--output-file"}, interactive = true)
    private Path outputFile;

    @Override
    public void run() {
        interactWithArgs();
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
            System.out.println(mixtures.items().size());

            try(var stream = Files.newOutputStream(outputFile, newWrite())) {
                MixturesWriter.getInstance()
                        .outputStream(stream)
                        .writeMixtures(mixtures);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private OpenOption[] newWrite() {
        return new StandardOpenOption[] {
            StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING
        };
    }

    private void interactWithArgs() {
        var console = new Scanner(System.in);
        ingredientsFile = readIngredientsFile(console);
        activatingEffectsFile = readActivatingEffectsFile(console);
        desiredEffectsFile = readDesiredEffectsFile(console);
        maxWeight = readMaxWeight(console);
        outputFile = readOutputFile(console);
    }

    private Path readOutputFile(Scanner console) {
        if (outputFile != null) {
            return outputFile;
        }

        System.out.print("Enter filepath to output file (if it not exists, it will be created): ");
        var filepath = console.next();
        return Paths.get(filepath);
    }

    private Double readMaxWeight(Scanner console) {
        if (maxWeight != null) {
            return maxWeight;
        }

        System.out.print("Enter max weight for created mixtures: ");
        return console.nextDouble();
    }

    private Path readDesiredEffectsFile(Scanner console) {
        if (desiredEffectsFile != null) {
            return desiredEffectsFile;
        }

        System.out.print("Enter filepath to desired effects file: ");
        var filepath = console.next();

        return Paths.get(filepath);
    }

    private Path readActivatingEffectsFile(Scanner console) {
        if (activatingEffectsFile != null) {
            return activatingEffectsFile;
        }

        System.out.print("Enter filepath to activating effects file: ");
        var filePath = console.next();

        return Paths.get(filePath);
    }

    private Path readIngredientsFile(Scanner console) {
        if (ingredientsFile != null) {
            return ingredientsFile;
        }

        System.out.print("Enter filepath to ingredients file: ");
        var filePath = console.next();

        return Paths.get(filePath);
    }
}
