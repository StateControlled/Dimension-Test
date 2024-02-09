package com.statecontrolled.dimensiontest.world.chunk;

import java.util.Optional;

import com.statecontrolled.dimensiontest.DimensionTest;
import com.statecontrolled.dimensiontest.world.dimension.TestDimension;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class NoUse {

    public void buildSurface(WorldGenRegion level, StructureManager structureManager, RandomState random, ChunkAccess chunk) {
        try {
            ServerLevel serverLevel = level.getServer().getLevel(TestDimension.M_LEVEL_KEY);
            Optional<StructureTemplate> template = serverLevel.getStructureManager().get(new ResourceLocation(DimensionTest.MOD_ID, "corridor_cross"));

            if (template.isPresent()) {
                StructureTemplate placeable = template.get();

                ChunkPos pos = chunk.getPos();
                int x = pos.getBlockX(0);
                int z = pos.getBlockZ(0);
                int y = 72;

                DimensionTest.LOGGER.log(java.util.logging.Level.INFO, "Placing structure [" + placeable + "] at (" + x + ", " + y + ", " + z + ")");

                placeable.placeInWorld(
                        serverLevel,
                        BlockPos.ZERO,
                        new BlockPos(x, y, z),
                        new StructurePlaceSettings().setRotation(Rotation.NONE).setMirror(Mirror.NONE).setIgnoreEntities(false),
                        serverLevel.random,
                        3
                );
            } else {
                DimensionTest.LOGGER.log(java.util.logging.Level.WARNING, "Could not find structure at " + template);
            }
        } catch (NullPointerException e) {
            DimensionTest.LOGGER.log(java.util.logging.Level.SEVERE, "Unexpected NULL value : " + e.getMessage(), e);
        } catch (Exception e) {
            DimensionTest.LOGGER.log(java.util.logging.Level.SEVERE, "An exception has occurred : " + e.getMessage(), e);
        }
    }

}
