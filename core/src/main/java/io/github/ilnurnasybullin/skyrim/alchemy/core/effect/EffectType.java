package io.github.ilnurnasybullin.skyrim.alchemy.core.effect;

public enum EffectType {
    POSITIVE("positive"),
    NEGATIVE("negative");

    private final String type;

    EffectType(String type) {
        this.type = type;
    }

    public String type() {
        return type;
    }

}
