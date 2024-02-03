package com.statecontrolled.dimensiontest.world.biome;

import com.statecontrolled.dimensiontest.DimensionTest;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;

/**
 * Setup for mod biomes
 **/
public class ModBiomes {
    public static final ResourceKey<Biome> BIOME_ONE = register("biome_one");
    public static final ResourceKey<Biome> BIOME_TWO = register("biome_two");
    public static final ResourceKey<Biome> BIOME_THREE = register("biome_three");

    private ModBiomes() {
        ;
    }

    public static ResourceKey<Biome> register(String name) {
        return ResourceKey.create(Registries.BIOME, new ResourceLocation(DimensionTest.MOD_ID, name));
    }

    /**
     * Initialize biomes. Define biome settings.
     **/
    public static void bootstrap(BootstapContext<Biome> context) {
        context.register(BIOME_ONE, initBiomeOne(context));
        context.register(BIOME_TWO, initBiomeTwo(context));
        context.register(BIOME_THREE, initBiomeThree(context));
    }

    /**
     * Setup for Biome_One
     */
    public static Biome initBiomeOne(BootstapContext<Biome> context) {
        DimensionTest.LOGGER.log(java.util.logging.Level.INFO, "Init Biome One");

        MobSpawnSettings.Builder mobSpawnBuilder = new MobSpawnSettings.Builder();
        mobSpawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.STRAY, 6, 2, 4));

        BiomeGenerationSettings.Builder biomeBuilder =
                new BiomeGenerationSettings.Builder(context.lookup(Registries.PLACED_FEATURE), context.lookup(Registries.CONFIGURED_CARVER));

        biomeBuilder.addCarver(GenerationStep.Carving.AIR, ModConfiguredCarvers.CUSTOM_CARVER_KEY);
        //biomeBuilder.addFeature()

        return new Biome.BiomeBuilder()
                .hasPrecipitation(false)
                .downfall(0.8f)
                .temperature(0.75f)
                .generationSettings(biomeBuilder.build())
                .mobSpawnSettings(mobSpawnBuilder.build())
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(0x98c9f5)
                        .waterFogColor(0x9fd9ff)
                        .skyColor(0x3fb9e6)
                        .fogColor(0xf9f8f7)
                        .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build()
                ).build();
    }

    /**
     * Setup for Biome_Two
     */
    public static Biome initBiomeTwo(BootstapContext<Biome> context) {
        DimensionTest.LOGGER.log(java.util.logging.Level.INFO, "Init Biome Two");

        MobSpawnSettings.Builder mobSpawnBuilder = new MobSpawnSettings.Builder();
        mobSpawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.SKELETON, 6, 2, 4));

        BiomeGenerationSettings.Builder biomeBuilder =
                new BiomeGenerationSettings.Builder(context.lookup(Registries.PLACED_FEATURE), context.lookup(Registries.CONFIGURED_CARVER));

        biomeBuilder.addCarver(GenerationStep.Carving.AIR, ModConfiguredCarvers.CUSTOM_CARVER_KEY);

        return new Biome.BiomeBuilder()
                .hasPrecipitation(false)
                .downfall(0.8f)
                .temperature(0.85f)
                .generationSettings(biomeBuilder.build())
                .mobSpawnSettings(mobSpawnBuilder.build())
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(0xf598c9)
                        .waterFogColor(0xff9fd9)
                        .skyColor(0x363fb9)
                        .fogColor(0xf7f9f8)
                        .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build()
                ).build();
    }

    /**
     * Setup for Biome_Three
     */
    public static Biome initBiomeThree(BootstapContext<Biome> context) {
        DimensionTest.LOGGER.log(java.util.logging.Level.INFO, "Init Biome Three");

        MobSpawnSettings.Builder mobSpawnBuilder = new MobSpawnSettings.Builder();
        mobSpawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.DROWNED, 6, 2, 4));

        BiomeGenerationSettings.Builder biomeBuilder =
                new BiomeGenerationSettings.Builder(context.lookup(Registries.PLACED_FEATURE), context.lookup(Registries.CONFIGURED_CARVER));

        biomeBuilder.addCarver(GenerationStep.Carving.AIR, ModConfiguredCarvers.CUSTOM_CARVER_KEY);

        return new Biome.BiomeBuilder()
                .hasPrecipitation(false)
                .downfall(0.8f)
                .temperature(0.95f)
                .generationSettings(biomeBuilder.build())
                .mobSpawnSettings(mobSpawnBuilder.build())
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(0xc9f598)
                        .waterFogColor(0xd9ff9f)
                        .skyColor(0xb9e63f)
                        .fogColor(0xf8f7f9)
                        .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build()
                ).build();
    }

}
