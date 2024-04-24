package net.nerdorg.vortexmod.datagen.providers;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.nerdorg.vortexmod.VortexMod;
import net.nerdorg.vortexmod.worldgen.ModConfiguredFeatures;
import net.nerdorg.vortexmod.worldgen.ModPlacedFeatures;
import net.nerdorg.vortexmod.worldgen.biome.ModBiomes;
import net.nerdorg.vortexmod.worldgen.dimension.ModDimensions;
import net.nerdorg.vortexmod.worldgen.utils.ModNoiseGenerator;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModWorldGenProvider extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.DIMENSION_TYPE, ModDimensions::bootstrapType)
            .add(Registries.CONFIGURED_FEATURE, ModConfiguredFeatures::bootstrap)
            .add(Registries.PLACED_FEATURE, ModPlacedFeatures::bootstrap)
            .add(Registries.BIOME, ModBiomes::bootstrap)
            .add(Registries.NOISE_SETTINGS, ModNoiseGenerator::bootstrap)
            .add(Registries.LEVEL_STEM, ModDimensions::bootstrapStem);

    public ModWorldGenProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(VortexMod.MODID));
    }
}
