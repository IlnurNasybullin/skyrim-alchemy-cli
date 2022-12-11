package io.github.ilnurnasybullin.skyrim.alchemy.cli;

import io.github.ilnurnasybullin.skyrim.alchemy.core.effect.Effect;
import io.github.ilnurnasybullin.skyrim.alchemy.core.effect.EffectRepository;
import picocli.CommandLine.Command;

import java.util.Comparator;

@Command(name = "print-effects")
public class EffectsPrinter implements Runnable {

    @Override
    public void run() {
        EffectRepository.getInstance()
                .stream()
                .sorted(Comparator.comparing(Effect::name))
                .forEach(this::printEffect);
    }

    private void printEffect(Effect effect) {
        System.out.printf("%s [%s]%n", effect.name(), hexId(effect.id()));
    }

    private String hexId(int id) {
        return Integer.toHexString(id);
    }
}
