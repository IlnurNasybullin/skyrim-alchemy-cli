package io.github.ilnurnasybullin.skyrim.alchemy.core.math.mip;

public enum Inequality {
    LE("<"),
    LQ("<="),
    EQ("="),
    GE(">="),
    GR(">");

    private final String expression;

    Inequality(String expression) {
        this.expression = expression;
    }

    public String expression() {
        return expression;
    }
}
