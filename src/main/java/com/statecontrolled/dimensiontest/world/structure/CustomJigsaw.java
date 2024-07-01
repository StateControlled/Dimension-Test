package com.statecontrolled.dimensiontest.world.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;

import java.util.List;
import java.util.Optional;

public class CustomJigsaw extends Structure {
    public static final DimensionPadding DEFAULT_DIMENSION_PADDING = DimensionPadding.ZERO;
    public static final LiquidSettings DEFAULT_LIQUID_SETTINGS = LiquidSettings.APPLY_WATERLOGGING;
    public static final MapCodec<CustomJigsaw> CODEC =
            RecordCodecBuilder.<CustomJigsaw>mapCodec(
                    customJigsawInstance -> customJigsawInstance.group(
                                    settingsCodec(customJigsawInstance),
                                    StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(p_227656_ -> p_227656_.startPool),
                                    ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(p_227654_ -> p_227654_.startJigsawName),
                                    HeightProvider.CODEC.fieldOf("start_height").forGetter(p_227649_ -> p_227649_.startHeight),
                                    Codec.BOOL.fieldOf("use_expansion_hack").forGetter(p_227646_ -> p_227646_.useExpansionHack),
                                    Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(p_227644_ -> p_227644_.projectStartToHeightmap),
                                    Codec.list(PoolAliasBinding.CODEC).optionalFieldOf("pool_aliases", List.of()).forGetter(p_307187_ -> p_307187_.poolAliases),
                                    DimensionPadding.CODEC
                                            .optionalFieldOf("dimension_padding", DEFAULT_DIMENSION_PADDING)
                                            .forGetter(p_348455_ -> p_348455_.dimensionPadding),
                                    LiquidSettings.CODEC.optionalFieldOf("liquid_settings", DEFAULT_LIQUID_SETTINGS).forGetter(p_352036_ -> p_352036_.liquidSettings)
                            )
                            .apply(customJigsawInstance, CustomJigsaw::new)
            )
            .validate(CustomJigsaw::verifyRange);
    private final Holder<StructureTemplatePool> startPool;
    private final Optional<ResourceLocation> startJigsawName;
    private final HeightProvider startHeight;
    private final boolean useExpansionHack;
    private final Optional<Heightmap.Types> projectStartToHeightmap;
    private final List<PoolAliasBinding> poolAliases;
    private final DimensionPadding dimensionPadding;
    private final LiquidSettings liquidSettings;

    private static DataResult<CustomJigsaw> verifyRange(CustomJigsaw customJigsaw) {
        return DataResult.success(customJigsaw);
    }
    
//    private static DataResult<JigsawStructure> verifyRange(JigsawStructure jigsawStructure) {
//        int i = switch (jigsawStructure.terrainAdaptation()) {
//            case NONE -> 0;
//            case BURY, BEARD_THIN, BEARD_BOX, ENCAPSULATE -> 12;
//        };
//        return jigsawStructure.maxDistanceFromCenter + i > 128
//                ? DataResult.error(() -> "Structure size including terrain adaptation must not exceed 128")
//                : DataResult.success(jigsawStructure);
//    }

    public CustomJigsaw(Structure.StructureSettings structureSettings,
                        Holder<StructureTemplatePool> startPool,
                        Optional<ResourceLocation> resourceLocation,
                        HeightProvider startHeight,
                        boolean useExpansionHack,
                        Optional<Heightmap.Types> projectStartToHeightmap,
                        List<PoolAliasBinding> poolAliases,
                        DimensionPadding dimensionPadding,
                        LiquidSettings liquidSettings) {
        super(structureSettings);
        this.startPool = startPool;
        this.startJigsawName = resourceLocation;
        this.startHeight = startHeight;
        this.useExpansionHack = useExpansionHack;
        this.projectStartToHeightmap = projectStartToHeightmap;
        this.poolAliases = poolAliases;
        this.dimensionPadding = dimensionPadding;
        this.liquidSettings = liquidSettings;
    }

    public CustomJigsaw(Structure.StructureSettings pSettings,
                        Holder<StructureTemplatePool> pStartPool,
                        HeightProvider pStartHeight,
                        boolean pUseExpansionHack,
                        Heightmap.Types pProjectStartToHeightmap) {
        this(
                pSettings,
                pStartPool,
                Optional.empty(),
                pStartHeight,
                pUseExpansionHack,
                Optional.of(pProjectStartToHeightmap),
                List.of(),
                DEFAULT_DIMENSION_PADDING,
                DEFAULT_LIQUID_SETTINGS
        );
    }

    public CustomJigsaw(Structure.StructureSettings pSettings,
                        Holder<StructureTemplatePool> pStartPool,
                        HeightProvider pStartHeight,
                        boolean pUseExpansionHack) {
        this(
                pSettings,
                pStartPool,
                Optional.empty(),
                pStartHeight,
                pUseExpansionHack,
                Optional.empty(),
                List.of(),
                DEFAULT_DIMENSION_PADDING,
                DEFAULT_LIQUID_SETTINGS
        );
    }

    @Override
    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext pContext) {
        ChunkPos chunkpos = pContext.chunkPos();
        int i = this.startHeight.sample(pContext.random(), new WorldGenerationContext(pContext.chunkGenerator(), pContext.heightAccessor()));
        BlockPos blockpos = new BlockPos(chunkpos.getMinBlockX(), i, chunkpos.getMinBlockZ());

        return JigsawPlacement.addPieces(
                pContext,
                this.startPool,
                this.startJigsawName,
                Integer.MAX_VALUE - 1,
                blockpos,
                this.useExpansionHack,
                this.projectStartToHeightmap,
                Integer.MAX_VALUE - 1,
                PoolAliasLookup.create(this.poolAliases, blockpos, pContext.seed()),
                this.dimensionPadding,
                this.liquidSettings
        );
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.CUSTOM_STRUCTURE.get();
    }
}
