package io.github.cvrunmin.create_andertech.content.kinetics.transmission;

import com.simibubi.create.api.contraption.transformable.TransformableBlock;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.foundation.block.IBE;
import io.github.cvrunmin.create_andertech.index.AllBlockEntityTypes;
import net.createmod.catnip.lang.Lang;
import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * A three port rotational differential, dressed as an encased large cogwheel.
 * <p>
 * Two shaft ends (NEG/POS on the rotation axis) and the large cog ring (COG) exchange rotation through
 * {@code wNEG + wPOS = 2 * wCOG}; a side set to NONE is capped, has no connection at all and counts as 0. Exactly
 * one side has to be OUT and at least one has to be IN for the block to do anything - see
 * docs/blackbox_differential_plan.md for the complete table and the propagation model behind it.
 */
public class CrimschistBlackboxDifferentialBlock extends RotatedPillarKineticBlock
        implements ICogWheel, IBE<CrimschistBlackboxDifferentialBlockEntity>, TransformableBlock {

    public static final EnumProperty<Mode> NEGATIVE_MODE = EnumProperty.create("negative_mode", Mode.class);
    public static final EnumProperty<Mode> POSITIVE_MODE = EnumProperty.create("positive_mode", Mode.class);

    public CrimschistBlackboxDifferentialBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(NEGATIVE_MODE, Mode.NONE)
                .setValue(POSITIVE_MODE, Mode.NONE));
    }

    public enum Mode implements StringRepresentable {
        NONE, IN, OUT;

        @Override
        public String getSerializedName() {
            return Lang.asId(name());
        }
    }

    public enum Port {
        NEG, POS, COG
    }

    // Ports

    public static Mode positiveMode(BlockState state){
        return state.getValue(POSITIVE_MODE);
    }

    public static Mode negativeMode(BlockState state){
        return state.getValue(NEGATIVE_MODE);
    }

    public static Mode cogMode(BlockState state){
        var pos = positiveMode(state);
        var neg = negativeMode(state);
        if(pos != Mode.OUT && neg != Mode.OUT){
            return Mode.OUT;
        }
        return Mode.IN;
    }

    public static Mode modeOf(BlockState state, Port port) {
        return switch (port){
            case NEG -> negativeMode(state);
            case POS -> positiveMode(state);
            case COG -> cogMode(state);
        };
    }

    public static Direction directionOf(Direction.Axis axis, boolean positive) {
        return Direction.get(positive ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE, axis);
    }

    /** The port a given face of this block belongs to; perpendicular faces all address the ring. */
    public static Port portForFace(BlockState state, Direction face) {
        if (face.getAxis() != state.getValue(AXIS))
            return Port.COG;
        return face.getAxisDirection() == Direction.AxisDirection.POSITIVE ? Port.POS : Port.NEG;
    }

    public static Optional<Port> portOf(BlockState state, BlockPos self, BlockPos other){
        BlockPos diff = other.subtract(self);
        Direction.Axis axis = state.getValue(AXIS);
        int alongAxis = axis.choose(diff.getX(), diff.getY(), diff.getZ());
        if (alongAxis != 0) {
            for (Direction.Axis perpendicular : Direction.Axis.values())
                if (perpendicular != axis && perpendicular.choose(diff.getX(), diff.getY(), diff.getZ()) != 0) {
                    if(diff.distManhattan(BlockPos.ZERO) == 2){
                        return Optional.of(Port.COG);
                    }
                    return Optional.empty();
                }
            boolean isPositive = alongAxis > 0;
            if(isPositive) return Optional.of(Port.POS);
            else return Optional.of(Port.NEG);
        }
        if(diff.distManhattan(BlockPos.ZERO) == 2){
            return Optional.of(Port.COG);
        }
        return Optional.empty();
    }

    public static boolean isInputPort(BlockState state, BlockPos self, BlockPos other) {
        var portOpt = portOf(state, self, other);
        return portOpt.map(port -> modeOf(state, port)).orElse(Mode.NONE) == Mode.IN;
    }

    public static boolean isOutputPort(BlockState state, BlockPos self, BlockPos other) {
        var portOpt = portOf(state, self, other);
        return portOpt.map(port -> modeOf(state, port)).orElse(Mode.NONE) == Mode.OUT;
    }

    public static boolean hasShaftOn(BlockState state, Direction face) {
        if (face.getAxis() == state.getValue(AXIS)) {
            EnumProperty<Mode> property = face.getAxisDirection() == Direction.AxisDirection.NEGATIVE ? NEGATIVE_MODE : POSITIVE_MODE;
            return state.getValue(property) != Mode.NONE;
        }
        return false;
    }

    // IRotate / ICogWheel

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return hasShaftOn(state, face);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(AXIS);
    }

    @Override
    public boolean isLargeCog() {
        return true;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return CogWheelBlock.isValidCogwheelPosition(true, level, pos, state.getValue(AXIS));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(NEGATIVE_MODE, POSITIVE_MODE);
    }

    // Placement

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        BlockState placedOn = context.getLevel()
                .getBlockState(context.getClickedPos()
                        .relative(context.getClickedFace()
                                .getOpposite()));
        if (ICogWheel.isSmallCog(placedOn))
            state = state.setValue(AXIS, ((IRotate) placedOn.getBlock()).getRotationAxis(placedOn));
        return state;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level.isClientSide)
            return;
        if (!(level.getBlockEntity(pos) instanceof CrimschistBlackboxDifferentialBlockEntity differential))
            return;
        var axis = state.getValue(AXIS);
        var negNeighbour = pos.relative(axis, -1);
        var negBlockState = level.getBlockState(negNeighbour);
        if(negBlockState.getBlock() instanceof IRotate negRotate){
            if(negRotate.hasShaftTowards(level, negNeighbour, negBlockState, Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE))){
                state = state.setValue(NEGATIVE_MODE, Mode.IN);
            }
        }
        var posNeighbour = pos.relative(axis, 1);
        var posBlockState = level.getBlockState(negNeighbour);
        if(posBlockState.getBlock() instanceof IRotate posRotate){
            if(posRotate.hasShaftTowards(level, posNeighbour, posBlockState, Direction.fromAxisAndDirection(axis, Direction.AxisDirection.NEGATIVE))){
                state = state.setValue(POSITIVE_MODE, Mode.IN);
            }
        }
        KineticBlockEntity.switchToBlockState(level, pos, state);
    }

    // Interaction

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        if (context.getClickedFace().getAxis() != state.getValue(AXIS))
            return super.onWrenched(state, context);
        if(context.getClickedFace().getAxisDirection() == Direction.AxisDirection.NEGATIVE)
            return cycleNegativeMode(state, context.getLevel(), context.getClickedPos());
        return cyclePositiveMode(state, context.getLevel(), context.getClickedPos());
    }

    private static InteractionResult cycleNegativeMode(BlockState state, Level level, BlockPos pos){
        if(!level.isClientSide){
            var posMode = state.getValue(POSITIVE_MODE);
            BlockState newState;
            if(posMode == Mode.NONE){
                var cycledMode = state.getValue(NEGATIVE_MODE) == Mode.IN ? Mode.OUT : Mode.IN;
                newState = state.setValue(NEGATIVE_MODE, cycledMode);
            }else if(posMode == Mode.OUT){
                var cycledMode = state.getValue(NEGATIVE_MODE) == Mode.IN ? Mode.NONE : Mode.IN;
                newState = state.setValue(NEGATIVE_MODE, cycledMode);
            }
            else{
                newState = state.cycle(NEGATIVE_MODE);
            }
            KineticBlockEntity.switchToBlockState(level, pos, newState);
            IWrenchable.playRotateSound(level, pos);
        }
        return InteractionResult.SUCCESS;
    }

    private static InteractionResult cyclePositiveMode(BlockState state, Level level, BlockPos pos){
        if(!level.isClientSide){
            var negMode = state.getValue(NEGATIVE_MODE);
            BlockState newState;
            if(negMode == Mode.NONE){
                var cycledMode = state.getValue(POSITIVE_MODE) == Mode.IN ? Mode.OUT : Mode.IN;
                newState = state.setValue(POSITIVE_MODE, cycledMode);
            } else if (negMode == Mode.OUT) {
                var cycledMode = state.getValue(POSITIVE_MODE) == Mode.IN ? Mode.NONE : Mode.IN;
                newState = state.setValue(POSITIVE_MODE, cycledMode);
            } else{
                newState = state.cycle(POSITIVE_MODE);
            }
            KineticBlockEntity.switchToBlockState(level, pos, newState);
            IWrenchable.playRotateSound(level, pos);
        }
        return InteractionResult.SUCCESS;
    }

    // Transforms

    @Override
    protected boolean areStatesKineticallyEquivalent(BlockState oldState, BlockState newState) {
        if (newState.getBlock() instanceof CrimschistBlackboxDifferentialBlock) {
            if(positiveMode(oldState) != positiveMode(newState) || negativeMode(oldState) != negativeMode(newState))
                return false;
        }
        return super.areStatesKineticallyEquivalent(oldState, newState);
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        if (!(adjacentBlockState.getBlock() instanceof CrimschistBlackboxDifferentialBlock))
            return false;
        if (adjacentBlockState.getValue(AXIS) != state.getValue(AXIS) || side.getAxis() != state.getValue(AXIS))
            return false;
        return hasShaftOn(state, side) == hasShaftOn(adjacentBlockState, side.getOpposite());
    }

    private static BlockState swapModes(BlockState state) {
        Mode negative = state.getValue(NEGATIVE_MODE);
        Mode positive = state.getValue(POSITIVE_MODE);
        return state.setValue(NEGATIVE_MODE, positive)
                .setValue(POSITIVE_MODE, negative);
    }

    /**
     * A rotation or mirror that reverses the axis also swaps which physical end is the positive one, so the two end
     * modes have to travel with it. The constraint is symmetric in wNEG/wPOS, so a functional state stays functional.
     */
    private static BlockState swapModesForRotation(BlockState state, Rotation rotation, Direction.Axis rotationAxis) {
        if (rotation == Rotation.NONE)
            return state;

        Direction.Axis axis = state.getValue(AXIS);
        if (axis == rotationAxis)
            return state;

        if (rotation == Rotation.CLOCKWISE_180)
            return swapModes(state);

        boolean clockwise = rotation == Rotation.CLOCKWISE_90;

        if (rotationAxis == Direction.Axis.X) {
            if (axis == Direction.Axis.Z && !clockwise || axis == Direction.Axis.Y && clockwise)
                return swapModes(state);
        } else if (rotationAxis == Direction.Axis.Y) {
            if (axis == Direction.Axis.X && !clockwise || axis == Direction.Axis.Z && clockwise)
                return swapModes(state);
        } else if (rotationAxis == Direction.Axis.Z) {
            if (axis == Direction.Axis.Y && !clockwise || axis == Direction.Axis.X && clockwise)
                return swapModes(state);
        }

        return state;
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        Direction.Axis axis = state.getValue(AXIS);
        if (axis == Direction.Axis.X && mirror == Mirror.FRONT_BACK
                || axis == Direction.Axis.Z && mirror == Mirror.LEFT_RIGHT)
            return swapModes(state);
        return state;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        state = swapModesForRotation(state, rotation, Direction.Axis.Y);
        return super.rotate(state, rotation);
    }

    @Override
    public BlockState getRotatedBlockState(BlockState originalState, Direction targetedFace) {
        originalState = swapModesForRotation(originalState, Rotation.CLOCKWISE_90, targetedFace.getAxis());
        return originalState.setValue(AXIS, VoxelShaper
                .axisAsFace(originalState.getValue(AXIS))
                .getClockWise(targetedFace.getAxis())
                .getAxis());
    }

    @Override
    public BlockState transform(BlockState state, StructureTransform transform) {
        if (transform.mirror != null)
            state = mirror(state, transform.mirror);

        if (transform.rotationAxis == Direction.Axis.Y)
            return rotate(state, transform.rotation);

        state = swapModesForRotation(state, transform.rotation, transform.rotationAxis);
        return state.setValue(AXIS, transform.rotateAxis(state.getValue(AXIS)));
    }

    // Block entity

    @Override
    public Class<CrimschistBlackboxDifferentialBlockEntity> getBlockEntityClass() {
        return CrimschistBlackboxDifferentialBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CrimschistBlackboxDifferentialBlockEntity> getBlockEntityType() {
        return AllBlockEntityTypes.CRIMSCHIST_BLACKBOX_DIFFERENTIAL.get();
    }
}
