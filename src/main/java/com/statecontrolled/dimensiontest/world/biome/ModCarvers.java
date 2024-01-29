package com.statecontrolled.dimensiontest.world.biome;

import com.statecontrolled.dimensiontest.DimensionTest;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Hopefully will register custom carver so it can be used.
 **/
public class ModCarvers {
    public static final DeferredRegister<WorldCarver<?>> CUSTOM_CARVER_REG =
            DeferredRegister.create(Registries.CARVER, DimensionTest.MOD_ID);

    public static final DeferredHolder<WorldCarver<?>, CustomCarver> CUSTOM_CARVER =
            CUSTOM_CARVER_REG.register("custom_carver", () -> new CustomCarver(CaveCarverConfiguration.CODEC));

    public static void register(IEventBus event) {
        CUSTOM_CARVER_REG.register(event);
    }

//    public static WorldCarver<CaveCarverConfiguration> CUSTOM_CARVER;
//
//    public static void registerCarvers() {
//        CUSTOM_CARVER = register("custom_carver", new CustomCarver(CaveCarverConfiguration.CODEC));
//    }
//
//    private static <C extends CarverConfiguration> WorldCarver<C> register(String name, WorldCarver<C> carver) {
//        Registry.register(BuiltInRegistries.CARVER, new ResourceLocation(DimensionTest.MOD_ID, name), carver);
//        return carver;
//    }

}
