package io.github.ilnurnasybullin.skyrim.alchemy.repository;

import java.util.Map;
import java.util.Optional;

/**
 * @author Ilnur Nasybullin
 * @since 08.11.2022
 */
public abstract class MapPropertyRepository<K, V> {

    private final Map<K, V> properties;

    public MapPropertyRepository(String filename) {
        this.properties = readProperties(filename);
    }

    protected abstract Map<K, V> readProperties(String filename);

    public Optional<V> property(K id) {
        return Optional.ofNullable(properties.get(id));
    }
}
