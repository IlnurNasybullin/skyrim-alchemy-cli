package io.github.ilnurnasybullin.skyrim.alchemy.core.mixture;

import io.github.ilnurnasybullin.skyrim.alchemy.core.math.mip.MipDataMapper;
import io.github.ilnurnasybullin.skyrim.alchemy.core.math.mip.MipSolution;
import io.github.ilnurnasybullin.skyrim.alchemy.core.repository.Bag;

public record MixtureSolution(CreatingParameters parameters, MipDataMapper dataMapper, MipSolution solution,
                              Bag<Mixture> createdMixtures) {
}
