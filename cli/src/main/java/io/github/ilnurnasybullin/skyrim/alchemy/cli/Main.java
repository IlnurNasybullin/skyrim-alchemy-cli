package io.github.ilnurnasybullin.skyrim.alchemy.cli;

import io.github.ilnurnasybullin.skyrim.alchemy.core.effect.EffectRepository;
import io.github.ilnurnasybullin.skyrim.alchemy.core.ingredient.IngredientRepository;
import io.github.ilnurnasybullin.skyrim.alchemy.locale.AlchemyResourceBundleProvider;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.Locale;

@Command(subcommands = {
        MixturesCreator.class,
        EffectsPrinter.class
})
public class Main {

    static {
        var bundleProvider = new AlchemyResourceBundleProvider();
        EffectRepository.getInstance().init(bundleProvider.getBundle("effects", Locale.getDefault()));
        IngredientRepository.getInstance().init(bundleProvider.getBundle("ingredients", Locale.getDefault()));
    }

    public static void main(String[] args) {
        new CommandLine(new Main())
                .execute(args);
    }
}