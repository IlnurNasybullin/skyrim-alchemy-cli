package io.github.ilnurnasybullin.skyrim.alchemy.core.effect;

import lombok.Builder;

import java.util.Objects;

public record Effect(int id, String name, EffectType type) {

    @Builder
    public static Effect of(int id, String name, EffectType effectType) {
        Objects.requireNonNull(name, "Name of effect is cannot be null!");
        Objects.requireNonNull(effectType, "Type of effect is cannot be null!");

        return new Effect(id, name, effectType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Effect effect = (Effect) o;
        return id == effect.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name;
    }
}
