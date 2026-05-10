package io.github.cvrunmin.create_andertech.index;

import io.github.cvrunmin.create_andertech.CreateAndertech;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NetherForestVegetationConfig;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;

public class AllConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>>
            CRIMSON_FOREST_VEGETATION_BONEMEAL_POOR = key("crimson_forest_vegetation_bonemeal_poor"),
            CRIMSON_FOREST_VEGETATION_BONEMEAL_RICH = key("crimson_forest_vegetation_bonemeal_rich"),
            WARPED_FOREST_VEGETATION_BONEMEAL_POOR = key("warped_forest_vegetation_bonemeal_poor"),
            WARPED_FOREST_VEGETATION_BONEMEAL_RICH = key("warped_forest_vegetation_bonemeal_rich");

    private static ResourceKey<ConfiguredFeature<?, ?>> key(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, name));
    }

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> ctx) {
        WeightedStateProvider richCrimsonWeight = new WeightedStateProvider(
                SimpleWeightedRandomList.<BlockState>builder()
                        .add(Blocks.CRIMSON_FUNGUS.defaultBlockState(), 9)
                        .add(Blocks.WARPED_FUNGUS.defaultBlockState(), 1)
        );
        WeightedStateProvider richWarpedWeight = new WeightedStateProvider(
                SimpleWeightedRandomList.<BlockState>builder()
                        .add(Blocks.WARPED_FUNGUS.defaultBlockState(), 9)
                        .add(Blocks.CRIMSON_FUNGUS.defaultBlockState(), 1)
        );
        WeightedStateProvider poorWarpedWeight = new WeightedStateProvider(
                SimpleWeightedRandomList.<BlockState>builder()
                        .add(Blocks.WARPED_ROOTS.defaultBlockState(), 90)
                        .add(Blocks.CRIMSON_ROOTS.defaultBlockState(), 10)
        );
        FeatureUtils.register(ctx, CRIMSON_FOREST_VEGETATION_BONEMEAL_POOR, Feature.NETHER_FOREST_VEGETATION, new NetherForestVegetationConfig(BlockStateProvider.simple(Blocks.CRIMSON_ROOTS), 5, 1));
        FeatureUtils.register(ctx, WARPED_FOREST_VEGETATION_BONEMEAL_POOR, Feature.NETHER_FOREST_VEGETATION, new NetherForestVegetationConfig(poorWarpedWeight, 5, 1));
        FeatureUtils.register(ctx, CRIMSON_FOREST_VEGETATION_BONEMEAL_RICH, Feature.NETHER_FOREST_VEGETATION, new NetherForestVegetationConfig(richCrimsonWeight, 5, 1));
        FeatureUtils.register(ctx, WARPED_FOREST_VEGETATION_BONEMEAL_RICH, Feature.NETHER_FOREST_VEGETATION, new NetherForestVegetationConfig(richWarpedWeight, 5, 1));
    }
}
