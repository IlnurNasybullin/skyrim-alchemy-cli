module io.github.ilnurnasybullin.skyrim.core.test {
    requires io.github.ilnurnasybullin.skyrim.alchemy.core;
    requires io.github.ilnurnasybullin.skyrim.alchemy.math;
    requires io.github.ilnurnasybullin.skyrim.alchemy.repository;

    requires org.junit.jupiter.api;
    requires org.apiguardian.api;
    requires org.junit.jupiter.params;
    requires org.assertj.core;

    exports test.io.github.ilnurnasybullin.skyrim.alchemy.core;
}