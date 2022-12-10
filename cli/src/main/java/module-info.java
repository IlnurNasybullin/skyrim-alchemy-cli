import io.github.ilnurnasybullin.skyrim.alchemy.cli.effects.EffectsReader;
import io.github.ilnurnasybullin.skyrim.alchemy.cli.effects.SimpleEffectsReader;
import io.github.ilnurnasybullin.skyrim.alchemy.cli.ingredients.IngredientsReader;
import io.github.ilnurnasybullin.skyrim.alchemy.cli.ingredients.SimpleIngredientsReader;
import io.github.ilnurnasybullin.skyrim.alchemy.cli.mixtures.MixturesWriter;
import io.github.ilnurnasybullin.skyrim.alchemy.cli.mixtures.SimpleMixtureWriter;

module io.github.ilnurnasybullin.skyrim.alchemy.cli {
    requires io.github.ilnurnasybullin.skyrim.alchemy.core;
    requires io.github.ilnurnasybullin.skyrim.alchemy.math;
    requires io.github.ilnurnasybullin.skyrim.alchemy.repository;
    requires io.github.ilnurnasybullin.skyrim.alchemy.locale;
    requires info.picocli;

    exports io.github.ilnurnasybullin.skyrim.alchemy.cli;
    opens io.github.ilnurnasybullin.skyrim.alchemy.cli to info.picocli;

    uses IngredientsReader;
    provides IngredientsReader with SimpleIngredientsReader;

    uses EffectsReader;
    provides EffectsReader with SimpleEffectsReader;

    uses MixturesWriter;
    provides MixturesWriter with SimpleMixtureWriter;
}