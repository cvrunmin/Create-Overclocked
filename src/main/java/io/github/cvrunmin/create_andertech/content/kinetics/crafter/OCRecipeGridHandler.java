package io.github.cvrunmin.create_andertech.content.kinetics.crafter;

import com.google.common.base.Predicates;
import io.github.cvrunmin.create_andertech.AllBlocks;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

}
