package io.github.ilnurnasybullin.skyrim.alchemy.core.repository;

import java.util.*;
import java.util.function.Predicate;

public class Bag<T> {

    private final Map<T, Long> withCount;

    public Bag(Map<T, Long> withCount) {
        this.withCount = Map.copyOf(withCount);
    }

    public static <T> Bag<T> empty() {
        return new Bag<>(Map.of());
    }

    public boolean isEmpty() {
        return withCount.isEmpty();
    }

    public Bag<T> removeIfItem(Predicate<T> isRemove) {
        var copy = new HashMap<>(withCount);
        copy.keySet().removeIf(isRemove);

        return new Bag<>(copy);
    }

    public Set<T> items() {
        return withCount.keySet();
    }

    public long totalCount() {
        return withCount.values()
                .stream()
                .mapToLong(value -> value)
                .sum();
    }

    public Map<T, Long> map() {
        return withCount;
    }

    /**
     * Changes is not visible for this object
     */
    public Map<T, Long> modifiableMap() {
        return new HashMap<>(withCount);
    }

    public Bag<T> sum(Bag<T> other) {
        var sumMap = modifiableMap();
        other.withCount.forEach((element, count) -> {
            sumMap.compute(element, (key, oldValue) -> {
                if (oldValue == null) {
                    return count;
                } else {
                    return oldValue + count;
                }
            });
        });

        return new Bag<>(sumMap);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bag<?> bag = (Bag<?>) o;
        return Objects.equals(withCount, bag.withCount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(withCount);
    }

    @Override
    public String toString() {
        var builder = new StringBuilder("{\"Item map\": [");
        if (!withCount.isEmpty()) {
            var iterator = withCount.entrySet().iterator();
            var entry = iterator.next();
            builder.append(entryRecord(entry));

            while (iterator.hasNext()) {
                entry = iterator.next();
                builder.append(",")
                        .append(entryRecord(entry));
            }
        }
        builder.append("]}");
        return builder.toString();
    }

    private String entryRecord(Map.Entry<T, Long> entry) {
        return String.format("{\"item\": %s, \"count\": %d}", entry.getKey(), entry.getValue());
    }

    public Optional<Long> getByItem(T item) {
        return Optional.ofNullable(withCount.get(item));
    }

    interface ItemBuild<T> {
        Builder.Add<T> item(T item);
        Bag<T> build();
    }

    public static class Builder<T> implements ItemBuild<T> {

        private T item;
        private final Map<T, Long> withCount = new HashMap<>();

        @Override
        public Add<T> item(T item) {
            this.item = item;
            return new AddImpl<>(this);
        }

        private Builder<T> add(long count) {
            withCount.compute(item, (key, oldValue) -> {
                if (oldValue == null) {
                    return count;
                } else {
                    return Math.addExact(oldValue, count);
                }
            });
            item = null;
            return this;
        }

        @Override
        public Bag<T> build() {
            return new Bag<>(withCount);
        }

        public interface Add<T> {
            Builder<T> add(long count);
        }

        private static class AddImpl<T> implements Add<T> {

            private final Builder<T> outer;

            private AddImpl(Builder<T> outer) {
                this.outer = outer;
            }

            @Override
            public Builder<T> add(long count) {
                return outer.add(count);
            }
        }

    }
}
