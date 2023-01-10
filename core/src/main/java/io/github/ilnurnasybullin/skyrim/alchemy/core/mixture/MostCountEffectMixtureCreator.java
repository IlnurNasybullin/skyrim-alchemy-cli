package io.github.ilnurnasybullin.skyrim.alchemy.core.mixture;

import io.github.ilnurnasybullin.skyrim.alchemy.core.concurrent.TasksSolver;
import io.github.ilnurnasybullin.skyrim.alchemy.core.effect.Effect;
import io.github.ilnurnasybullin.skyrim.alchemy.core.effect.EffectType;
import io.github.ilnurnasybullin.skyrim.alchemy.core.ingredient.Ingredient;
import io.github.ilnurnasybullin.skyrim.alchemy.core.math.combinatoric.CombinatorialService;
import io.github.ilnurnasybullin.skyrim.alchemy.core.math.mip.FunctionType;
import io.github.ilnurnasybullin.skyrim.alchemy.core.math.mip.MipDataMapper;
import io.github.ilnurnasybullin.skyrim.alchemy.core.math.mip.MipSolution;
import io.github.ilnurnasybullin.skyrim.alchemy.core.math.mip.MipSolver;
import io.github.ilnurnasybullin.skyrim.alchemy.core.repository.Bag;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Class for creating mixtures with most count of effects in mixtures
 */
public class MostCountEffectMixtureCreator implements MixtureCreator {

    private Set<Effect> activatingEffects;
    private Set<Effect> desiredEffects;
    private MixtureWeight mixturesWeight;
    private Bag<Ingredient> ingredients;

    @Override
    public MixtureCreator activatingEffects(Set<Effect> effects) {
        this.activatingEffects = Set.copyOf(effects);
        return this;
    }

    @Override
    public MixtureCreator desiredEffects(Set<Effect> effects) {
        this.desiredEffects = Set.copyOf(effects);
        return this;
    }

    @Override
    public MixtureCreator maxWeight(double maxWeight) {
        this.mixturesWeight = new MixtureWeight(maxWeight);
        return this;
    }

    @Override
    public MixtureCreator ingredients(Bag<Ingredient> ingredients) {
        this.ingredients = ingredients;
        return this;
    }

    @Override
    public Bag<Mixture> createMixturesForNpc() {
        // extra cases
        if (!mixturesWeight.canCreateMixture()) {
            return Bag.empty();
        }

        if (typeOfRequiredEffects() != typeOfDesiredEffects()) {
            return Bag.empty();
        }

        var ingredients = new IngredientsRemover(this.ingredients)
                .removeIf(this::hasNotAnyRequiredEffect)
                .orIf(this::hasNotAnyDesiredEffect)
                .remove();

        if (ingredients.isEmpty()) {
            return Bag.empty();
        }

        var splitByEffectsCount = new MixtureTemplatesSplitter(ingredients)
                        .splitByEffectsCount();

        CreatingParameters creatingData = new CreatingParameters.Builder()
                .templates(splitByEffectsCount)
                .ingredients(ingredients)
                .weight(mixturesWeight)
                .build();

        Optional<MixtureSolution> solution = new TasksSolver<CreatingParameters, MixtureSolution>()
                .solver(this::solve)
                .nextTask(this::nextTask)
                .solve(creatingData);

        if (solution.isEmpty()) {
            return Bag.empty();
        }

        return createFromAnswer(solution.get());
    }

    private Bag<Mixture> createFromAnswer(MixtureSolution solution) {
        return solution.parameters()
                .createdMixtures()
                .sum(solution.createdMixtures());
    }

    private boolean hasNotAnyRequiredEffect(Ingredient ingredient) {
        return !ingredient.hasAnyEffect(activatingEffects);
    }

    private boolean hasNotAnyDesiredEffect(Ingredient ingredient) {
        return !ingredient.hasAnyEffect(desiredEffects);
    }

    private CreatingParameters nextTask(MixtureSolution solution) {
        return solution.parameters()
                .recalculate(solution.createdMixtures());
    }

