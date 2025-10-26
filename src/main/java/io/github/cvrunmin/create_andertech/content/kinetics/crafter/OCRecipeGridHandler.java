package io.github.cvrunmin.create_andertech.content.kinetics.crafter;

import com.google.common.base.Predicates;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.crafter.MechanicalCraftingInventory;
import com.simibubi.create.infrastructure.config.AllConfigs;
import io.github.cvrunmin.create_andertech.AllBlocks;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.Pointing;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.FireworkRocketRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Predicate;

import static com.simibubi.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING;

/**
 * Same content with {@link com.simibubi.create.content.kinetics.crafter.RecipeGridHandler}, but target for {@link ChronenditeMechanicalCrafterBlockEntity}
 */
public class OCRecipeGridHandler {

    public static List<ChronenditeMechanicalCrafterBlockEntity> getAllCraftersOfChain(ChronenditeMechanicalCrafterBlockEntity root) {
        return getAllCraftersOfChainIf(root, Predicates.alwaysTrue());
    }

    public static List<ChronenditeMechanicalCrafterBlockEntity> getAllCraftersOfChainIf(ChronenditeMechanicalCrafterBlockEntity root,
                                                                             Predicate<ChronenditeMechanicalCrafterBlockEntity> test) {
        return getAllCraftersOfChainIf(root, test, false);
    }

    public static List<ChronenditeMechanicalCrafterBlockEntity> getAllCraftersOfChainIf(ChronenditeMechanicalCrafterBlockEntity root,
                                                                             Predicate<ChronenditeMechanicalCrafterBlockEntity> test, boolean poweredStart) {
        List<ChronenditeMechanicalCrafterBlockEntity> crafters = new ArrayList<>();
        List<Pair<ChronenditeMechanicalCrafterBlockEntity, ChronenditeMechanicalCrafterBlockEntity>> frontier = new ArrayList<>();
        Set<ChronenditeMechanicalCrafterBlockEntity> visited = new HashSet<>();
        frontier.add(Pair.of(root, null));

        boolean empty = false;
        boolean allEmpty = true;

        while (!frontier.isEmpty()) {
            Pair<ChronenditeMechanicalCrafterBlockEntity, ChronenditeMechanicalCrafterBlockEntity> pair = frontier.remove(0);
            ChronenditeMechanicalCrafterBlockEntity current = pair.getKey();
            ChronenditeMechanicalCrafterBlockEntity last = pair.getValue();

            if (visited.contains(current))
                return null;
            if (!(test.test(current)))
                empty = true;
            else
                allEmpty = false;

            crafters.add(current);
            visited.add(current);

            ChronenditeMechanicalCrafterBlockEntity target = getTargetingCrafter(current);
            if (target != last && target != null)
                frontier.add(Pair.of(target, current));
            for (ChronenditeMechanicalCrafterBlockEntity preceding : getPrecedingCrafters(current))
                if (preceding != last)
                    frontier.add(Pair.of(preceding, current));
        }

        return empty && !poweredStart || allEmpty ? null : crafters;
    }

    public static ChronenditeMechanicalCrafterBlockEntity getTargetingCrafter(ChronenditeMechanicalCrafterBlockEntity crafter) {
        BlockState state = crafter.getBlockState();
        if (!isCrafter(state))
            return null;

        BlockPos targetPos = crafter.getBlockPos()
                .relative(ChronenditeMechanicalCrafterBlock.getTargetDirection(state));
        ChronenditeMechanicalCrafterBlockEntity targetBE = OCCrafterHelper.getCrafter(crafter.getLevel(), targetPos);
        if (targetBE == null)
            return null;

        BlockState targetState = targetBE.getBlockState();
        if (!isCrafter(targetState))
            return null;
        if (state.getValue(HORIZONTAL_FACING) != targetState.getValue(HORIZONTAL_FACING))
            return null;
        return targetBE;
    }

    public static List<ChronenditeMechanicalCrafterBlockEntity> getPrecedingCrafters(ChronenditeMechanicalCrafterBlockEntity crafter) {
        BlockPos pos = crafter.getBlockPos();
        Level world = crafter.getLevel();
        List<ChronenditeMechanicalCrafterBlockEntity> crafters = new ArrayList<>();
        BlockState blockState = crafter.getBlockState();
        if (!isCrafter(blockState))
            return crafters;

        Direction blockFacing = blockState.getValue(HORIZONTAL_FACING);
        Direction blockPointing = ChronenditeMechanicalCrafterBlock.getTargetDirection(blockState);
        for (Direction facing : Iterate.directions) {
            if (blockFacing.getAxis() == facing.getAxis())
                continue;
            if (blockPointing == facing)
                continue;

            BlockPos neighbourPos = pos.relative(facing);
            BlockState neighbourState = world.getBlockState(neighbourPos);
            if (!isCrafter(neighbourState))
                continue;
            if (ChronenditeMechanicalCrafterBlock.getTargetDirection(neighbourState) != facing.getOpposite())
                continue;
            if (blockFacing != neighbourState.getValue(HORIZONTAL_FACING))
                continue;
            ChronenditeMechanicalCrafterBlockEntity be = OCCrafterHelper.getCrafter(world, neighbourPos);
            if (be == null)
                continue;

            crafters.add(be);
        }

        return crafters;
    }

