package io.github.ilnurnasybullin.skyrim.alchemy.core.mixture;

import io.github.ilnurnasybullin.skyrim.alchemy.core.ingredient.Ingredient;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class LevelMixtureTemplate {

    private final List<MixtureTemplate> templates;
    private final int level;

    public LevelMixtureTemplate(List<MixtureTemplate> templates, int level) {
        this.templates = List.copyOf(templates);
        this.level = level;
    }

    public List<MixtureTemplate> templates() {
        return templates;
    }

    public int level() {
        return level;
    }

    public Optional<LevelMixtureTemplate> retainAll(Set<Ingredient> ingredients) {
        var clearTemplates = templates.stream()
                .map(template -> template.retainAll(ingredients))
                .flatMap(Optional::stream)
                .toList();

        if (clearTemplates.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new LevelMixtureTemplate(clearTemplates, level));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LevelMixtureTemplate that = (LevelMixtureTemplate) o;
        return level == that.level && Objects.equals(templates, that.templates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(templates, level);
    }

    public static class Builder {

        private List<MixtureTemplate> templates;
        private int level;

        public Builder templates(List<MixtureTemplate> templates) {
            this.templates = templates;
            return this;
        }

        public Builder level(int level) {
            this.level = level;
            return this;
        }

        public LevelMixtureTemplate build() {
            return new LevelMixtureTemplate(templates, level);
        }

    }
}