    private Optional<MixtureSolution> solve(CreatingParameters parameters) {
        if (!parameters.weight().canCreateMixture()) {
            return Optional.empty();
        }

        var templates = parameters.templates();
        if (!templates.hasNext()) {
            return Optional.empty();
        }

        var template = templates.getAndRemove();
        MipDataMapper dataMapper = new MipDataMapper()
                .ingredients(parameters.ingredients().items())
                .templates(template.templates())
                .ingredientCounter(ingredient ->
                        parameters.ingredients()
                                .getByItem(ingredient)
                                .orElse(0L)
                )
                .maxWeight(parameters.weight().weight())
                .type(FunctionType.MAX)
                .resolve();

        MipSolution solution = MipSolver.getInstance()
                .a(dataMapper.a())
                .b(dataMapper.b())
                .c(dataMapper.c())
                .functionType(dataMapper.type())
                .inequalities(dataMapper.inequalities())
                .solve();

        var x = solution.x();
        var createdMixtures = dataMapper.mixtures(x);

        return Optional.of(new MixtureSolution(parameters, dataMapper, solution, createdMixtures));
    }

    private EffectType typeOfDesiredEffects() {
        return anyType(desiredEffects);
    }

    private EffectType anyType(Set<Effect> effects) {
        return effects.stream()
                .findAny()
                .map(Effect::type)
                .orElseThrow();
    }

    private EffectType typeOfRequiredEffects() {
        return anyType(activatingEffects);
    }

    private static <T> boolean hasIntersection(Collection<T> c1, Collection<T> c2) {
        return !Collections.disjoint(c1, c2);
    }

    interface RemoveIf {
        IngredientsRemover.OrIf removeIf(Predicate<Ingredient> isRemove);
    }

    private static class IngredientsRemover implements RemoveIf {

        interface OrIf {
            OrIf orIf(Predicate<Ingredient> isRemove);
            Bag<Ingredient> remove();
        }

        private static class OrIfImpl implements OrIf {

            private final IngredientsRemover outer;

            private OrIfImpl(IngredientsRemover outer) {
                this.outer = outer;
            }

            @Override
            public OrIf orIf(Predicate<Ingredient> isRemove) {
                return outer.orIf(isRemove);
            }

            @Override
            public Bag<Ingredient> remove() {
                return outer.remove();
            }
        }

        private final Bag<Ingredient> bag;
        private Predicate<Ingredient> isRemove;

        private IngredientsRemover(Bag<Ingredient> bag) {
            this.bag = bag;
        }

        @Override
        public OrIf removeIf(Predicate<Ingredient> isRemove) {
            this.isRemove = isRemove;
            return new OrIfImpl(this);
        }

        private OrIf orIf(Predicate<Ingredient> orRemove) {
            isRemove = isRemove.or(orRemove);
            return new OrIfImpl(this);
        }

        private Bag<Ingredient> remove() {
            return bag.removeIfItem(isRemove);
        }

    }

    private class MixtureTemplatesSplitter {

        private final Bag<Ingredient> ingredients;

        private MixtureTemplatesSplitter(Bag<Ingredient> ingredients) {
            this.ingredients = ingredients;
        }

        public LevelMixtureTemplates splitByEffectsCount() {
            var effectType = anyType(desiredEffects);
            var mixtureType = effectType == EffectType.POSITIVE ? MixtureType.POTION : MixtureType.POISON;

            var leveledTemplates = new HashMap<Integer, List<MixtureTemplate>>();
            var combinationGenerator = CombinatorialService.getInstance();

            var ingredients = this.ingredients.items();

            for (int k = Mixture.MIN_INGREDIENTS_COUNT;  k <= Mixture.MAX_INGREDIENTS_COUNT && k <= ingredients.size(); k++) {
                for (Set<Ingredient> combination: combinationGenerator.combinatorial(ingredients, k)) {
                    var effects = MixtureTemplate.commonEffectsBuilder()
                            .ingredients(combination)
                            .isUsefulEffects(this::isUsefulEffects)
                            .isUsefulEffect(this::isUsefulEffect)
                            .mixtureType(mixtureType)
                            .tryBuild();

                    if (!effects.isEmpty()) {
                        leveledTemplates
                                .computeIfAbsent(effects.size(), key -> new ArrayList<>())
                                .add(new MixtureTemplate(combination, mixtureType));
                    }
                }
            }

            var templates = leveledTemplates.entrySet()
                    .stream()
                    .map(entry -> new LevelMixtureTemplate(entry.getValue(), entry.getKey()))
                    .collect(Collectors.toMap(
                            LevelMixtureTemplate::level, Function.identity(), (value1, value2) -> value2,
                            () -> new TreeMap<>(Comparator.reverseOrder())
                    ));

            return new LevelMixtureTemplates(templates);
        }


        private boolean isUsefulEffects(Set<Effect> effects) {
            return hasIntersection(effects, activatingEffects) &&
                    hasIntersection(effects, desiredEffects);
        }

        private boolean isUsefulEffect(Effect effect) {
            return desiredEffects.contains(effect) ||
                    activatingEffects.contains(effect);
        }

    }
}
