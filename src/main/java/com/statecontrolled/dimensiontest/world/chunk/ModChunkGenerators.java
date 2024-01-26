package com.statecontrolled.dimensiontest.world.chunk;

import com.mojang.serialization.Codec;
import com.statecontrolled.dimensiontest.DimensionTest;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModChunkGenerators {
    public static final DeferredRegister<Codec<? extends ChunkGenerator>> CHUNK_GENERATORS =
            DeferredRegister.create(Registries.CHUNK_GENERATOR, DimensionTest.MOD_ID);

    public static final DeferredHolder<Codec<? extends ChunkGenerator>, Codec<TestChunkGenerator>> TEST_CHUNK_GENERATOR =
            CHUNK_GENERATORS.register("test_chunk_generator", () -> TestChunkGenerator.CODEC);

    public static final DeferredHolder<Codec<? extends ChunkGenerator>, Codec<DimChunkGenerator>> DIM_CHUNK_GENERATOR =
            CHUNK_GENERATORS.register("dim_chunk_generator", () -> DimChunkGenerator.CODEC);

    private ModChunkGenerators() {
        ;
    }

    public static void register(IEventBus eventBus) {
        CHUNK_GENERATORS.register(eventBus);
    }
}
