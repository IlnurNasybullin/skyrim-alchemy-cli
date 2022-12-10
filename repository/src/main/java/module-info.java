import io.github.ilnurnasybullin.skyrim.alchemy.core.effect.EffectRepository;
import io.github.ilnurnasybullin.skyrim.alchemy.core.ingredient.IngredientRepository;
import io.github.ilnurnasybullin.skyrim.alchemy.repository.config.RepositoryProperties;
import io.github.ilnurnasybullin.skyrim.alchemy.repository.config.RepositoryPropertiesImpl;
import io.github.ilnurnasybullin.skyrim.alchemy.repository.csv.CsvReader;
import io.github.ilnurnasybullin.skyrim.alchemy.repository.csv.CsvReaderImpl;
import io.github.ilnurnasybullin.skyrim.alchemy.repository.effect.EffectPropertyRepository;
import io.github.ilnurnasybullin.skyrim.alchemy.repository.effect.EffectPropertyRepositoryImpl;
import io.github.ilnurnasybullin.skyrim.alchemy.repository.effect.EffectRepositoryImpl;
import io.github.ilnurnasybullin.skyrim.alchemy.repository.ingredient.IngredientPropertyRepository;
import io.github.ilnurnasybullin.skyrim.alchemy.repository.ingredient.IngredientPropertyRepositoryImpl;
import io.github.ilnurnasybullin.skyrim.alchemy.repository.ingredient.IngredientRepositoryImpl;

open module io.github.ilnurnasybullin.skyrim.alchemy.repository {
    requires io.github.ilnurnasybullin.skyrim.alchemy.core;

    uses CsvReader;
    provides CsvReader with CsvReaderImpl;

    uses RepositoryProperties;
    provides RepositoryProperties with RepositoryPropertiesImpl;

    uses EffectPropertyRepository;
    provides EffectPropertyRepository with EffectPropertyRepositoryImpl;

    provides EffectRepository with EffectRepositoryImpl;

    uses IngredientPropertyRepository;
    provides IngredientPropertyRepository with IngredientPropertyRepositoryImpl;

    provides IngredientRepository with IngredientRepositoryImpl;

    exports io.github.ilnurnasybullin.skyrim.alchemy.repository.ingredient;
}