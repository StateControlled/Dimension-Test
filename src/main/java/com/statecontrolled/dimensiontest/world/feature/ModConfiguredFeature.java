package com.statecontrolled.dimensiontest.world.feature;

import com.mojang.serialization.Codec;

import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class ModConfiguredFeature extends ModFeature<ModFeatureConfiguration> {

    public ModConfiguredFeature(Codec<ModFeatureConfiguration> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext context) {
        return super.place(context);
    }
}
