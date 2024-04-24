package net.nerdorg.vortexmod.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.nerdorg.vortexmod.VortexMod;

import java.util.List;

public class ModConfiguredFeatures {
    // Blue Vortex Biome
    public static final ResourceKey<ConfiguredFeature<?, ?>> BLUE_BIOME_1_CONFIG = registerKey("blue_biome_1_config");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BLUE_BIOME_2_CONFIG = registerKey("blue_biome_2_config");
    // Orange Vortex Biome
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORANGE_BIOME_1_CONFIG = registerKey("orange_biome_1_config");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORANGE_BIOME_2_CONFIG = registerKey("orange_biome_2_config");
    // Purple Vortex Biome
    public static final ResourceKey<ConfiguredFeature<?, ?>> PURPLE_BIOME_1_CONFIG = registerKey("purple_biome_1_config");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PURPLE_BIOME_2_CONFIG = registerKey("purple_biome_2_config");
    // Black Vortex Biome
    public static final ResourceKey<ConfiguredFeature<?, ?>> BLACK_BIOME_1_CONFIG = registerKey("black_biome_1_config");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BLACK_BIOME_2_CONFIG = registerKey("black_biome_2_config");

    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
        RuleTest blueReplace = new BlockMatchTest(Blocks.SEA_LANTERN);
        RuleTest orangeReplace = new BlockMatchTest(Blocks.GLOWSTONE);
        RuleTest purpleReplace = new BlockMatchTest(Blocks.PURPLE_CONCRETE);
        RuleTest blackReplace = new BlockMatchTest(Blocks.OBSIDIAN);


        // Blue Vortex Biome
        register(context, BLUE_BIOME_1_CONFIG, Feature.ORE,
                new OreConfiguration(List.of(OreConfiguration.target
                        (blueReplace, Blocks.LIGHT_BLUE_CONCRETE.defaultBlockState())),
                        64
                )
        );
        register(context, BLUE_BIOME_2_CONFIG, Feature.ORE,
                new OreConfiguration(List.of(OreConfiguration.target
                        (blueReplace, Blocks.WHITE_CONCRETE.defaultBlockState())),
                        64
                )
        );

        // Orange Vortex Biome
        register(context, ORANGE_BIOME_1_CONFIG, Feature.ORE,
                new OreConfiguration(List.of(OreConfiguration.target
                        (orangeReplace, Blocks.YELLOW_TERRACOTTA.defaultBlockState())),
                        64
                )
        );
        register(context, ORANGE_BIOME_2_CONFIG, Feature.ORE,
                new OreConfiguration(List.of(OreConfiguration.target
                        (orangeReplace, Blocks.ORANGE_CONCRETE.defaultBlockState())),
                        64
                )
        );

        // Purple Vortex Biome
        register(context, PURPLE_BIOME_1_CONFIG, Feature.ORE,
                new OreConfiguration(List.of(OreConfiguration.target
                        (purpleReplace, Blocks.PURPLE_TERRACOTTA.defaultBlockState())),
                        64
                )
        );
        register(context, PURPLE_BIOME_2_CONFIG, Feature.ORE,
                new OreConfiguration(List.of(OreConfiguration.target
                        (purpleReplace, Blocks. PURPLE_GLAZED_TERRACOTTA.defaultBlockState())),
                        64
                )
        );

        // Black Vortex Biome
        register(context, BLACK_BIOME_1_CONFIG, Feature.ORE,
                new OreConfiguration(List.of(OreConfiguration.target
                        (blackReplace, Blocks.GILDED_BLACKSTONE.defaultBlockState())),
                        64
                )
        );
        register(context, BLACK_BIOME_2_CONFIG, Feature.ORE,
                new OreConfiguration(List.of(OreConfiguration.target
                        (blackReplace, Blocks.CRYING_OBSIDIAN.defaultBlockState())),
                        64
                )
        );
    }


    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(VortexMod.MODID, name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstapContext<ConfiguredFeature<?, ?>> context,
                                                                                          ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}