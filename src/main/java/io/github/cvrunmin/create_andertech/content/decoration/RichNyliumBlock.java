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
import net.minecraft.world.level.lighting.LightEngine;

public class RichNyliumBlock extends Block implements BonemealableBlock {
    public RichNyliumBlock(Properties properties) {
        super(properties);
    }

    public static boolean canBeNylium(BlockState state, LevelReader reader, BlockPos pos) {
        BlockPos blockpos = pos.above();
        BlockState blockstate = reader.getBlockState(blockpos);
        int i = LightEngine.getLightBlockInto(reader, state, pos, blockstate, blockpos, Direction.UP, blockstate.getLightBlock(reader, blockpos));
        return i < reader.getMaxLightLevel();
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if(!canBeNylium(state, level, pos)){
            if(state.is(AllBlocks.RICH_CRIMSON_NYLIUM)){
                level.setBlockAndUpdate(pos, AllBlocks.UNTOPPED_CRIMSON_NYLIUM.getDefaultState());
            }else if(state.is(AllBlocks.RICH_WARPED_NYLIUM)){
                level.setBlockAndUpdate(pos, AllBlocks.UNTOPPED_WARPED_NYLIUM.getDefaultState());
            }
        }else{
            if (!level.isAreaLoaded(pos, 3)) return;
            for (int i = 0; i < 4; i++) {
                BlockPos blockpos = pos.offset(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
                if(state.is(AllBlocks.RICH_CRIMSON_NYLIUM)) {
                    if (level.getBlockState(blockpos).is(AllBlocks.UNTOPPED_CRIMSON_NYLIUM) && canBeNylium(this.defaultBlockState(), level, blockpos)) {
                        level.setBlockAndUpdate(blockpos, this.defaultBlockState());
                    } else if (level.getBlockState(blockpos).is(Blocks.NETHERRACK) && canBeNylium(AllBlocks.UNTOPPED_CRIMSON_NYLIUM.getDefaultState(), level, blockpos)) {
                        level.setBlockAndUpdate(blockpos, AllBlocks.UNTOPPED_CRIMSON_NYLIUM.getDefaultState());
                    }
                } else if (state.is(AllBlocks.RICH_WARPED_NYLIUM)) {
                    if (level.getBlockState(blockpos).is(AllBlocks.UNTOPPED_WARPED_NYLIUM) && canBeNylium(this.defaultBlockState(), level, blockpos)) {
                        level.setBlockAndUpdate(blockpos, this.defaultBlockState());
                    } else if (level.getBlockState(blockpos).is(Blocks.NETHERRACK) && canBeNylium(AllBlocks.UNTOPPED_WARPED_NYLIUM.getDefaultState(), level, blockpos)) {
                        level.setBlockAndUpdate(blockpos, AllBlocks.UNTOPPED_WARPED_NYLIUM.getDefaultState());
                    }
                }
            }
        }
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        if(state.is(AllBlocks.UNTOPPED_WARPED_NYLIUM) || state.is(AllBlocks.UNTOPPED_CRIMSON_NYLIUM)){
            if(!level.getBlockState(pos.above()).propagatesSkylightDown(level, pos)){
                return false;
            }
            for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) {
                BlockState blockState = level.getBlockState(blockpos);
                if ((state.is(AllBlocks.UNTOPPED_CRIMSON_NYLIUM) && blockState.is(AllBlocks.RICH_CRIMSON_NYLIUM))
                        || (state.is(AllBlocks.UNTOPPED_WARPED_NYLIUM) && blockState.is(AllBlocks.RICH_WARPED_NYLIUM))) {
                    return true;
                }
            }
            return false;
        }
        return level.getBlockState(pos.above()).isAir();
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        BlockState blockstate = level.getBlockState(pos);
        if(blockstate.is(AllBlocks.UNTOPPED_CRIMSON_NYLIUM) || blockstate.is(AllBlocks.UNTOPPED_WARPED_NYLIUM)){
            for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) {
                BlockState blockstate1 = level.getBlockState(blockpos);
                if (blockstate1.is(AllBlocks.RICH_WARPED_NYLIUM) && blockstate.is(AllBlocks.UNTOPPED_WARPED_NYLIUM)) {
                    level.setBlockAndUpdate(pos, AllBlocks.RICH_WARPED_NYLIUM.getDefaultState());
                    break;
                }

                if (blockstate1.is(AllBlocks.RICH_CRIMSON_NYLIUM) && blockstate.is(AllBlocks.UNTOPPED_CRIMSON_NYLIUM)) {
                    level.setBlockAndUpdate(pos, AllBlocks.RICH_CRIMSON_NYLIUM.getDefaultState());
                    break;
                }
            }
            return;
        }
        BlockPos blockpos = pos.above();
        ChunkGenerator chunkgenerator = level.getChunkSource().getGenerator();
        Registry<ConfiguredFeature<?, ?>> registry = level.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE);
        if(blockstate.is(AllBlocks.RICH_CRIMSON_NYLIUM)){
            this.place(registry, AllConfiguredFeatures.CRIMSON_FOREST_VEGETATION_BONEMEAL_RICH, level, chunkgenerator, random, blockpos);
        }else if(blockstate.is(AllBlocks.RICH_WARPED_NYLIUM)){
            this.place(registry, AllConfiguredFeatures.WARPED_FOREST_VEGETATION_BONEMEAL_RICH, level, chunkgenerator, random, blockpos);
            this.place(registry, NetherFeatures.NETHER_SPROUTS_BONEMEAL, level, chunkgenerator, random, blockpos);
            if (random.nextInt(4) == 0) {
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
    public Type getType() {
        return Type.NEIGHBOR_SPREADER;
    }
}
