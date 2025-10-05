package io.github.cvrunmin.create_andertech.content.kinetics.belt;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.item.BeltConnectorItem;
import com.simibubi.create.content.kinetics.simpleRelays.AbstractSimpleShaftBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.simpleRelays.CrimschistShaftBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class OCBeltConnectorHelper {
    public static boolean isSameTypeOfShaft(Level world, BlockPos first, BlockPos second){
        BlockState firstState = world.getBlockState(first);
        BlockState secondState = world.getBlockState(second);
        return firstState.getBlock() == secondState.getBlock();
    }

    public static boolean canConnectAsSpeedingBelt(Level world, BlockPos first, BlockPos second) {
        if (!world.isLoaded(first) || !world.isLoaded(second))
            return false;
        if (!second.closerThan(first, BeltConnectorItem.maxLength()))
            return false;

        BlockPos diff = second.subtract(first);
        BlockState firstState = world.getBlockState(first);
        if(!isSameTypeOfShaft(world, first, second)) return false;
        Direction.Axis shaftAxis = firstState.getValue(BlockStateProperties.AXIS);

        int x = diff.getX();
        int y = diff.getY();
        int z = diff.getZ();
        int sames = ((Math.abs(x) == Math.abs(y)) ? 1 : 0) + ((Math.abs(y) == Math.abs(z)) ? 1 : 0)
                + ((Math.abs(z) == Math.abs(x)) ? 1 : 0);

        if (shaftAxis.choose(x, y, z) != 0) {
            return false;
        }
        if (sames != 1) {
            return false;
        }
        BlockState secondState = world.getBlockState(second);
        if (shaftAxis != secondState.getValue(BlockStateProperties.AXIS))
            return false;
        if (shaftAxis == Direction.Axis.Y && x != 0 && z != 0)
            return false;

        BlockEntity blockEntity = world.getBlockEntity(first);
        BlockEntity blockEntity2 = world.getBlockEntity(second);

        if (!(blockEntity instanceof KineticBlockEntity))
            return false;
        if (!(blockEntity2 instanceof KineticBlockEntity))
            return false;

        float speed1 = ((KineticBlockEntity) blockEntity).getTheoreticalSpeed();
        float speed2 = ((KineticBlockEntity) blockEntity2).getTheoreticalSpeed();
        if (Math.signum(speed1) != Math.signum(speed2) && speed1 != 0 && speed2 != 0)
            return false;

        BlockPos step = BlockPos.containing(Math.signum(diff.getX()), Math.signum(diff.getY()), Math.signum(diff.getZ()));
        int limit = 1000;
        for (BlockPos currentPos = first.offset(step); !currentPos.equals(second) && limit-- > 0; currentPos =
                currentPos.offset(step)) {
            BlockState blockState = world.getBlockState(currentPos);
            if (CrimschistShaftBlock.isShaft(blockState) && blockState.getValue(AbstractSimpleShaftBlock.AXIS) == shaftAxis)
                continue;
            if (!blockState.canBeReplaced())
                return false;
        }

        return true;

    }
}
