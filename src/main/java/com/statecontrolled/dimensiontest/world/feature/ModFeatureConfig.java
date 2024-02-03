package com.statecontrolled.dimensiontest.world.feature;

import java.util.List;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class ModFeatureConfig implements FeatureConfiguration {
    public static final Codec<ModFeatureConfig> CODEC = RecordCodecBuilder.create((configInstance) -> configInstance.group(
        Codec.mapPair(
            ResourceLocation.CODEC.fieldOf("resourcelocation"),
            ExtraCodecs.POSITIVE_INT.fieldOf("weight")
        ).codec().listOf().fieldOf("nbt_entries").forGetter((config) -> config.options)
    ).apply(configInstance, ModFeatureConfig::new));

    public final List<Pair<ResourceLocation, Integer>> options;

    public ModFeatureConfig(List<Pair<ResourceLocation, Integer>> options) {
        this.options = options;
    }

    public List<Pair<ResourceLocation, Integer>> getOptions() {
        return options;
    }

}
