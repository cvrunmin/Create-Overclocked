package io.github.cvrunmin.create_andertech.content.kinetics.deployer;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.Create;
import com.simibubi.create.content.equipment.sandPaper.SandPaperPolishingRecipe;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipe;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.advancement.CreateAdvancement;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.List;
import java.util.stream.Collectors;

import static com.simibubi.create.content.kinetics.base.DirectionalKineticBlock.FACING;

public class OCDeployerBeltCallbacks {
    public static BeltProcessingBehaviour.ProcessingResult onItemReceived(TransportedItemStack s, TransportedItemStackHandlerBehaviour i,
                                                                          ChronenditeDeployerBlockEntity blockEntity) {

        if (blockEntity.getSpeed() == 0)
            return BeltProcessingBehaviour.ProcessingResult.PASS;
        if (blockEntity.mode == ChronenditeDeployerBlockEntity.Mode.PUNCH)
            return BeltProcessingBehaviour.ProcessingResult.PASS;
        BlockState blockState = blockEntity.getBlockState();
        if (!blockState.hasProperty(FACING) || blockState.getValue(FACING) != Direction.DOWN)
            return BeltProcessingBehaviour.ProcessingResult.PASS;
        if (blockEntity.state != ChronenditeDeployerBlockEntity.State.WAITING)
            return BeltProcessingBehaviour.ProcessingResult.HOLD;
        if (blockEntity.redstoneLocked)
            return BeltProcessingBehaviour.ProcessingResult.PASS;

        OCDeployerFakePlayer player = blockEntity.getPlayer();
        ItemStack held = player == null ? ItemStack.EMPTY : player.getMainHandItem();

        if (held.isEmpty())
            return BeltProcessingBehaviour.ProcessingResult.HOLD;
        if (blockEntity.getRecipe(s.stack) == null)
            return BeltProcessingBehaviour.ProcessingResult.PASS;

        blockEntity.start();
        return BeltProcessingBehaviour.ProcessingResult.HOLD;
    }

    public static BeltProcessingBehaviour.ProcessingResult whenItemHeld(TransportedItemStack s, TransportedItemStackHandlerBehaviour i,
                                                                        ChronenditeDeployerBlockEntity blockEntity) {

        if (blockEntity.getSpeed() == 0)
            return BeltProcessingBehaviour.ProcessingResult.PASS;
        BlockState blockState = blockEntity.getBlockState();
        if (!blockState.hasProperty(FACING) || blockState.getValue(FACING) != Direction.DOWN)
            return BeltProcessingBehaviour.ProcessingResult.PASS;

        OCDeployerFakePlayer player = blockEntity.getPlayer();
        ItemStack held = player == null ? ItemStack.EMPTY : player.getMainHandItem();
        if (held.isEmpty())
            return BeltProcessingBehaviour.ProcessingResult.HOLD;

        Recipe<?> recipe = blockEntity.getRecipe(s.stack);
        if (recipe == null)
            return BeltProcessingBehaviour.ProcessingResult.PASS;

        if (blockEntity.state == ChronenditeDeployerBlockEntity.State.RETRACTING && blockEntity.timer == 1000) {
            activate(s, i, blockEntity, recipe);
            return BeltProcessingBehaviour.ProcessingResult.HOLD;
        }

        if (blockEntity.state == ChronenditeDeployerBlockEntity.State.WAITING) {
            if (blockEntity.redstoneLocked)
                return BeltProcessingBehaviour.ProcessingResult.PASS;
            blockEntity.start();
        }

        return BeltProcessingBehaviour.ProcessingResult.HOLD;
    }

    public static void activate(TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler,
                                ChronenditeDeployerBlockEntity blockEntity, Recipe<?> recipe) {

        List<TransportedItemStack> collect =
                RecipeApplier.applyRecipeOn(blockEntity.getLevel(), ItemHandlerHelper.copyStackWithSize(transported.stack, 1), recipe)
                        .stream()
                        .map(stack -> {
                            TransportedItemStack copy = transported.copy();
                            boolean centered = BeltHelper.isItemUpright(stack);
                            copy.stack = stack;
                            copy.locked = true;
                            copy.angle = centered ? 180 : Create.RANDOM.nextInt(360);
                            return copy;
                        })
                        .map(t -> {
                            t.locked = false;
                            return t;
                        })
                        .collect(Collectors.toList());

        blockEntity.award(AllAdvancements.DEPLOYER);

        transported.clearFanProcessingData();

        TransportedItemStack left = transported.copy();
        blockEntity.player.spawnedItemEffects = transported.stack.copy();
        left.stack.shrink(1);
        ItemStack resultItem = null;

        if (collect.isEmpty()) {
            resultItem = left.stack.copy();
            handler.handleProcessingOnItem(transported, TransportedItemStackHandlerBehaviour.TransportedResult.convertTo(left));
        } else {
            resultItem = collect.get(0).stack.copy();
            handler.handleProcessingOnItem(transported, TransportedItemStackHandlerBehaviour.TransportedResult.convertToAndLeaveHeld(collect, left));
        }

        ItemStack heldItem = blockEntity.player.getMainHandItem();
        boolean keepHeld =
                recipe instanceof ItemApplicationRecipe && ((ItemApplicationRecipe) recipe).shouldKeepHeldItem();

        if (!keepHeld) {
            if (heldItem.isDamageableItem())
                heldItem.hurtAndBreak(1, blockEntity.player,
                        s -> s.broadcastBreakEvent(InteractionHand.MAIN_HAND));
            else
                heldItem.shrink(1);
        }

        if (resultItem != null && !resultItem.isEmpty())
            awardAdvancements(blockEntity, resultItem);

        BlockPos pos = blockEntity.getBlockPos();
        Level world = blockEntity.getLevel();
        if (heldItem.isEmpty())
            world.playSound(null, pos, SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, .25f, 1);
        world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, .25f, .75f);
        if (recipe instanceof SandPaperPolishingRecipe)
            AllSoundEvents.SANDING_SHORT.playOnServer(world, pos, .35f, 1f);

        blockEntity.notifyUpdate();
    }

    private static void awardAdvancements(ChronenditeDeployerBlockEntity blockEntity, ItemStack created) {
        CreateAdvancement advancement = null;

        if (AllBlocks.ANDESITE_CASING.isIn(created))
            advancement = AllAdvancements.ANDESITE_CASING;
        else if (AllBlocks.BRASS_CASING.isIn(created))
            advancement = AllAdvancements.BRASS_CASING;
        else if (AllBlocks.COPPER_CASING.isIn(created))
            advancement = AllAdvancements.COPPER_CASING;
        else if (AllBlocks.RAILWAY_CASING.isIn(created))
            advancement = AllAdvancements.TRAIN_CASING;
        else
            return;

        blockEntity.award(advancement);
    }
}
