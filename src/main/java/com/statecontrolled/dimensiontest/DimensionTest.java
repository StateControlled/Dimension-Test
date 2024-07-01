package com.statecontrolled.dimensiontest;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

import com.statecontrolled.dimensiontest.block.ModBlocks;
import com.statecontrolled.dimensiontest.item.ModItems;
import com.statecontrolled.dimensiontest.util.ModCreativeModeTab;
import com.statecontrolled.dimensiontest.world.cave.ModCarvers;
import com.statecontrolled.dimensiontest.world.chunk.ModChunkGenerators;

import com.statecontrolled.dimensiontest.world.structure.ModStructures;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

/**
 * Main class.
 * <p>
 * Copyright (c) 2024 by State_Controlled.
 * <p>
 * This source code is released under the MIT License.
 *
 * @author State_Controlled
 */
@Mod(DimensionTest.MOD_ID)
public class DimensionTest {
    public static final String MOD_ID = "dimensiontest";
    public static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(DimensionTest.class.getName());

    // Setup for logger
    static {
        try {
            FileHandler fileHandler = new FileHandler("dimension_test.log", false);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
            LOGGER.setLevel(Level.FINE);
            String ID = UUID.randomUUID().toString();
            LOGGER.log(Level.INFO, ID + " : Init Logger");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Main entry point for the Mod.
     *
     * @param modEventBus NeoForge will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
     **/
    public DimensionTest(IEventBus modEventBus) {
        ModBlocks.register(modEventBus);

        ModItems.register(modEventBus);

        ModChunkGenerators.register(modEventBus);

        ModCreativeModeTab.register(modEventBus);

        ModCarvers.register(modEventBus);

        ModStructures.register(modEventBus);
    }

}
