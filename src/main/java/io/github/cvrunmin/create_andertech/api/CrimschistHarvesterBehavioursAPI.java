package io.github.cvrunmin.create_andertech.api;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.infrastructure.config.AllConfigs;
import io.github.cvrunmin.create_andertech.impl.CrimschistHarvesterBehavioursImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
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

    public static HarvestContext movementBehaviourContext(MovementBehaviour behaviour, MovementContext context){
        return new HarvestContext(behaviour, context);
    }

    public static void harvest(Level level, BlockPos pos, ItemStack harvestTool, float vfxChance, HarvestContext context, boolean takeASeed){
        MutableBoolean seedSubtracted = new MutableBoolean(!takeASeed);
        BlockState state = level.getBlockState(pos);
        FluidState fluidState = level.getFluidState(pos);

        if (level.random.nextFloat() < vfxChance)
            level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, pos, Block.getId(state));
        BlockEntity blockEntity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;

        if (level instanceof ServerLevel serverLevel && level.getGameRules()
                .getBoolean(GameRules.RULE_DOBLOCKDROPS) && !level.restoringBlockSnapshots) {
            List<ItemStack> drops = Block.getDrops(state, serverLevel, pos, blockEntity, context.movementContext.contraption.entity, harvestTool);

            BlockDropsEvent event = new BlockDropsEvent(serverLevel, pos, state, blockEntity, new ArrayList<>(), context.movementContext.contraption.entity, harvestTool);
            NeoForge.EVENT_BUS.post(event);
            if (!event.isCanceled()) {
                if (event.getDroppedExperience() > 0) {
                    state.getBlock().popExperience(serverLevel, pos, event.getDroppedExperience());
                }
            }

            for (ItemStack itemStack : drops) {
                if (takeASeed && !seedSubtracted.getValue() && ItemHelper.sameItem(itemStack, new ItemStack(state.getBlock()))) {
                    itemStack.shrink(1);
                    seedSubtracted.setTrue();
                }
                context.baseBehaviour.collectOrDropItem(context.movementContext, itemStack);
            }

            // Simulating IceBlock#playerDestroy. Not calling method directly as it would drop item
            // entities as a side-effect
            Registry<Enchantment> enchantmentRegistry = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
            if (state.getBlock() instanceof IceBlock && harvestTool.getEnchantmentLevel(enchantmentRegistry.getHolderOrThrow(Enchantments.SILK_TOUCH)) == 0) {
                if (!level.dimensionType().ultraWarm()) {
                    BlockState below = level.getBlockState(pos.below());
                    if (below.blocksMotion() || below.liquid()) {
                        fluidState = IceBlock.meltsInto().getFluidState();
                    }
                }
            }

            state.spawnAfterBreak(serverLevel, pos, ItemStack.EMPTY, false);
        }
        level.setBlockAndUpdate(pos, fluidState.createLegacyBlock());
    }

    public static boolean tryHarvest(Level level, BlockPos pos, BlockState state, HarvestContext context){
        boolean harvestPartial = AllConfigs.server().kinetics.harvestPartiallyGrown.get();
        boolean replant = AllConfigs.server().kinetics.harvesterReplants.get();
        Block block = state.getBlock();
        if(CALLBACKS.containsKey(block)){
            for (HarvestCallback harvestCallback : CALLBACKS.get(block)) {
                var result = harvestCallback.tryHarvest(level, pos, state, context, replant, harvestPartial);
                if(result != CallResult.PASS) return result.asBoolean();
            }
        }
        return CrimschistHarvesterBehavioursImpl.tryHarvestFallback(level, pos, state, context, replant, harvestPartial);
    }

    @FunctionalInterface
    public interface HarvestCallback {
        CallResult tryHarvest(Level level, BlockPos pos, BlockState state, HarvestContext context, boolean shouldReplenish, boolean harvestPartiallyGrown);
    }

    public record HarvestContext(MovementBehaviour baseBehaviour, MovementContext movementContext){

    }

    public enum CallResult {
        PASS, SUCCESS, FAIL;

        public boolean asBoolean(){
            return this == SUCCESS;
        }

        public static CallResult of(boolean flag){
            return flag ? SUCCESS : FAIL;
        }
    }
}
