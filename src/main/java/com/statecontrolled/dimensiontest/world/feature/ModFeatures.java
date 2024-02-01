package com.statecontrolled.dimensiontest.world.feature;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.neoforged.bus.api.IEventBus;

public class ModFeatures {
    public static final Feature<ModFeatureConfiguration> MOD_FEATURE = register("mod_feature", new ModFeature<>(ModFeatureConfiguration.CODEC));

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> F register(String key, F value) {
        return Registry.register(BuiltInRegistries.FEATURE, key, value);
    }

    public static void register(IEventBus event) {
        ;
    }

}
