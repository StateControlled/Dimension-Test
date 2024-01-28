package com.statecontrolled.dimensiontest.world.biome;

import com.statecontrolled.dimensiontest.DimensionTest;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.WorldCarver;

/**
 * Hopefully will register custom carver so it can be used.
 **/
public class ModCarvers {
    public static WorldCarver<CaveCarverConfiguration> CUSTOM_CARVER;

    public static void registerCarvers() {
        CUSTOM_CARVER = register("custom_carver", new CustomCarver(CaveCarverConfiguration.CODEC));
    }

    private static <C extends CarverConfiguration> WorldCarver<C> register(String name, WorldCarver<C> carver) {
        Registry.register(BuiltInRegistries.CARVER, new ResourceLocation(DimensionTest.MOD_ID, name), carver);
        return carver;
    }

}
