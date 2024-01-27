package com.statecontrolled.dimensiontest.world.biome;

import java.util.function.BiConsumer;

import com.statecontrolled.dimensiontest.DimensionTest;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.WorldCarver;

public class ModCarvers {
//    public static final DeferredRegister<WorldCarver<?>> WORLD_CARVERS =
//            DeferredRegister.create(Registries.CARVER, DimensionTest.MOD_ID);

//    public static final DeferredHolder<WorldCarver<?>, CustomCarver> CUSTOM_CARVER =
//            WORLD_CARVERS.register("custom_carver", () -> new CustomCarver(CaveCarverConfiguration.CODEC));

//    public static final Supplier<CustomCarver> CUSTOM_CARVER =
//            WORLD_CARVERS.register("custom_carver", () -> new CustomCarver(CaveCarverConfiguration.CODEC));

//    public static void register(IEventBus event) {
//        WORLD_CARVERS.register(event);
//    }

    public static WorldCarver<CaveCarverConfiguration> CUSTOM_CARVER;

    public static void registerCarvers(BiConsumer<ResourceLocation, WorldCarver<?>> consumer) {
        CUSTOM_CARVER = register(consumer, "custom_carver", new CustomCarver(CaveCarverConfiguration.CODEC));
    }

    private static <C extends CarverConfiguration> WorldCarver<C> register(
            BiConsumer<ResourceLocation, WorldCarver<?>> consumer, String name, WorldCarver<C> carver) {

        consumer.accept(new ResourceLocation(DimensionTest.MOD_ID, name), carver);
        return carver;
    }

}
