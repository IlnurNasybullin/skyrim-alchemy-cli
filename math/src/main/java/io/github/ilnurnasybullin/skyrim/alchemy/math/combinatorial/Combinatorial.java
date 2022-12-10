package io.github.ilnurnasybullin.skyrim.alchemy.math.combinatorial;

import io.github.ilnurnasybullin.skyrim.alchemy.core.math.combinatoric.CombinatorialService;
import io.github.ilnurnasybullin.math.combinations.Combination;

import java.util.Set;

public class Combinatorial implements CombinatorialService {

    @Override
    public <T> Iterable<Set<T>> combinatorial(Set<T> elements, int k) {
        return Combination.unordered(elements, k);
    }
}
