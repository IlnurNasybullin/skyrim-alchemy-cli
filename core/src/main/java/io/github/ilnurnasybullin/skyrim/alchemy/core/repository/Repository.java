package io.github.ilnurnasybullin.skyrim.alchemy.core.repository;

import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Stream;

/**
 * @author Ilnur Nasybullin
 * @since 08.11.2022
 */
public interface Repository<T, ID> {
    Optional<T> findById(ID id);
    void init(ResourceBundle resourceBundle);
    List<T> findAll();
    Stream<T> stream();
}
