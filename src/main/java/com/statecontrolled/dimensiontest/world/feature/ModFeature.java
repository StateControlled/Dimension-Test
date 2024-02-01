package com.statecontrolled.dimensiontest.world.feature;

import com.mojang.serialization.Codec;

import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

// Placed feature
public class ModFeature<T extends ModFeatureConfiguration> extends Feature<T> {

    public ModFeature(Codec<T> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext pContext) {
        // wave function collapse here

        return true;
    }
}
