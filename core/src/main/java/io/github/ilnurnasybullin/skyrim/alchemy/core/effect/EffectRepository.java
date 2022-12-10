package io.github.ilnurnasybullin.skyrim.alchemy.core.effect;

import io.github.ilnurnasybullin.skyrim.alchemy.core.repository.Repository;

import java.util.ServiceLoader;

public interface EffectRepository extends Repository<Effect, Integer> {

    static EffectRepository getInstance() {
        return ServiceLoader.load(EffectRepository.class)
                .findFirst()
                .orElseThrow();
    }
}
