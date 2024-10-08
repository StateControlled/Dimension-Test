package com.statecontrolled.dimensiontest.world.chunk;

import com.mojang.serialization.MapCodec;
import com.statecontrolled.dimensiontest.DimensionTest;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModChunkGenerators {
    public static final DeferredRegister<MapCodec<? extends ChunkGenerator>> CHUNK_GENERATORS =
            DeferredRegister.create(Registries.CHUNK_GENERATOR, DimensionTest.MOD_ID);

    public static final DeferredHolder<MapCodec<? extends ChunkGenerator>, MapCodec<TestChunkGenerator>> TEST_CHUNK_GENERATOR =
            CHUNK_GENERATORS.register("test_chunk_generator", () -> TestChunkGenerator.CODEC);

    public static final DeferredHolder<MapCodec<? extends ChunkGenerator>, MapCodec<CustomChunkGenerator>> DIM_CHUNK_GENERATOR =
            CHUNK_GENERATORS.register("dim_chunk_generator", () -> CustomChunkGenerator.CODEC);

    private ModChunkGenerators() {
        ;
    }

    public static void register(IEventBus eventBus) {
        CHUNK_GENERATORS.register(eventBus);
    }
}
