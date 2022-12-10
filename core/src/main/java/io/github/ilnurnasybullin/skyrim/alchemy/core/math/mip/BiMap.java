package io.github.ilnurnasybullin.skyrim.alchemy.core.math.mip;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class BiMap<K1, K2> {

    private final Map<K1, K2> k1ToK2;
    private final Map<K2, K1> k2ToK1;

    public BiMap() {
        this.k1ToK2 = new HashMap<>();
        this.k2ToK1 = new HashMap<>();
    }

    public void putKeys(K1 key1, K2 key2) {
        k1ToK2.put(key1, key2);
        k2ToK1.put(key2, key1);
    }

    public int size() {
        return k1ToK2.size();
    }

    public K1 key1(K2 key2) {
        return k2ToK1.get(key2);
    }

    public K2 key2(K1 key1) {
        return k1ToK2.get(key1);
    }
    public void forEach(BiConsumer<? super K1, ? super K2> consumer) {
        k1ToK2.forEach(consumer);
    }

}
