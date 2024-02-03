package com.statecontrolled.dimensiontest.world.feature;

import java.util.Optional;
import java.util.logging.Level;

import com.mojang.serialization.Codec;
import com.statecontrolled.dimensiontest.DimensionTest;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class ModStructureFeature <T extends ModFeatureConfig> extends Feature<T> {

    public ModStructureFeature(Codec<T> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<T> context) {
        ResourceLocation structure = null;

        StructureTemplateManager templateManager = context.level().getLevel().getStructureManager();
        Optional<StructureTemplate> template = templateManager.get(structure);

        if (template.isEmpty()) {
            DimensionTest.LOGGER.log(Level.WARNING, "Resource location for specified nbt file was not found! " + structure);
            return false;
        }

        StructurePlaceSettings placeSettings = new StructurePlaceSettings().setRotation(Rotation.NONE).setIgnoreEntities(false).setKeepLiquids(false);
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos().set(context.origin());

        template.get().placeInWorld(context.level(), blockPos, blockPos, placeSettings, context.random(), Block.UPDATE_INVISIBLE);

        return true;
    }


}
