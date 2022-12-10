package io.github.ilnurnasybullin.skyrim.alchemy.math.mip;

import io.github.ilnurnasybullin.math.simplex.Inequality;

class InequalityAdapter {

    public Inequality adapt(io.github.ilnurnasybullin.skyrim.alchemy.core.math.mip.Inequality inequality) {
        return switch (inequality) {
            case LE -> Inequality.LE;
            case LQ -> Inequality.LQ;
            case EQ -> Inequality.EQ;
            case GE -> Inequality.GE;
            case GR -> Inequality.GR;
        };
    }

}
