package io.github.ilnurnasybullin.skyrim.alchemy.core.mixture;

import io.github.ilnurnasybullin.skyrim.alchemy.core.repository.Bag;

public record MixtureWeight(double weight) {
    public MixtureWeight recalculateMaxWeight(Bag<Mixture> mixtures) {
        var recalculatedWeight = weight - mixtures.totalCount() * Mixture.DEFAULT_WEIGHT;
        return new MixtureWeight(recalculatedWeight);
    }

    public boolean canCreateMixture() {
        return Mixture.DEFAULT_WEIGHT <= weight;
    }
}
