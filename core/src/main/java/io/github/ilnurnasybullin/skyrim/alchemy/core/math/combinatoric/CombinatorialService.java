package io.github.ilnurnasybullin.skyrim.alchemy.core.math.combinatoric;

import java.util.ServiceLoader;
import java.util.Set;

public interface CombinatorialService {
    <T> Iterable<Set<T>> combinatorial(Set<T> elements, int k);

    static CombinatorialService getInstance() {
        return ServiceLoader.load(CombinatorialService.class)
                .findFirst()
                .orElseThrow();
    }

}
