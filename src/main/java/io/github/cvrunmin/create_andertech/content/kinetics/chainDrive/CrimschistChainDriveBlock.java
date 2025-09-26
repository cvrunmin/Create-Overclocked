package io.github.cvrunmin.create_andertech.content.kinetics.chainDrive;

import com.simibubi.create.api.contraption.transformable.TransformableBlock;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.content.kinetics.chainDrive.ChainDriveBlock;
import com.simibubi.create.content.kinetics.chainDrive.ChainGearshiftBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import io.github.cvrunmin.create_andertech.AllBlockEntityTypes;
import io.github.cvrunmin.create_andertech.content.kinetics.simpleRelays.OCKineticBlockEntity;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.lang.Lang;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.PushReaction;

public class CrimschistChainDriveBlock extends RotatedPillarKineticBlock
        implements IBE<OCKineticBlockEntity>, TransformableBlock {

    public static final Property<CrimschistChainDriveBlock.Part> PART = EnumProperty.create("part", CrimschistChainDriveBlock.Part.class);
    public static final BooleanProperty CONNECTED_ALONG_FIRST_COORDINATE =
            DirectionalAxisKineticBlock.AXIS_ALONG_FIRST_COORDINATE;

    public CrimschistChainDriveBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(PART, CrimschistChainDriveBlock.Part.NONE));
    }

    @Override
    public boolean shouldCheckWeakPower(BlockState state, SignalGetter level, BlockPos pos, Direction side) {
        return false;
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.NORMAL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(PART, CONNECTED_ALONG_FIRST_COORDINATE));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction.Axis placedAxis = context.getNearestLookingDirection()
                .getAxis();
        Direction.Axis axis = context.getPlayer() != null && context.getPlayer()
                .isShiftKeyDown() ? placedAxis : getPreferredAxis(context);
        if (axis == null)
            axis = placedAxis;

        BlockState state = defaultBlockState().setValue(AXIS, axis);
        for (Direction facing : Iterate.directions) {
            if (facing.getAxis() == axis)
                continue;
            BlockPos pos = context.getClickedPos();
            BlockPos offset = pos.relative(facing);
            state = updateShape(state, facing, context.getLevel()
                    .getBlockState(offset), context.getLevel(), pos, offset);
        }
        return state;
    }

    private BlockState updateShapeWithNormalVariant(BlockState stateIn, BlockState neighbour, Direction.Axis axis, Direction.Axis faceAxis, Part part, boolean positive, boolean facingAlongFirst, Direction.Axis connectionAxis){
        Part otherPart = neighbour.getValue(PART);
        boolean connectionAlongFirst = stateIn.getValue(CONNECTED_ALONG_FIRST_COORDINATE);
        Direction.Axis otherAxis = neighbour.getValue(AXIS);
        boolean otherConnection = neighbour.getValue(CONNECTED_ALONG_FIRST_COORDINATE);
        Direction.Axis otherConnectionAxis =
                otherConnection ? (otherAxis == Direction.Axis.X ? Direction.Axis.Y : Direction.Axis.X) : (otherAxis == Direction.Axis.Z ? Direction.Axis.Y : Direction.Axis.Z);

        if (neighbour.getValue(AXIS) == faceAxis)
            return stateIn;
        if (otherPart != Part.NONE && otherConnectionAxis != faceAxis)
            return stateIn;

        if (part == Part.NONE) {
            part = positive ? Part.START : Part.END;
            connectionAlongFirst = axis == Direction.Axis.X ? faceAxis.isVertical() : faceAxis == Direction.Axis.X;
        } else if (connectionAxis != faceAxis) {
            return stateIn;
        }

        if ((part == Part.START) != positive)
            part = Part.MIDDLE;

        return stateIn.setValue(PART, part)
                .setValue(CONNECTED_ALONG_FIRST_COORDINATE, connectionAlongFirst);
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction face, BlockState neighbour, LevelAccessor worldIn,
                                  BlockPos currentPos, BlockPos facingPos) {
        CrimschistChainDriveBlock.Part part = stateIn.getValue(PART);
        Direction.Axis axis = stateIn.getValue(AXIS);
        boolean connectionAlongFirst = stateIn.getValue(CONNECTED_ALONG_FIRST_COORDINATE);
        Direction.Axis connectionAxis =
                connectionAlongFirst ? (axis == Direction.Axis.X ? Direction.Axis.Y : Direction.Axis.X) : (axis == Direction.Axis.Z ? Direction.Axis.Y : Direction.Axis.Z);

        Direction.Axis faceAxis = face.getAxis();
        boolean facingAlongFirst = axis == Direction.Axis.X ? faceAxis.isVertical() : faceAxis == Direction.Axis.X;
        boolean positive = face.getAxisDirection() == Direction.AxisDirection.POSITIVE;

        if (axis == faceAxis)
            return stateIn;

        if (!(neighbour.getBlock() instanceof CrimschistChainDriveBlock || neighbour.getBlock() instanceof ChainDriveBlock)) {
            if (facingAlongFirst != connectionAlongFirst || part == Part.NONE)
                return stateIn;
            if (part == Part.MIDDLE)
                return stateIn.setValue(PART, positive ? Part.END : Part.START);
            if ((part == Part.START) == positive)
                return stateIn.setValue(PART, Part.NONE);
            return stateIn;
        }

        if(neighbour.getBlock() instanceof ChainDriveBlock){
            return updateShapeWithNormalVariant(stateIn, neighbour, axis, faceAxis, part, positive, facingAlongFirst, connectionAxis);
        }

        Part otherPart = neighbour.getValue(PART);
        Direction.Axis otherAxis = neighbour.getValue(AXIS);
        boolean otherConnection = neighbour.getValue(CONNECTED_ALONG_FIRST_COORDINATE);
        Direction.Axis otherConnectionAxis =
                otherConnection ? (otherAxis == Direction.Axis.X ? Direction.Axis.Y : Direction.Axis.X) : (otherAxis == Direction.Axis.Z ? Direction.Axis.Y : Direction.Axis.Z);

        if (neighbour.getValue(AXIS) == faceAxis)
            return stateIn;
        if (otherPart != Part.NONE && otherConnectionAxis != faceAxis)
            return stateIn;

        if (part == Part.NONE) {
            part = positive ? Part.START : Part.END;
            connectionAlongFirst = axis == Direction.Axis.X ? faceAxis.isVertical() : faceAxis == Direction.Axis.X;
        } else if (connectionAxis != faceAxis) {
            return stateIn;
        }

        if ((part == Part.START) != positive)
            part = Part.MIDDLE;

        return stateIn.setValue(PART, part)
                .setValue(CONNECTED_ALONG_FIRST_COORDINATE, connectionAlongFirst);
    }

    @Override
    public BlockState getRotatedBlockState(BlockState originalState, Direction targetedFace) {
        if (originalState.getValue(PART) == CrimschistChainDriveBlock.Part.NONE)
            return super.getRotatedBlockState(originalState, targetedFace);
        return super.getRotatedBlockState(originalState,
                Direction.get(Direction.AxisDirection.POSITIVE, getConnectionAxis(originalState)));
    }

    @Override
    public BlockState updateAfterWrenched(BlockState newState, UseOnContext context) {
        Direction.Axis axis = newState.getValue(AXIS);
        newState = defaultBlockState().setValue(AXIS, axis);
        if (newState.hasProperty(BlockStateProperties.POWERED))
            newState = newState.setValue(BlockStateProperties.POWERED, context.getLevel()
                    .hasNeighborSignal(context.getClickedPos()));
        for (Direction facing : Iterate.directions) {
            if (facing.getAxis() == axis)
                continue;
            BlockPos pos = context.getClickedPos();
            BlockPos offset = pos.relative(facing);
            newState = updateShape(newState, facing, context.getLevel()
                    .getBlockState(offset), context.getLevel(), pos, offset);
        }
        return newState;
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == state.getValue(AXIS);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(AXIS);
    }

    public static boolean areBlocksConnected(BlockState state, BlockState other, Direction facing) {
        CrimschistChainDriveBlock.Part part = state.getValue(PART);
        Direction.Axis connectionAxis = getConnectionAxis(state);
        Direction.Axis otherConnectionAxis = getConnectionAxis(other);

        if (otherConnectionAxis != connectionAxis)
            return false;
        if (facing.getAxis() != connectionAxis)
            return false;
        if (facing.getAxisDirection() == Direction.AxisDirection.POSITIVE && (part == CrimschistChainDriveBlock.Part.MIDDLE || part == CrimschistChainDriveBlock.Part.START))
            return true;
        if (facing.getAxisDirection() == Direction.AxisDirection.NEGATIVE && (part == CrimschistChainDriveBlock.Part.MIDDLE || part == CrimschistChainDriveBlock.Part.END))
            return true;

        return false;
    }

    protected static Direction.Axis getConnectionAxis(BlockState state) {
        Direction.Axis axis = state.getValue(AXIS);
        boolean connectionAlongFirst = state.getValue(CONNECTED_ALONG_FIRST_COORDINATE);
        Direction.Axis connectionAxis =
                connectionAlongFirst ? (axis == Direction.Axis.X ? Direction.Axis.Y : Direction.Axis.X) : (axis == Direction.Axis.Z ? Direction.Axis.Y : Direction.Axis.Z);
        return connectionAxis;
    }

    public static float getRotationSpeedModifier(KineticBlockEntity from, KineticBlockEntity to) {
        float fromMod = 1;
        float toMod = 1;
        if (from instanceof ChainGearshiftBlockEntity)
            fromMod = ((ChainGearshiftBlockEntity) from).getModifier();
        if (to instanceof ChainGearshiftBlockEntity)
            toMod = ((ChainGearshiftBlockEntity) to).getModifier();
        return fromMod / toMod;
    }

    public enum Part implements StringRepresentable {
        START, MIDDLE, END, NONE;

        @Override
        public String getSerializedName() {
            return Lang.asId(name());
        }
    }

    @Override
    public Class<OCKineticBlockEntity> getBlockEntityClass() {
        return OCKineticBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends OCKineticBlockEntity> getBlockEntityType() {
        return AllBlockEntityTypes.ENCASED_CRIMSCHIST_SHAFT.get();
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return rotate(state, rot, Direction.Axis.Y);
    }

    protected BlockState rotate(BlockState pState, Rotation rot, Direction.Axis rotAxis) {
        Direction.Axis connectionAxis = getConnectionAxis(pState);
        Direction direction = Direction.fromAxisAndDirection(connectionAxis, Direction.AxisDirection.POSITIVE);
        Direction normal = Direction.fromAxisAndDirection(pState.getValue(AXIS), Direction.AxisDirection.POSITIVE);
        for (int i = 0; i < rot.ordinal(); i++) {
            direction = direction.getClockWise(rotAxis);
            normal = normal.getClockWise(rotAxis);
        }

        if (direction.getAxisDirection() == Direction.AxisDirection.NEGATIVE)
            pState = reversePart(pState);

        Direction.Axis newAxis = normal.getAxis();
        Direction.Axis newConnectingDirection = direction.getAxis();
        boolean alongFirst = newAxis == Direction.Axis.X && newConnectingDirection == Direction.Axis.Y
                || newAxis != Direction.Axis.X && newConnectingDirection == Direction.Axis.X;

        return pState.setValue(AXIS, newAxis)
                .setValue(CONNECTED_ALONG_FIRST_COORDINATE, alongFirst);
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        Direction.Axis connectionAxis = getConnectionAxis(pState);
        if (pMirror.mirror(Direction.fromAxisAndDirection(connectionAxis, Direction.AxisDirection.POSITIVE))
                .getAxisDirection() == Direction.AxisDirection.POSITIVE)
            return pState;
        return reversePart(pState);
    }

    protected BlockState reversePart(BlockState pState) {
        Part part = pState.getValue(PART);
        if (part == Part.START)
            return pState.setValue(PART, Part.END);
        if (part == Part.END)
            return pState.setValue(PART, Part.START);
        return pState;
    }

    @Override
    public BlockState transform(BlockState state, StructureTransform transform) {
        return rotate(mirror(state, transform.mirror), transform.rotation, transform.rotationAxis);
    }

}
