package io.github.cvrunmin.create_andertech.content.kinetics.crafter;

import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import static com.simibubi.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING;

public class OCCrafterCTBehaviour extends ConnectedTextureBehaviour.Base {

    @Override
    public boolean connectsTo(BlockState state, BlockState other, BlockAndTintGetter reader, BlockPos pos, BlockPos otherPos,
                              Direction face) {
        if (state.getBlock() != other.getBlock())
            return false;
        if (state.getValue(HORIZONTAL_FACING) != other.getValue(HORIZONTAL_FACING))
            return false;
        return OCCrafterHelper.areCraftersConnected(reader, pos, otherPos);
    }

    @Override
    protected boolean reverseUVs(BlockState state, Direction direction) {
        if (!direction.getAxis()
                .isVertical())
            return false;
        Direction facing = state.getValue(HORIZONTAL_FACING);
        if (facing.getAxis() == direction.getAxis())
            return false;

        boolean isNegative = facing.getAxisDirection() == Direction.AxisDirection.NEGATIVE;
        if (direction == Direction.DOWN && facing.getAxis() == Direction.Axis.Z)
            return !isNegative;
        return isNegative;
    }

    @Override
    public CTSpriteShiftEntry getShift(BlockState state, Direction direction,TextureAtlasSprite sprite) {
        Direction facing = state.getValue(HORIZONTAL_FACING);
        boolean isFront = facing.getAxis() == direction.getAxis();
        boolean isVertical = direction.getAxis()
                .isVertical();
        boolean facingX = facing.getAxis() == Direction.Axis.X;
        return isFront ? AllSpriteShifts.BRASS_CASING
                : isVertical && !facingX ? AllSpriteShifts.CRAFTER_OTHERSIDE : AllSpriteShifts.CRAFTER_SIDE;
    }
}
