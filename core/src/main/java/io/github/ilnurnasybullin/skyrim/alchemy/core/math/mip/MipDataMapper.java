package io.github.ilnurnasybullin.skyrim.alchemy.core.math.mip;

import io.github.ilnurnasybullin.skyrim.alchemy.core.ingredient.Ingredient;
import io.github.ilnurnasybullin.skyrim.alchemy.core.mixture.Mixture;
import io.github.ilnurnasybullin.skyrim.alchemy.core.mixture.MixtureTemplate;
import io.github.ilnurnasybullin.skyrim.alchemy.core.repository.Bag;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

public class MipDataMapper {

    private BiMap<Ingredient, Integer> ingredientMapper;
    private BiMap<MixtureTemplate, Integer> templateMapper;
    private Function<Ingredient, Long> bFunction;

    private double[][] a;
    private double[] b;
    private Inequality[] inequalities;
    private double[] c;
    private FunctionType type;
    private double maxWeight;

    public MipDataMapper ingredients(Collection<Ingredient> ingredients) {
        ingredientMapper = new BiMap<>();
        List<Ingredient> shuffle = new ArrayList<>(ingredients);
        Collections.shuffle(shuffle, ThreadLocalRandom.current());

        int i = 0;
        for (var ingredient: shuffle) {
            ingredientMapper.putKeys(ingredient, i);
            i++;
        }

        return this;
    }

    public MipDataMapper ingredientCounter(Function<Ingredient, Long> bFunction) {
        this.bFunction = bFunction;
        return this;
    }

    public MipDataMapper type(FunctionType type) {
        this.type = type;
        return this;
    }

    public MipDataMapper templates(Collection<MixtureTemplate> mixtures) {
        templateMapper = new BiMap<>();
        List<MixtureTemplate> shuffle = new ArrayList<>(mixtures);
        Collections.shuffle(shuffle, ThreadLocalRandom.current());

        int i = 0;
        for (var template: shuffle) {
            templateMapper.putKeys(template, i);
            i++;
        }

        return this;
    }

    public MipDataMapper maxWeight(double maxWeight) {
        this.maxWeight = maxWeight;
        return this;
    }

    public MipDataMapper resolve() {
        int ingredientsCount = ingredientMapper.size();
        int mixturesCount = templateMapper.size();

        a = new double[ingredientsCount + 1][mixturesCount];
        templateMapper.forEach((mixture, mixtureIndex) -> {
            mixture.ingredients().forEach(ingredient -> {
                try {
                    var ingredientIndex = ingredientMapper.key2(ingredient);
                    a[ingredientIndex][mixtureIndex] = 1;
                } catch (NullPointerException e) {
                    throw new RuntimeException(e);
                }
            });
        });
        Arrays.fill(a[ingredientsCount], 1);

        b = new double[ingredientsCount + 1];
        ingredientMapper.forEach((ingredient, index) -> {
            b[index] = bFunction.apply(ingredient);
        });
        b[ingredientsCount] = (int) (maxWeight / Mixture.DEFAULT_WEIGHT);

        c = new double[mixturesCount];
        Arrays.fill(c, 1);

        inequalities = new Inequality[ingredientsCount + 1];
        Arrays.fill(inequalities, Inequality.LQ);

        return this;
    }

    public FunctionType type() {
        return type;
    }

    public double[][] a() {
        return a;
    }

    public double[] b() {
        return b;
    }

    public double[] c() {
        return c;
    }

    public Inequality[] inequalities() {
        return inequalities;
    }

    public Bag<Mixture> mixtures(long[] x) {
        var mixtures = new HashMap<Mixture, Long>();
        for (int i = 0; i < x.length; i++) {
            if (x[i] == 0) {
                continue;
            }

            var template = templateMapper.key1(i);
            var mixture = new Mixture.Builder()
                    .ingredients(template.ingredients())
                    .type(template.type())
                    .build();

            mixtures.put(mixture, x[i]);
        }

        return new Bag<>(mixtures);
    }

}