    private static boolean isCrafter(BlockState state) {
        return AllBlocks.CHRONENDITE_MECHANICAL_CRAFTER.has(state);
    }

    public static ItemStack tryToApplyRecipe(Level world, GroupedItems items) {
        items.calcStats();
        CraftingContainer craftinginventory = new OCMechanicalCraftingInventory(items);
        ItemStack result = null;
        RegistryAccess registryAccess = world.registryAccess();
        if (AllConfigs.server().recipes.allowRegularCraftingInCrafter.get())
            result = world.getRecipeManager()
                    .getRecipeFor(RecipeType.CRAFTING, craftinginventory, world)
                    .filter(r -> isRecipeAllowed(r, craftinginventory))
                    .map(r -> r.assemble(craftinginventory, registryAccess))
                    .orElse(null);
        if (result == null)
            result = AllRecipeTypes.MECHANICAL_CRAFTING.find(craftinginventory, world)
                    .map(r -> r.assemble(craftinginventory, registryAccess))
                    .orElse(null);
        return result;
    }

    public static boolean isRecipeAllowed(CraftingRecipe recipe, CraftingContainer inventory) {
        if (recipe instanceof FireworkRocketRecipe) {
            int numItems = 0;
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                if (!inventory.getItem(i).isEmpty()) {
                    numItems++;
                }
            }
            if (numItems > AllConfigs.server().recipes.maxFireworkIngredientsInCrafter.get()) {
                return false;
            }
        }
        if (AllRecipeTypes.shouldIgnoreInAutomation(recipe))
            return false;
        return true;
    }

    public static class GroupedItems {
        Map<Pair<Integer, Integer>, ItemStack> grid = new HashMap<>();
        int minX, minY, maxX, maxY, width, height;
        boolean statsReady;

        public GroupedItems() {}

        public GroupedItems(ItemStack stack) {
            grid.put(Pair.of(0, 0), stack);
        }

        public void mergeOnto(GroupedItems other, Pointing pointing) {
            int xOffset = pointing == Pointing.LEFT ? 1 : pointing == Pointing.RIGHT ? -1 : 0;
            int yOffset = pointing == Pointing.DOWN ? 1 : pointing == Pointing.UP ? -1 : 0;
            grid.forEach(
                    (pair, stack) -> other.grid.put(Pair.of(pair.getKey() + xOffset, pair.getValue() + yOffset), stack));
            other.statsReady = false;
        }

        public void write(CompoundTag nbt) {
            ListTag gridNBT = new ListTag();
            grid.forEach((pair, stack) -> {
                CompoundTag entry = new CompoundTag();
                entry.putInt("x", pair.getKey());
                entry.putInt("y", pair.getValue());
                entry.put("item", stack.serializeNBT());
                gridNBT.add(entry);
            });
            nbt.put("Grid", gridNBT);
        }

        public static GroupedItems read(CompoundTag nbt) {
            GroupedItems items = new GroupedItems();
            ListTag gridNBT = nbt.getList("Grid", Tag.TAG_COMPOUND);
            gridNBT.forEach(inbt -> {
                CompoundTag entry = (CompoundTag) inbt;
                int x = entry.getInt("x");
                int y = entry.getInt("y");
                ItemStack stack = ItemStack.of(entry.getCompound("item"));
                items.grid.put(Pair.of(x, y), stack);
            });
            return items;
        }

        public void calcStats() {
            if (statsReady)
                return;
            statsReady = true;

            minX = 0;
            minY = 0;
            maxX = 0;
            maxY = 0;

            for (Pair<Integer, Integer> pair : grid.keySet()) {
                int x = pair.getKey();
                int y = pair.getValue();
                minX = Math.min(minX, x);
                minY = Math.min(minY, y);
                maxX = Math.max(maxX, x);
                maxY = Math.max(maxY, y);
            }

            width = maxX - minX + 1;
            height = maxY - minY + 1;
        }

        public boolean onlyEmptyItems() {
            for (ItemStack stack : grid.values())
                if (!stack.isEmpty())
                    return false;
            return true;
        }

    }

}
