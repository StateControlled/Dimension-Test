package com.statecontrolled.dimensiontest.world.biome;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.mojang.serialization.Codec;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;

public record ModCarvers(HolderSet<Biome> biomes, HolderSet<ConfiguredWorldCarver<?>> carvers, GenerationStep.Decoration step)
        implements BiomeModifier {

    public static List<Holder<ConfiguredWorldCarver<?>>> ADD_NOISE_FEATURES = new ArrayList<>();

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        List<Holder<ConfiguredWorldCarver<?>>> addedCarvers = new ArrayList<>();

    }

    @Override
    public Codec<? extends BiomeModifier> codec() {
        return null;
    }




}
