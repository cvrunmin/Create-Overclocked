package io.github.cvrunmin.create_andertech.impl;

import com.simibubi.create.AllTags;
import com.simibubi.create.compat.Mods;
import com.simibubi.create.compat.farmersdelight.FarmersDelightCompat;
import io.github.cvrunmin.create_andertech.api.CrimschistHarvesterBehavioursAPI;
import io.github.cvrunmin.create_andertech.mixin.CropBlockAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.common.SpecialPlantable;

public class CrimschistHarvesterBehavioursImpl {

    private static boolean isValidCrop(Level world, BlockPos pos, BlockState state, boolean harvestPartial, boolean replant) {
        if (state.getBlock() instanceof CropBlock crop) {
            if (harvestPartial)
                return state != crop.getStateForAge(0) || !replant;
            return crop.isMaxAge(state);
        }

        if (state.getCollisionShape(world, pos)
                .isEmpty() || state.getBlock() instanceof CocoaBlock) {
            for (Property<?> property : state.getProperties()) {
                if (!(property instanceof IntegerProperty ageProperty))
                    continue;
                if (!property.getName()
                        .equals(BlockStateProperties.AGE_1.getName()))
                    continue;
                int age = state.getValue(ageProperty);
                if (state.getBlock() instanceof SweetBerryBushBlock && age <= 1 && replant)
                    continue;
                if (age == 0 && replant || !harvestPartial && (ageProperty.getPossibleValues().size() - 1 != age))
                    continue;
                return true;
            }
        }

        return false;
    }

    private static boolean isValidOther(Level world, BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof CropBlock)
            return false;
        if (state.getBlock() instanceof SugarCaneBlock)
            return true;
        if (state.is(BlockTags.LEAVES))
            return true;
        if (state.getBlock() instanceof CocoaBlock)
            return state.getValue(CocoaBlock.AGE) == CocoaBlock.MAX_AGE;

        if (state.getCollisionShape(world, pos).isEmpty()) {
            if (state.getBlock() instanceof GrowingPlantBlock)
                return true;

            for (Property<?> property : state.getProperties()) {
                if (!(property instanceof IntegerProperty))
                    continue;
                if (!property.getName().equals(BlockStateProperties.AGE_1.getName()))
                    continue;
                return false;
            }

            if (state.getBlock() instanceof MushroomBlock && Mods.FARMERSDELIGHT.isLoaded()) {
                return FarmersDelightCompat.shouldHarvestMushroom(world, pos, state);
            }

            // TODO: 1.21.5-rc1+ change to VegetationBlock (https://github.com/neoforged/NeoForge/commit/9f6edae1894ad249a8719c4e1f14beda0fdedc72)
            if (state.getBlock() instanceof BushBlock)
                return true;
            if (state.getBlock() instanceof SpecialPlantable)
                return true;
        }

        return false;
    }

    private static BlockState cutCrop(Level world, BlockPos pos, BlockState state) {
        Block block = state.getBlock();
        if (block instanceof CropBlock crop) {
            BlockState newState = crop.getStateForAge(0);
            if (!newState.is(block))
                return newState;
            IntegerProperty ageProperty = ((CropBlockAccessor) crop).callGetAgeProperty();
            return state.setValue(ageProperty, 0);
        }
        if (state.is(Blocks.SWEET_BERRY_BUSH)) {
            return state.setValue(BlockStateProperties.AGE_3, 1);
        }
        if (AllTags.AllBlockTags.SUGAR_CANE_VARIANTS.matches(block) || block instanceof GrowingPlantBlock) {
            if (state.getFluidState().isEmpty())
                return Blocks.AIR.defaultBlockState();
            return state.getFluidState().createLegacyBlock();
        }
        if (state.getCollisionShape(world, pos).isEmpty() || block instanceof CocoaBlock) {
            for (Property<?> property : state.getProperties()) {
                if (!(property instanceof IntegerProperty maybeAgeProperty))
                    continue;
                if (!property.getName().equals(BlockStateProperties.AGE_1.getName()))
                    continue;
                return state.setValue(maybeAgeProperty, 0);
            }
        }

        if (state.getFluidState().isEmpty())
            return Blocks.AIR.defaultBlockState();
        return state.getFluidState().createLegacyBlock();
    }

    public static boolean tryHarvestFallback(Level level, BlockPos pos, BlockState state, CrimschistHarvesterBehavioursAPI.HarvestContext context, boolean shouldReplenish, boolean harvestPartiallyGrown){
        BlockState stateVisited = level.getBlockState(pos);
        if (stateVisited.isAir() || AllTags.AllBlockTags.NON_HARVESTABLE.matches(stateVisited))
            return false;
        boolean notCropButCuttable = false;

        if (!isValidCrop(level, pos, stateVisited, harvestPartiallyGrown, shouldReplenish)) {
            if (isValidOther(level, pos, stateVisited))
                notCropButCuttable = true;
            else
                return false;
        }
        ItemStack item = ItemStack.EMPTY;
        float effectChance = 1;

        if (stateVisited.is(BlockTags.LEAVES)) {
            item = new ItemStack(Items.SHEARS);
            effectChance = .45f;
        }
        CrimschistHarvesterBehavioursAPI.harvest(level, pos, item, effectChance, context, !notCropButCuttable && shouldReplenish);
        if(shouldReplenish){
            var afterState = cutCrop(level, pos, state);
            level.setBlockAndUpdate(pos, afterState.canSurvive(level, pos) ? afterState : Blocks.AIR.defaultBlockState());
        }
        return true;
    }
}
