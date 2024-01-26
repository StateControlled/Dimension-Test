package com.statecontrolled.dimensiontest;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

import com.statecontrolled.dimensiontest.block.ModBlocks;
import com.statecontrolled.dimensiontest.item.ModItems;
import com.statecontrolled.dimensiontest.world.chunk.ModChunkGenerators;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(DimensionTest.MOD_ID)
public class DimensionTest {
    public static final String MOD_ID = "dimensiontest";
    public static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(DimensionTest.class.getName());

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

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public DimensionTest(IEventBus modEventBus) {
        ModBlocks.register(modEventBus);

        ModItems.register(modEventBus);

        ModChunkGenerators.register(modEventBus);

        ModCreativeModeTab.register(modEventBus);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ExampleMod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        //NeoForge.EVENT_BUS.register(this);
    }

}
