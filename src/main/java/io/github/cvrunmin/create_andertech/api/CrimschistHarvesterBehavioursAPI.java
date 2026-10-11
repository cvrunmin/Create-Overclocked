package io.github.cvrunmin.create_andertech.api;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.BlockHelper;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CrimschistHarvesterBehavioursAPI {
    private static final Map<Block, List<HarvestCallback>> CALLBACKS = new ConcurrentHashMap<>();

    public static void register(Block block, HarvestCallback callback){
        CALLBACKS.computeIfAbsent(block, k -> Collections.synchronizedList(new ArrayList<>())).addFirst(callback);
    }

    public static void harvest(Level level, BlockPos pos, ItemStack harvestTool, float vfxChance, MovementBehaviour behaviour, MovementContext context, boolean takeASeed){
        MutableBoolean seedSubtracted = new MutableBoolean(!takeASeed);
        BlockState state = level.getBlockState(pos);
        BlockHelper.destroyBlockAs(level, pos, null, harvestTool, vfxChance, stack -> {
            if (takeASeed && !seedSubtracted.getValue() && ItemHelper.sameItem(stack, new ItemStack(state.getBlock()))) {
                stack.shrink(1);
                seedSubtracted.setTrue();
            }
            behaviour.collectOrDropItem(context, stack);
        });
    }

    public static boolean tryHarvest(Level level, BlockPos pos, BlockState state, MovementBehaviour behaviour){
        boolean harvestPartial = AllConfigs.server().kinetics.harvestPartiallyGrown.get();
        boolean replant = AllConfigs.server().kinetics.harvesterReplants.get();
        Block block = state.getBlock();
        if(CALLBACKS.containsKey(block)){
            for (HarvestCallback harvestCallback : CALLBACKS.get(block)) {
                var result = harvestCallback.tryHarvest(level, pos, state, behaviour, replant, harvestPartial);
                if(result != CallResult.PASS) return result.asBoolean();
            }
        }
        return false;
    }

    @FunctionalInterface
    public interface HarvestCallback {
        CallResult tryHarvest(Level level, BlockPos pos, BlockState state, MovementBehaviour behaviour, boolean shouldReplenish, boolean harvestPartiallyGrown);
    }

    public enum CallResult {
        PASS, SUCCESS, FAIL;

        public boolean asBoolean(){
            return this == SUCCESS;
        }
    }
}
