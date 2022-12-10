package io.github.ilnurnasybullin.skyrim.alchemy.repository.effect;

import java.util.Optional;
import java.util.ServiceLoader;

public interface EffectPropertyRepository {
    Optional<String> property(Integer id);

    static EffectPropertyRepository getInstance() {
        return ServiceLoader.load(EffectPropertyRepository.class)
                .findFirst()
                .orElseThrow();
    }
}
