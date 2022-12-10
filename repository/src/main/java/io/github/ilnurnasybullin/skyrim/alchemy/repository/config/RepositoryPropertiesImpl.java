package io.github.ilnurnasybullin.skyrim.alchemy.repository.config;

import java.io.IOException;
import java.util.*;

public class RepositoryPropertiesImpl implements RepositoryProperties {

    private final static RepositoryPropertiesImpl INSTANCE = new RepositoryPropertiesImpl();

    private final Map<Object, Object> properties;

    public RepositoryPropertiesImpl() {
        this("/repositories.properties");
    }

    public RepositoryPropertiesImpl(String filename) {
        this.properties = readProperties(filename);
    }

    private Map<Object, Object> readProperties(String filename) {
        Properties properties = new Properties();
        try {
            final var mapBuilder = new MapBuilder();
            Map<Object, Object> mapProperties = new HashMap<>();
            properties.load(getClass().getResourceAsStream(filename));
            properties.forEach((key, value) -> {
                var stringKey = (String) key;
                Arrays.stream(stringKey.split("\\."))
                        .forEach(mapBuilder::key);
                mapBuilder.value(value);
            });

            return mapBuilder.map();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static RepositoryProperties provider() {
        return INSTANCE;
    }

    @Override
    public KeysValue keys() {
        return new MapKeysValue(properties);
    }

    public static class MapKeysValue implements KeysValue {

        private Map<?, ?> currentMap;

        public MapKeysValue(Map<Object, Object> properties) {
            currentMap = properties;
        }

        @Override
        public KeysValue key(String key) {
            var value = currentMap.get(key);
            if (value instanceof Map<?,?> map) {
                currentMap = map;
                return this;
            }

            throw new IllegalArgumentException(
                    String.format("Key %s is not exist!", key)
            );
        }

        @Override
        public Optional<String> value(String key) {
            var value = currentMap.get(key);
            if (value instanceof String string) {
                return Optional.of(string);
            }

            return Optional.empty();
        }
    }

    interface Value {
        Key key(Object key);
    }

    interface Key {
        Key key(Object key);
        Value value(Object value);
        Map<Object, Object> map();
    }

    private static class ValueImpl implements Value {

        private final Key outer;

        private ValueImpl(Key outer) {
            this.outer = outer;
        }

        @Override
        public Key key(Object key) {
            return outer.key(key);
        }
    }

    private static class MapBuilder implements Key {

        private final Map<Object, Object> root = new HashMap<>();
        private Map<Object, Object> current = root;
        private Object prevKey = null;

        @Override
        public Key key(Object key) {
            if (key == null) {
                throw new NullPointerException();
            }

            if (this.prevKey == null) {
                this.prevKey = key;
                return this;
            }

            current = (Map<Object, Object>) current.computeIfAbsent(prevKey, k -> new HashMap<>());
            prevKey = key;
            return this;
        }

        @Override
        public Value value(Object value) {
            current.put(prevKey, value);
            current = root;
            prevKey = null;
            return new ValueImpl(this);
        }

        @Override
        public Map<Object, Object> map() {
            return root;
        }
    }
}
