package com.statecontrolled.dimensiontest.world.structure;

import com.mojang.serialization.MapCodec;
import com.statecontrolled.dimensiontest.DimensionTest;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModStructures {
    public static final DeferredRegister<StructureType<?>> DEFERRED_REGISTRY_STRUCTURE =
            DeferredRegister.create(Registries.STRUCTURE_TYPE, DimensionTest.MOD_ID);

    public static final DeferredHolder<StructureType<?>, StructureType<CustomStructure>> CUSTOM_STRUCTURE =
            DEFERRED_REGISTRY_STRUCTURE.register("custom_structure", () -> explicitStructureTypeTyping(CustomStructure.CODEC));

    public static final DeferredHolder<StructureType<?>, StructureType<CustomJigsaw>> CUSTOM_JIGSAW =
            DEFERRED_REGISTRY_STRUCTURE.register("custom_jigsaw", () -> explicitStructureTypeTyping(CustomJigsaw.CODEC));

    private static <T extends Structure> StructureType<T> explicitStructureTypeTyping(MapCodec<T> structureCodec) {
        return () -> structureCodec;
    }

    public static void register(IEventBus bus) {
        DEFERRED_REGISTRY_STRUCTURE.register(bus);
    }
}
