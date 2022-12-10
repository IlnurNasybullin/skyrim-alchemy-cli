package io.github.ilnurnasybullin.skyrim.alchemy.repository.config;

import java.util.ServiceLoader;

public interface RepositoryProperties {
    KeysValue keys();

    static RepositoryProperties getInstance() {
        return ServiceLoader.load(RepositoryProperties.class)
                .findFirst()
                .orElseThrow();
    }
}
