package com.statecontrolled.dimensiontest.world.feature;

import java.util.List;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class ModFeatureConfiguration implements FeatureConfiguration {
    public static final Codec<ModFeatureConfiguration> CODEC =
        RecordCodecBuilder.create(
            (instance) -> instance.group(
                ResourceLocation.CODEC.fieldOf("processors").forGetter((config) -> config.processor),
                ResourceLocation.CODEC.fieldOf("post_processors")
                    .orElse(new ResourceLocation("minecraft:empty")).forGetter((config) -> config.postProcessor),
                Codec.mapPair(ResourceLocation.CODEC.fieldOf("resourcelocation"), ExtraCodecs.POSITIVE_INT.fieldOf("weight"))
                    .codec().listOf().fieldOf("nbt_entries").forGetter((config) -> config.nbtResourcelocationsAndWeights)
            ).apply(instance, ModFeatureConfiguration::new)
        );

    public final List<Pair<ResourceLocation, Integer>> nbtResourcelocationsAndWeights;
    public final ResourceLocation processor;
    public final ResourceLocation postProcessor;

    public ModFeatureConfiguration(ResourceLocation processor, ResourceLocation postProcessor,
                                   List<Pair<ResourceLocation, Integer>> nbtIdentifiersAndWeights) {

        this.nbtResourcelocationsAndWeights = nbtIdentifiersAndWeights;
        this.processor = processor;
        this.postProcessor = postProcessor;
    }
}
