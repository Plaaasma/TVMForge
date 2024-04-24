package net.nerdorg.vortexmod.worldgen.dimension;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.nerdorg.vortexmod.VortexMod;
import net.nerdorg.vortexmod.worldgen.biome.ModBiomes;
import net.nerdorg.vortexmod.worldgen.utils.ModNoiseGenerator;

import java.util.List;
import java.util.OptionalLong;

public class ModDimensions {
    // Vortex DIM
    public static final ResourceKey<LevelStem> vortexDIM_KEY = ResourceKey.create(Registries.LEVEL_STEM,
            new ResourceLocation(VortexMod.MODID, "vortexdim"));
    public static final ResourceKey<Level> vortexDIM_LEVEL_KEY = ResourceKey.create(Registries.DIMENSION,
            new ResourceLocation(VortexMod.MODID, "vortexdim"));
    public static final ResourceKey<DimensionType> vortex_DIM_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE,
            new ResourceLocation(VortexMod.MODID, "vortexdim_type"));
    // Tardis DIM
    public static final ResourceKey<LevelStem> tardisDIM_KEY = ResourceKey.create(Registries.LEVEL_STEM,
            new ResourceLocation(VortexMod.MODID, "tardisdim"));
    public static final ResourceKey<Level> tardisDIM_LEVEL_KEY = ResourceKey.create(Registries.DIMENSION,
            new ResourceLocation(VortexMod.MODID, "tardisdim"));
    public static final ResourceKey<DimensionType> tardis_DIM_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE,
            new ResourceLocation(VortexMod.MODID, "tardisdim_type"));

    public static void bootstrapType(BootstapContext<DimensionType> context) {
        context.register(vortex_DIM_TYPE, new DimensionType(
                OptionalLong.of(12000), // fixedTime
                false, // hasSkylight
                false, // hasCeiling
                false, // ultraWarm
                false, // natural
                10.0, // coordinateScale
                true, // bedWorks
                false, // respawnAnchorWorks
                -256, // minY
                256, // height
                256, // logicalHeight
                BlockTags.INFINIBURN_OVERWORLD, // infiniburn
                BuiltinDimensionTypes.OVERWORLD_EFFECTS, // effectsLocation
                1.0f, // ambientLight
                new DimensionType.MonsterSettings(false, false, ConstantInt.of(0), 0)));

        context.register(tardis_DIM_TYPE, new DimensionType(
                OptionalLong.of(18000), // fixedTime
                false, // hasSkylight
                false, // hasCeiling
                false, // ultraWarm
                false, // natural
                1.0, // coordinateScale
                true, // bedWorks
                false, // respawnAnchorWorks
                -256, // minY
                256, // height
                256, // logicalHeight
                BlockTags.INFINIBURN_NETHER, // infiniburn
                BuiltinDimensionTypes.END_EFFECTS, // effectsLocation
                1.0f, // ambientLight
                new DimensionType.MonsterSettings(false, false, ConstantInt.of(0), 0)));
    }

    public static void bootstrapStem(BootstapContext<LevelStem> context) {
        HolderGetter<Biome> biomeRegistry = context.lookup(Registries.BIOME);
        HolderGetter<DimensionType> dimTypes = context.lookup(Registries.DIMENSION_TYPE);
        HolderGetter<NoiseGeneratorSettings> noiseGenSettings = context.lookup(Registries.NOISE_SETTINGS);

        // VORTEX

       NoiseBasedChunkGenerator vortexNoiseBasedChunkGenerator = new NoiseBasedChunkGenerator(
               MultiNoiseBiomeSource.createFromList(
                        new Climate.ParameterList<>(List.of(
                                Pair.of(Climate.parameters(0.9F, 0.0F, 0.8F, 0.0F, 0.0F, 0.0F, 0.0F),
                                        biomeRegistry.getOrThrow(ModBiomes.BLUE_VORTEX_BIOME)),
                                Pair.of(Climate.parameters(0.0F, 0.9F, 0.8F, 0.0F, 0.0F, 0.0F, 0.0F),
                                        biomeRegistry.getOrThrow(ModBiomes.ORANGE_VORTEX_BIOME)),
                                Pair.of(Climate.parameters(0.0F, 0.0F, 0.0F, 1.0F, 0.7F, 0.0F, 0.0F),
                                        biomeRegistry.getOrThrow(ModBiomes.PURPLE_VORTEX_BIOME)),
                                Pair.of(Climate.parameters(0.5F, 0.5F, 0.8F, 0.0F, 0.0F, 0.0F, 0.0F),
                                        biomeRegistry.getOrThrow(ModBiomes.BLACK_VORTEX_BIOME))
                       ))),
                noiseGenSettings.getOrThrow(ModNoiseGenerator.VORTEX_CAVES));

        LevelStem vortexStem = new LevelStem(dimTypes.getOrThrow(ModDimensions.vortex_DIM_TYPE), vortexNoiseBasedChunkGenerator);

        context.register(vortexDIM_KEY, vortexStem);

        // TARDIS

        NoiseBasedChunkGenerator tardisNoiseBasedChunkGenerator = new NoiseBasedChunkGenerator(
                new FixedBiomeSource(biomeRegistry.getOrThrow(ModBiomes.TARDIS_BIOME)),
                noiseGenSettings.getOrThrow(ModNoiseGenerator.VOID));
        LevelStem tardisStem = new LevelStem(dimTypes.getOrThrow(ModDimensions.tardis_DIM_TYPE), tardisNoiseBasedChunkGenerator);

        context.register(tardisDIM_KEY, tardisStem);
    }
}
