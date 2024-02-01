package com.statecontrolled.dimensiontest.world.structure;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;

import com.statecontrolled.dimensiontest.DimensionTest;

import net.minecraft.resources.ResourceLocation;

public class StructureInit {

    private StructureInit() {
        ;
    }

    public static HashMap<ResourceLocation, ResourceLocation[]> initializeStructures(File nbtFileDirectory) {
        ResourceLocation r = new ResourceLocation(DimensionTest.MOD_ID, "corridor_cross");
        r.getPath();
        return null;
    }
}
