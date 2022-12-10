package io.github.ilnurnasybullin.skyrim.alchemy.repository;

import io.github.ilnurnasybullin.skyrim.alchemy.core.repository.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author Ilnur Nasybullin
 * @since 08.11.2022
 */
public abstract class MapRepository<K, V> implements Repository<V, K> {

    protected Map<K, V> store;

    @Override
    public Optional<V> findById(K key) {
        return Optional.ofNullable(store.get(key));
    }

    @Override
    public List<V> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Stream<V> stream() {
        return store.values().stream();
    }
}
