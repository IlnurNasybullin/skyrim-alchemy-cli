package io.github.ilnurnasybullin.skyrim.alchemy.repository.config;

import java.util.Optional;

public interface KeysValue {
    KeysValue key(String key);
    Optional<String> value(String key);
}
