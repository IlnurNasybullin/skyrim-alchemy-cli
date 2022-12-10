package io.github.ilnurnasybullin.skyrim.alchemy.core;

import java.util.Map;
import java.util.SplittableRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        var ingredients = new String[]{
                "Dwarven Oil", "Fire Salts", "Giant's Toe", "Creep Cluster",
                "Salt Pile", "Taproot", "Garlic", "Elves Ear"
        };

        var i = new AtomicInteger(0);
        var map = new SplittableRandom()
                .ints(ingredients.length, -10, 10)
                .mapToObj(count -> Map.entry(ingredients[i.getAndIncrement()], count))
                .filter(entry -> entry.getValue() > 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        System.out.println(map);
    }
}
