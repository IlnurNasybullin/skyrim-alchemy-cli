package test.io.github.ilnurnasybullin.skyrim.alchemy.core;

import io.github.ilnurnasybullin.skyrim.alchemy.core.effect.Effect;
import io.github.ilnurnasybullin.skyrim.alchemy.core.effect.EffectRepository;
import io.github.ilnurnasybullin.skyrim.alchemy.core.effect.EffectType;
import io.github.ilnurnasybullin.skyrim.alchemy.core.ingredient.Ingredient;
import io.github.ilnurnasybullin.skyrim.alchemy.core.ingredient.IngredientRepository;
import io.github.ilnurnasybullin.skyrim.alchemy.core.mixture.Mixture;
import io.github.ilnurnasybullin.skyrim.alchemy.core.mixture.MixtureType;
import io.github.ilnurnasybullin.skyrim.alchemy.core.mixture.MostCountEffectMixtureCreator;
import io.github.ilnurnasybullin.skyrim.alchemy.core.repository.Bag;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MixtureCreatorTest {

    private static class ExpectedMixturesBuilder {

        interface IngredientAsMixture {
            IngredientAsMixture ingredient(Ingredient ingredient);
            WithType asMixture();
        }

        interface WithType {
            WithCount withType(MixtureType type);
        }

        interface WithCount {
            IngredientAsTemplate withCount(long count);
        }

        interface IngredientAsTemplate {
            IngredientAsMixture ingredient(Ingredient ingredient);
            IngredientBuild asTemplate();
        }

        interface IngredientBuild {
            IngredientAsMixture ingredient(Ingredient ingredient);
            MixtureCreatorTestData build();
        }

        private final MixtureCreatorTestData outer;
        private final Set<Ingredient> ingredients = new HashSet<>();
        private final Map<Mixture, Long> mixtures = new HashMap<>();
        private Bag.Builder<Mixture> builder = new Bag.Builder<>();
        private Bag.Builder.Add<Mixture> itemCounter;
        private final List<Bag<Mixture>> expected = new ArrayList<>();

        private ExpectedMixturesBuilder(MixtureCreatorTestData outer) {
            this.outer = outer;
        }

        public IngredientAsMixture ingredient(Ingredient ingredient) {
            ingredients.add(ingredient);
            return new IngredientAsMixtureImpl(this);
        }

        private WithType asMixture() {
            return new WithTypeImpl(this);
        }

        private WithCount withType(MixtureType type) {
            itemCounter = builder.item(Mixture.of(ingredients, type));
            ingredients.clear();
            return new WithCountImpl(this);
        }

        private IngredientAsTemplate withCount(long count) {
            builder = itemCounter.add(count);
            return new IngredientAsTemplateImpl(this);
        }

        private IngredientBuild asTemplate() {
            expected.add(builder.build());
            mixtures.clear();
            return new IngredientBuildImpl(this);
        }

        private MixtureCreatorTestData build() {
            outer.mixtures(expected);
            return outer;
        }

        private record IngredientAsMixtureImpl(ExpectedMixturesBuilder outer) implements IngredientAsMixture {

            @Override
            public IngredientAsMixture ingredient(Ingredient ingredient) {
                return outer.ingredient(ingredient);
            }

            @Override
            public WithType asMixture() {
                return outer.asMixture();
            }
        }

        private record WithTypeImpl(ExpectedMixturesBuilder outer) implements WithType {

            @Override
            public WithCount withType(MixtureType type) {
                return outer.withType(type);
            }
        }

        private record WithCountImpl(ExpectedMixturesBuilder outer) implements WithCount {

            @Override
            public IngredientAsTemplate withCount(long count) {
                return outer.withCount(count);
            }
        }

        private record IngredientAsTemplateImpl(ExpectedMixturesBuilder outer) implements IngredientAsTemplate {

            @Override
            public IngredientAsMixture ingredient(Ingredient ingredient) {
                return outer.ingredient(ingredient);
            }

            @Override
            public IngredientBuild asTemplate() {
                return outer.asTemplate();
            }
        }

        private record IngredientBuildImpl(ExpectedMixturesBuilder outer) implements IngredientBuild {

            @Override
            public IngredientAsMixture ingredient(Ingredient ingredient) {
                return this.outer.ingredient(ingredient);
            }

            @Override
            public MixtureCreatorTestData build() {
                return outer.build();
            }
        }

    }

    private static class MixtureCreatorTestData {

        private Executor executor = Runnable::run;
        private double maxWeight;
        private Set<Effect> desiredEffects;
        private Set<Effect> activatingEffects;
        private List<Bag<Mixture>> mixtures;

        private Bag.Builder<Ingredient> builder = new Bag.Builder<>();
        private Bag.Builder.Add<Ingredient> adder;

        private void mixtures(List<Bag<Mixture>> templates) {
            mixtures = templates;
        }

        private record IngredientCount(MixtureCreatorTestData outer) {
            public MixtureCreatorTestData withCount(long count) {
                return outer.withCount(count);
            }
        }

        private MixtureCreatorTestData withCount(long count) {
            builder = adder.add(count);
            return this;
        }

        public Bag<Ingredient> ingredients() {
            return builder.build();
        }

        public Executor executor() {
            return executor;
        }

        public double maxWeight() {
            return maxWeight;
        }

        public Set<Effect> desiredEffects() {
            return desiredEffects;
        }

        public Set<Effect> activatingEffects() {
            return activatingEffects;
        }

        public IngredientCount addIngredient(Ingredient ingredient) {
            adder = builder.item(ingredient);
            return new IngredientCount(this);
        }

        public List<Bag<Mixture>> expectedMixtures() {
            return mixtures;
        }

        public MixtureCreatorTestData desiredEffects(Set<Effect> desiredEffects) {
            this.desiredEffects = desiredEffects;
            return this;
        }

        public MixtureCreatorTestData activatingEffects(Set<Effect> activatingEffects) {
            this.activatingEffects = activatingEffects;
            return this;
        }

        public MixtureCreatorTestData maxWeight(double maxWeight) {
            this.maxWeight = maxWeight;
            return this;
        }

        public MixtureCreatorTestData executor(Executor executor) {
            this.executor = executor;
            return this;
        }

        public ExpectedMixturesBuilder expected() {
            return new ExpectedMixturesBuilder(this);
        }

    }

    /**
     * Тестирование создания микстур.<p>
     *
     * Тестирование осуществлялось по следующей таблице:
     * <table>
     *  <tr>
     *    <td>Ингредиенты\эффекты</td>
     *    <td style="font-weight:bold">Восстановление магии</td>
     *    <td style="font-weight:bold">Повышение навыка: иллюзия</td>
     *    <td style="font-weight:bold">Повышение переносимого веса</td>
     *    <td style="font-weight:bold">Регенерация магии</td>
     *    <td style="font-weight:bold">Сопротивление огню</td>
     *  </tr>
     *  <tr>
     *    <td style="font-weight:bold">Двемерское масло</td>
     *    <td style="text-align:center">+</td>
     *    <td style="text-align:center">+</td>
     *    <td style="text-align:center"></td>
     *    <td style="text-align:center">+</td>
     *    <td style="text-align:center"></td>
     *  </tr>
     *  <tr>
     *    <td style="font-weight:bold">Огненная соль</td>
     *    <td style="text-align:center">+</td>
     *    <td style="text-align:center"></td>
     *    <td style="text-align:center"></td>
     *    <td style="text-align:center">+</td>
     *    <td style="text-align:center">+</td>
     *  </tr>
     *  <tr>
     *    <td style="font-weight:bold">Палец великана</td>
     *    <td style="text-align:center"></td>
     *    <td style="text-align:center"></td>
     *    <td style="text-align:center">+</td>
     *    <td style="text-align:center"></td>
     *    <td style="text-align:center"></td>
     *  </tr>
     *  <tr>
     *    <td style="font-weight:bold">Ползучая лоза</td>
     *    <td style="text-align:center">+</td>
     *    <td style="text-align:center"></td>
     *    <td style="text-align:center">+</td>
     *    <td style="text-align:center"></td>
     *    <td style="text-align:center"></td>
     *  </tr>
     *  <tr>
     *    <td style="font-weight:bold">Соль</td>
     *    <td style="text-align:center"></td>
     *    <td style="text-align:center"></td>
     *    <td style="text-align:center"></td>
     *    <td style="text-align:center">+</td>
     *    <td style="text-align:center"></td>
     *  </tr>
     *  <tr>
     *    <td style="font-weight:bold">Стержневой корень</td>
     *    <td style="text-align:center">+</td>
     *    <td style="text-align:center">+</td>
     *    <td style="text-align:center"></td>
     *    <td style="text-align:center">+</td>
     *    <td style="text-align:center"></td>
     *  </tr>
     *  <tr>
     *    <td style="font-weight:bold">Чеснок</td>
     *    <td style="text-align:center"></td>
     *    <td style="text-align:center"></td>
     *    <td style="text-align:center"></td>
     *    <td style="text-align:center">+</td>
     *    <td style="text-align:center"></td>
     *  </tr>
     *  <tr>
     *    <td style="font-weight:bold">Эльфийское ухо</td>
     *    <td style="text-align:center">+</td>
     *    <td style="text-align:center"></td>
     *    <td style="text-align:center"></td>
     *    <td style="text-align:center"></td>
     *    <td style="text-align:center">+</td>
     *  </tr>
     * </table>
     */

    private static Ingredient findById(int id) {
        return IngredientRepository.getInstance()
                .findById(id)
                .orElseThrow();
    }

    private static Ingredient elvesEar() {
        return findById(0x00034d31);
    }

    private static Ingredient giantsToe() {
        return findById(0x0003ad64);
    }

    private static Ingredient fireSalts() {
        return findById(0x0003ad5e);
    }

    private static Ingredient dwarvenOil() {
        return findById(0x000f11c0);
    }

    private static Ingredient taproot() {
        return findById(0x0003ad71);
    }

    private static Ingredient creepCluster() {
        return findById(0x000b2183);
    }

    private static Set<Effect> allPositive() {
        return EffectRepository.getInstance()
                .stream()
                .filter(effect -> effect.type() == EffectType.POSITIVE)
                .collect(Collectors.toSet());
    }

    @ParameterizedTest
    @MethodSource(value = {
            "_testCreateMixture_0__success",
            "_testCreateMixture_1__success"
    })
    public void testCreateMixture__success(MixtureCreatorTestData testData) {
        var mixtureCreator = new MostCountEffectMixtureCreator();
        var createdMixtures = mixtureCreator.maxWeight(testData.maxWeight())
                .activatingEffects(testData.activatingEffects())
                .desiredEffects(testData.desiredEffects())
                .executor(testData.executor())
                .ingredients(testData.ingredients())
                .createMixturesForNpc();

        Assertions.assertThat(Set.copyOf(createdMixtures))
                .isEqualTo(Set.copyOf(testData.expectedMixtures()));
    }

    /**
     * 4 Эльфийских уха + 6 Пальцев великана + 7 Огненных солей = (4 Эльфийских уха + 4 Огненных солей)
     */
    public static Stream<Arguments> _testCreateMixture_0__success() {
        var testData = new MixtureCreatorTestData()
                .addIngredient(elvesEar())
                .withCount(4)
                .addIngredient(giantsToe())
                .withCount(6)
                .addIngredient(fireSalts())
                .withCount(7)
                .desiredEffects(allPositive())
                .activatingEffects(allPositive())
                .maxWeight(100)
                .expected()
                .ingredient(elvesEar())
                .ingredient(fireSalts())
                .asMixture()
                .withType(MixtureType.POTION)
                .withCount(4)
                .asTemplate()
                .build();

        return Stream.of(
                Arguments.of(testData)
        );
    }

    /**
     * 2 Двемерского масло + 5 Стержневых корней + 6 Пальцев великана +
     * 8 Эльфийских ушей + 7 Огненных солей + 9 Ползучих лоз =
     * 5 (Стержневых корней + Эльфийских ушей + Огненных солей) +
     * 2 (Двемерского масла + Эльфийских ушей + Огненных солей) +
     * 1 (Палец великана + Эльфийское ухо + Ползучая лоза) +
     * 5 (Пальцев великана + Ползучих лоз)
     */
    public static Stream<Arguments> _testCreateMixture_1__success() {
        var testData = new MixtureCreatorTestData()
                .addIngredient(dwarvenOil()).withCount(2)
                .addIngredient(taproot()).withCount(5)
                .addIngredient(giantsToe()).withCount(6)
                .addIngredient(elvesEar()).withCount(8)
                .addIngredient(fireSalts()).withCount(7)
                .addIngredient(creepCluster()).withCount(9)
                .desiredEffects(allPositive())
                .activatingEffects(allPositive())
                .maxWeight(100)
                .expected()
                .ingredient(taproot()).ingredient(elvesEar()).ingredient(fireSalts())
                .asMixture().withType(MixtureType.POTION).withCount(5)
                .ingredient(dwarvenOil()).ingredient(elvesEar()).ingredient(fireSalts())
                .asMixture().withType(MixtureType.POTION).withCount(2)
                .ingredient(giantsToe()).ingredient(elvesEar()).ingredient(creepCluster())
                .asMixture().withType(MixtureType.POTION).withCount(1)
                .ingredient(giantsToe()).ingredient(creepCluster())
                .asMixture().withType(MixtureType.POTION).withCount(5)
                .asTemplate()
                .build();

        return Stream.of(
                Arguments.of(testData)
        );
    }

}
