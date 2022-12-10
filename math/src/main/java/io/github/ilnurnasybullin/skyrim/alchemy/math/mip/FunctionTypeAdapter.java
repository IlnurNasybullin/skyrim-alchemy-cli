package io.github.ilnurnasybullin.skyrim.alchemy.math.mip;

import io.github.ilnurnasybullin.math.simplex.FunctionType;

class FunctionTypeAdapter {

    public FunctionType adapt(io.github.ilnurnasybullin.skyrim.alchemy.core.math.mip.FunctionType type) {
        return switch (type) {
            case MIN -> FunctionType.MIN;
            case MAX -> FunctionType.MAX;
        };
    }

}
