package io.github.cvrunmin.create_andertech.content.decoration;

import io.github.cvrunmin.create_andertech.index.AllBlocks;
import io.github.cvrunmin.create_andertech.index.AllConfiguredFeatures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.NetherFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class PoorNyliumBlock extends Block implements BonemealableBlock {
    public PoorNyliumBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if(!RichNyliumBlock.canBeNylium(state, level, pos)){
            level.setBlockAndUpdate(pos, Blocks.NETHERRACK.defaultBlockState());
        }
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return level.getBlockState(pos.above()).isAir();
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        BlockState blockstate = level.getBlockState(pos);
        BlockPos blockpos = pos.above();
        ChunkGenerator chunkgenerator = level.getChunkSource().getGenerator();
        Registry<ConfiguredFeature<?, ?>> registry = level.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE);
        if(blockstate.is(AllBlocks.POOR_CRIMSON_NYLIUM)){
            this.place(registry, AllConfiguredFeatures.CRIMSON_FOREST_VEGETATION_BONEMEAL_POOR, level, chunkgenerator, random, blockpos);
        }else if(blockstate.is(AllBlocks.POOR_WARPED_NYLIUM)){
            this.place(registry, AllConfiguredFeatures.WARPED_FOREST_VEGETATION_BONEMEAL_POOR, level, chunkgenerator, random, blockpos);
            this.place(registry, NetherFeatures.NETHER_SPROUTS_BONEMEAL, level, chunkgenerator, random, blockpos);
            if (random.nextInt(8) == 0) {
                this.place(registry, NetherFeatures.TWISTING_VINES_BONEMEAL, level, chunkgenerator, random, blockpos);
            }
        }
    }


    private void place(
            Registry<ConfiguredFeature<?, ?>> featureRegistry,
            ResourceKey<ConfiguredFeature<?, ?>> featureKey,
            ServerLevel level,
            ChunkGenerator chunkGenerator,
            RandomSource random,
            BlockPos pos
    ) {
        featureRegistry.getHolder(featureKey).ifPresent(p_255920_ -> p_255920_.value().place(level, chunkGenerator, random, pos));
    }

    @Override
    public BonemealableBlock.Type getType() {
        return BonemealableBlock.Type.NEIGHBOR_SPREADER;
    }
}
