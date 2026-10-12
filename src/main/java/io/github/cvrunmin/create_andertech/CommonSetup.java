package io.github.cvrunmin.create_andertech;

import io.github.cvrunmin.create_andertech.api.CrimschistHarvesterBehavioursAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Iterator;

@EventBusSubscriber(modid = CreateAndertech.MODID)
public class CommonSetup {
    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event){
        registerHarvestBehaviour();
    }

    private static void registerHarvestBehaviour(){
        CrimschistHarvesterBehavioursAPI.register(Blocks.WHEAT, (level, pos, state, context, shouldReplenish, harvestPartiallyGrown) -> {
            if(state.getValue(CropBlock.AGE) == 7 || harvestPartiallyGrown && state.getValue(CropBlock.AGE) != 0){
                CrimschistHarvesterBehavioursAPI.harvest(level, pos, ItemStack.EMPTY, 1.0f, context, shouldReplenish);
                if(shouldReplenish){
                    var newState = state.setValue(CropBlock.AGE, 0);
                    level.setBlockAndUpdate(pos, newState);
                }
                return CrimschistHarvesterBehavioursAPI.CallResult.SUCCESS;
            }
            return CrimschistHarvesterBehavioursAPI.CallResult.FAIL;
        });
        CrimschistHarvesterBehavioursAPI.register(Blocks.POTATOES, (level, pos, state, context, shouldReplenish, harvestPartiallyGrown) -> {
            if(state.getValue(CropBlock.AGE) == 7 || harvestPartiallyGrown && state.getValue(CropBlock.AGE) != 0){
                CrimschistHarvesterBehavioursAPI.harvest(level, pos, ItemStack.EMPTY, 1.0f, context, shouldReplenish);
                if(shouldReplenish){
                    var newState = state.setValue(CropBlock.AGE, 0);
                    level.setBlockAndUpdate(pos, newState);
                }
                return CrimschistHarvesterBehavioursAPI.CallResult.SUCCESS;
            }
            return CrimschistHarvesterBehavioursAPI.CallResult.FAIL;
        });
        CrimschistHarvesterBehavioursAPI.register(Blocks.CARROTS, (level, pos, state, context, shouldReplenish, harvestPartiallyGrown) -> {
            if(state.getValue(CropBlock.AGE) == 7 || harvestPartiallyGrown && state.getValue(CropBlock.AGE) != 0){
                CrimschistHarvesterBehavioursAPI.harvest(level, pos, ItemStack.EMPTY, 1.0f, context, shouldReplenish);
                if(shouldReplenish){
                    var newState = state.setValue(CropBlock.AGE, 0);
                    level.setBlockAndUpdate(pos, newState);
                }
                return CrimschistHarvesterBehavioursAPI.CallResult.SUCCESS;
            }
            return CrimschistHarvesterBehavioursAPI.CallResult.FAIL;
        });
        CrimschistHarvesterBehavioursAPI.register(Blocks.BEETROOTS, (level, pos, state, context, shouldReplenish, harvestPartiallyGrown) -> {
            if(state.getValue(BeetrootBlock.AGE) == 3 || harvestPartiallyGrown && state.getValue(BeetrootBlock.AGE) != 0){
                CrimschistHarvesterBehavioursAPI.harvest(level, pos, ItemStack.EMPTY, 1.0f, context, shouldReplenish);
                if(shouldReplenish){
                    var newState = state.setValue(BeetrootBlock.AGE, 0);
                    level.setBlockAndUpdate(pos, newState);
                }
                return CrimschistHarvesterBehavioursAPI.CallResult.SUCCESS;
            }
            return CrimschistHarvesterBehavioursAPI.CallResult.FAIL;
        });
        CrimschistHarvesterBehavioursAPI.register(Blocks.SWEET_BERRY_BUSH, (level, pos, state, context, shouldReplenish, harvestPartiallyGrown) -> {
            if(state.getValue(SweetBerryBushBlock.AGE) > 1){
                CrimschistHarvesterBehavioursAPI.harvest(level, pos, ItemStack.EMPTY, 1.0f, context, false);
                var newState = state.setValue(SweetBerryBushBlock.AGE, 1);
                level.setBlockAndUpdate(pos, newState);
                return CrimschistHarvesterBehavioursAPI.CallResult.SUCCESS;
            }
            return CrimschistHarvesterBehavioursAPI.CallResult.FAIL;
        });
        CrimschistHarvesterBehavioursAPI.register(Blocks.SUGAR_CANE, (level, pos, state, context, shouldReplenish, harvestPartiallyGrown) -> {
            var topPos = pos.mutable();
            while (level.getBlockState(topPos.above()).is(Blocks.SUGAR_CANE)){
                topPos.move(Direction.UP);
            }
            for (var curPos = topPos.mutable(); curPos.subtract(pos).getY() >= 0; curPos.move(Direction.DOWN)) {
                CrimschistHarvesterBehavioursAPI.harvest(level, curPos, ItemStack.EMPTY, 1.0f, context, false);
            }
            return CrimschistHarvesterBehavioursAPI.CallResult.SUCCESS;
        });
        CrimschistHarvesterBehavioursAPI.register(Blocks.CACTUS, (level, pos, state, context, shouldReplenish, harvestPartiallyGrown) -> {
            var topPos = pos.mutable();
            while (level.getBlockState(topPos.above()).is(Blocks.CACTUS)){
                topPos.move(Direction.UP);
            }
            for (var curPos = topPos.mutable(); curPos.subtract(pos).getY() >= 0; curPos.move(Direction.DOWN)) {
                CrimschistHarvesterBehavioursAPI.harvest(level, curPos, ItemStack.EMPTY, 1.0f, context, false);
            }
            return CrimschistHarvesterBehavioursAPI.CallResult.SUCCESS;
        });
        CrimschistHarvesterBehavioursAPI.register(Blocks.BAMBOO, (level, pos, state, context, shouldReplenish, harvestPartiallyGrown) -> {
            var topPos = pos.mutable();
            while (level.getBlockState(topPos.above()).is(Blocks.BAMBOO)){
                topPos.move(Direction.UP);
            }
            for (var curPos = topPos.mutable(); curPos.subtract(pos).getY() >= 0; curPos.move(Direction.DOWN)) {
                CrimschistHarvesterBehavioursAPI.harvest(level, curPos, ItemStack.EMPTY, 1.0f, context, false);
            }
            return CrimschistHarvesterBehavioursAPI.CallResult.SUCCESS;
        });
        CrimschistHarvesterBehavioursAPI.register(Blocks.KELP, (level, pos, state, context, shouldReplenish, harvestPartiallyGrown) -> {
            // top block of kelp
            CrimschistHarvesterBehavioursAPI.harvest(level, pos, ItemStack.EMPTY, 1.0f, context, false);
            return CrimschistHarvesterBehavioursAPI.CallResult.SUCCESS;
        });
        CrimschistHarvesterBehavioursAPI.register(Blocks.KELP_PLANT, (level, pos, state, context, shouldReplenish, harvestPartiallyGrown) -> {
            var topPos = pos.mutable();
            while (true){
                BlockState state1 = level.getBlockState(topPos.above());
                if (!(state1.is(Blocks.KELP_PLANT) || state1.is(Blocks.KELP))) break;
                topPos.move(Direction.UP);
            }
            for (var curPos = topPos.mutable(); curPos.subtract(pos).getY() >= 0; curPos.move(Direction.DOWN)) {
                CrimschistHarvesterBehavioursAPI.harvest(level, curPos, ItemStack.EMPTY, 1.0f, context, false);
            }
            return CrimschistHarvesterBehavioursAPI.CallResult.SUCCESS;
        });
        CrimschistHarvesterBehavioursAPI.register(Blocks.CAVE_VINES, (level, pos, state, context, shouldReplenish, harvestPartiallyGrown) -> {
            // bottom block of cave vines
            CrimschistHarvesterBehavioursAPI.harvest(level, pos, ItemStack.EMPTY, 1.0f, context, false);
            return CrimschistHarvesterBehavioursAPI.CallResult.SUCCESS;
        });
        CrimschistHarvesterBehavioursAPI.register(Blocks.CAVE_VINES_PLANT, (level, pos, state, context, shouldReplenish, harvestPartiallyGrown) -> {
            var topPos = pos.mutable();
            while (true){
                BlockState state1 = level.getBlockState(topPos.below());
                if (!(state1.is(Blocks.CAVE_VINES_PLANT) || state1.is(Blocks.CAVE_VINES))) break;
                topPos.move(Direction.DOWN);
            }
            for (var curPos = topPos.mutable(); pos.subtract(curPos).getY() >= 0; curPos.move(Direction.UP)) {
                CrimschistHarvesterBehavioursAPI.harvest(level, curPos, ItemStack.EMPTY, 1.0f, context, false);
            }
            return CrimschistHarvesterBehavioursAPI.CallResult.SUCCESS;
        });
        CrimschistHarvesterBehavioursAPI.register(Blocks.MELON, (level, pos, state, context, shouldReplenish, harvestPartiallyGrown) -> {
            var hasStem = false;
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                if(level.getBlockState(pos.relative(direction)).is(Blocks.ATTACHED_MELON_STEM)){
                    hasStem = true;
                    break;
                }
            }
            if(hasStem){
                CrimschistHarvesterBehavioursAPI.harvest(level, pos, ItemStack.EMPTY, 1.0f, context, false);
            }
            return CrimschistHarvesterBehavioursAPI.CallResult.of(hasStem);
        });
        CrimschistHarvesterBehavioursAPI.register(Blocks.PUMPKIN, (level, pos, state, context, shouldReplenish, harvestPartiallyGrown) -> {
            var hasStem = false;
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                if(level.getBlockState(pos.relative(direction)).is(Blocks.ATTACHED_PUMPKIN_STEM)){
                    hasStem = true;
                    break;
                }
            }
            if(hasStem){
                CrimschistHarvesterBehavioursAPI.harvest(level, pos, ItemStack.EMPTY, 1.0f, context, false);
            }
            return CrimschistHarvesterBehavioursAPI.CallResult.of(hasStem);
        });
        CrimschistHarvesterBehavioursAPI.register(Blocks.CHORUS_FLOWER, ((level, pos, state, context, shouldReplenish, harvestPartiallyGrown) -> {
            if(state.getValue(ChorusFlowerBlock.AGE) == 5 || harvestPartiallyGrown){
                CrimschistHarvesterBehavioursAPI.harvest(level, pos, ItemStack.EMPTY, 1.0f, context, false);
                return CrimschistHarvesterBehavioursAPI.CallResult.SUCCESS;
            }
            return CrimschistHarvesterBehavioursAPI.CallResult.FAIL;
        }));
        CrimschistHarvesterBehavioursAPI.register(Blocks.CHORUS_PLANT, ((level, pos, state, context, shouldReplenish, harvestPartiallyGrown) -> {
            var deque = new ArrayDeque<BlockPos>();
            var visited = new ArrayDeque<BlockPos>();
            deque.add(pos);
            while(!deque.isEmpty()){
                var visiting = deque.pop();
                if(visited.contains(visiting)) continue;
                BlockState blockState = level.getBlockState(visiting);
                if(blockState.is(Blocks.CHORUS_FLOWER)){
                    if(!harvestPartiallyGrown && blockState.getValue(ChorusFlowerBlock.AGE) != 5){
                        return CrimschistHarvesterBehavioursAPI.CallResult.FAIL;
                    }
                    visited.addLast(visiting);
                }else if(blockState.is(Blocks.CHORUS_PLANT)){
                    visited.addLast(visiting);
                    deque.addLast(visiting.east());
                    deque.addLast(visiting.west());
                    deque.addLast(visiting.north());
                    deque.addLast(visiting.south());
                    deque.addLast(visiting.above());
                }
            }
            boolean takeAFlower = shouldReplenish && level.getBlockState(pos.below()).is(Blocks.END_STONE);
            boolean hasTaken = false;
            for (Iterator<BlockPos> iterator = visited.descendingIterator(); iterator.hasNext(); ) {
                BlockPos blockPos = iterator.next();
                boolean shouldTryTake = level.getBlockState(blockPos).is(Blocks.CHORUS_FLOWER);
                CrimschistHarvesterBehavioursAPI.harvest(level, blockPos, ItemStack.EMPTY, 1.0f, context, takeAFlower && !hasTaken && shouldTryTake);
                if(shouldTryTake) hasTaken = true;
            }
            if(takeAFlower){
                level.setBlockAndUpdate(pos, Blocks.CHORUS_FLOWER.defaultBlockState());
            }
            return CrimschistHarvesterBehavioursAPI.CallResult.SUCCESS;
        }));
    }
}
