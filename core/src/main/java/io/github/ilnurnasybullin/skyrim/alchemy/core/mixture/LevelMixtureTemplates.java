package io.github.ilnurnasybullin.skyrim.alchemy.core.mixture;

import io.github.ilnurnasybullin.skyrim.alchemy.core.ingredient.Ingredient;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LevelMixtureTemplates {

    private final TreeMap<Integer, LevelMixtureTemplate> templates;

    private LevelMixtureTemplates(TreeMap<Integer, LevelMixtureTemplate> templates) {
        this.templates = templates;
    }

    public LevelMixtureTemplates(Map<Integer, LevelMixtureTemplate> templates) {
        this.templates = new TreeMap<>(Comparator.reverseOrder());
        this.templates.putAll(templates);
    }

    public boolean hasNext() {
        return !templates.isEmpty();
    }

    public LevelMixtureTemplate getAndRemove() {
        var level = templates.firstKey();
        return templates.remove(level);
    }

    public LevelMixtureTemplates retainAll(Set<Ingredient> ingredients) {
        var clearTemplates = templates.values()
                .stream()
                .map(template -> template.retainAll(ingredients))
                .flatMap(Optional::stream)
                .collect(Collectors.toMap(
                        LevelMixtureTemplate::level, Function.identity(),
                        (value1, value2) -> value1, () -> new TreeMap<>(Comparator.reverseOrder())
                ));

        return new LevelMixtureTemplates(clearTemplates);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LevelMixtureTemplates that = (LevelMixtureTemplates) o;
        return Objects.equals(templates, that.templates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(templates);
    }
}
