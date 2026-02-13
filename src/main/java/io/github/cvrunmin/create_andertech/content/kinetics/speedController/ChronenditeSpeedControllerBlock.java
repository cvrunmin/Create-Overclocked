package io.github.cvrunmin.create_andertech.content.kinetics.speedController;

import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.foundation.block.IBE;
import io.github.cvrunmin.create_andertech.index.AllBlockEntityTypes;
import io.github.cvrunmin.create_andertech.index.AllBlocks;
import io.github.cvrunmin.create_andertech.CreateAndertech;
import net.createmod.catnip.math.VoxelShaper;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementHelpers;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class ChronenditeSpeedControllerBlock extends RotatedPillarKineticBlock implements IBE<ChronenditeSpeedControllerBlockEntity> {
    public static final IntegerProperty FACING_DIRECTION = IntegerProperty.create("facing_direction", 0, 3);
    public static final Map<Direction.Axis, List<Direction>> AXIS_TO_DIRECTIONS = Map.ofEntries(
            Map.entry(Direction.Axis.X, List.of(Direction.UP, Direction.SOUTH, Direction.DOWN, Direction.NORTH)),
            Map.entry(Direction.Axis.Y, List.of(Direction.SOUTH, Direction.EAST, Direction.NORTH, Direction.WEST)),
            Map.entry(Direction.Axis.Z, List.of(Direction.EAST, Direction.UP, Direction.WEST, Direction.DOWN))
    );
    private static final int placementHelperId = PlacementHelpers.register(new PlacementHelper());

    public ChronenditeSpeedControllerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction.Axis preferredAxis = getPreferredAxis(context);
        boolean usePreferredAxis = preferredAxis != null && (context.getPlayer() == null || !context.getPlayer().isShiftKeyDown());
        if(usePreferredAxis){
            for (Direction direction : Direction.values()) {
                if(direction.getAxis() == preferredAxis) continue;
                BlockState offsetState = context.getLevel().getBlockState(context.getClickedPos().relative(direction));
                if(ICogWheel.isLargeCog(offsetState)){
                    var cogwheelAxis = offsetState.getValue(CogWheelBlock.AXIS);
                    if(cogwheelAxis != preferredAxis && cogwheelAxis != direction.getAxis()){
                        return defaultBlockState().setValue(AXIS, preferredAxis).setValue(FACING_DIRECTION, safeGetFacingDirection(preferredAxis, direction));
                    }
                }
            }
            if(context.getClickedFace().getAxis() == preferredAxis){
                for (Direction nearestLookingDirection : context.getNearestLookingDirections()) {
                    if(nearestLookingDirection.getAxis() != preferredAxis){
                        return defaultBlockState().setValue(AXIS, preferredAxis).setValue(FACING_DIRECTION, safeGetFacingDirection(preferredAxis, nearestLookingDirection.getOpposite()));
                    }
                }
                return defaultBlockState().setValue(AXIS, preferredAxis).setValue(FACING_DIRECTION, 0);
            }
            return defaultBlockState().setValue(AXIS, preferredAxis).setValue(FACING_DIRECTION, safeGetFacingDirection(preferredAxis, context.getClickedFace()));
        }
        else{
            if(context.getPlayer() == null || !context.getPlayer().isShiftKeyDown()){
                for (Direction direction : Direction.values()) {
                    BlockState offsetState = context.getLevel().getBlockState(context.getClickedPos().relative(direction));
                    if(ICogWheel.isLargeCog(offsetState)){
                        var cogwheelAxis = offsetState.getValue(CogWheelBlock.AXIS);
                        if(cogwheelAxis != direction.getAxis()){
                            preferredAxis = getOrthogonalAxis(direction, cogwheelAxis);
                            return defaultBlockState().setValue(AXIS, preferredAxis).setValue(FACING_DIRECTION, safeGetFacingDirection(preferredAxis, direction));
                        }
                    }
                }
            }
            var face = context.getClickedFace();
            preferredAxis = null; // void the searched preferred axis
            for (Direction nearestLookingDirection : context.getNearestLookingDirections()) {
                if(nearestLookingDirection.getAxis() != face.getAxis()){
                    preferredAxis = nearestLookingDirection.getAxis();
                    break;
                }
            }
            if(preferredAxis == null){
                // it should not be triggered, but just in case.
                preferredAxis = face.getAxis().isVertical() ? Direction.Axis.X : Direction.Axis.Y;
            }
            return defaultBlockState().setValue(AXIS, preferredAxis).setValue(FACING_DIRECTION, safeGetFacingDirection(preferredAxis, face));
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block p_220069_4_, BlockPos neighbourPos,
                                boolean p_220069_6_) {
        if (neighbourPos.equals(pos.relative(getDirectionFromState(state))))
            withBlockEntityDo(world, pos, ChronenditeSpeedControllerBlockEntity::updateBracket);
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand,
                                           BlockHitResult ray) {
        IPlacementHelper helper = PlacementHelpers.get(placementHelperId);
        if (helper.matchesItem(stack))
            return helper.getOffset(player, world, state, pos, ray).placeInWorld(world, (BlockItem) stack.getItem(), player, hand, ray);

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        var direction = getDirectionFromState(state);
        return VoxelShaper.forDirectional(AllShapes.SPEED_CONTROLLER, Direction.UP).get(direction);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING_DIRECTION);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state;
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(AXIS);
    }

    private static int safeGetFacingDirection(Direction.Axis preferredAxis, Direction direction){
        int value = AXIS_TO_DIRECTIONS.get(preferredAxis).indexOf(direction);
        if(value == -1){
            CreateAndertech.LOGGER.warn("Searched a wrong direction! ({} vs {})", preferredAxis, direction);
            value = 0;
        }
        return value;
    }

    public static Direction getDirectionFromState(BlockState state){
        if(!AllBlocks.CHRONENDITE_ROTATION_SPEED_CONTROLLER.has(state)) return Direction.UP;
        return AXIS_TO_DIRECTIONS.get(state.getValue(AXIS)).get(state.getValue(FACING_DIRECTION));
    }

    public static Direction.Axis getOrthogonalAxis(Direction direction, Direction.Axis axis) {
        Direction.Axis preferredAxis;
        switch (axis) {
            case X -> preferredAxis = direction.getAxis() == Direction.Axis.Y ? Direction.Axis.Z : Direction.Axis.Y;
            case Y -> preferredAxis = direction.getAxis() == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
            default -> preferredAxis = direction.getAxis() == Direction.Axis.X ? Direction.Axis.Y : Direction.Axis.X;
        }
        return preferredAxis;
    }


    @Override
    public BlockState getRotatedBlockState(BlockState originalState, Direction targetedFace) {
        var axis = originalState.getValue(AXIS);
        var facing = getDirectionFromState(originalState);
        if(targetedFace.getAxis() == Direction.Axis.Y){
            if(axis.isHorizontal()){
                axis = axis == Direction.Axis.Z ? Direction.Axis.X : Direction.Axis.Z;
                facing = facing.getClockWise(Direction.Axis.Y);
                return originalState.setValue(AXIS, axis).setValue(FACING_DIRECTION, AXIS_TO_DIRECTIONS.get(axis).indexOf(facing));
            }else{
                facing = facing.getClockWise(Direction.Axis.Y);
                return originalState.setValue(FACING_DIRECTION, AXIS_TO_DIRECTIONS.get(axis).indexOf(facing));
            }
        } else if (targetedFace.getAxis() == Direction.Axis.X) {
            if(axis != Direction.Axis.X){
                axis = axis == Direction.Axis.Z ? Direction.Axis.Y : Direction.Axis.Z;
                facing = facing.getClockWise(Direction.Axis.X);
                return originalState.setValue(AXIS, axis).setValue(FACING_DIRECTION, AXIS_TO_DIRECTIONS.get(axis).indexOf(facing));
            }else{
                facing = facing.getClockWise(Direction.Axis.X);
                return originalState.setValue(FACING_DIRECTION, AXIS_TO_DIRECTIONS.get(axis).indexOf(facing));
            }
        }else{
            if(axis != Direction.Axis.Z){
                axis = axis == Direction.Axis.X ? Direction.Axis.Y : Direction.Axis.X;
                facing = facing.getClockWise(Direction.Axis.Z);
                return originalState.setValue(AXIS, axis).setValue(FACING_DIRECTION, AXIS_TO_DIRECTIONS.get(axis).indexOf(facing));
            }else{
                facing = facing.getClockWise(Direction.Axis.Z);
                return originalState.setValue(FACING_DIRECTION, AXIS_TO_DIRECTIONS.get(axis).indexOf(facing));
            }

        }
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == state.getValue(AXIS);
    }

    @MethodsReturnNonnullByDefault
    private static class PlacementHelper implements IPlacementHelper {
        @Override
        public Predicate<ItemStack> getItemPredicate() {
            return ((Predicate<ItemStack>) ICogWheel::isLargeCogItem).and(ICogWheel::isDedicatedCogItem);
        }

        @Override
        public Predicate<BlockState> getStatePredicate() {
            return AllBlocks.CHRONENDITE_ROTATION_SPEED_CONTROLLER::has;
        }

        @Override
        public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos, BlockHitResult ray) {
            var offsetDirection = getDirectionFromState(state);
            BlockPos newPos = pos.relative(offsetDirection);
            if (!world.getBlockState(newPos).canBeReplaced())
                return PlacementOffset.fail();

            Direction.Axis newAxis = getOrthogonalAxis(offsetDirection, state.getValue(AXIS));

            if (!CogWheelBlock.isValidCogwheelPosition(true, world, newPos, newAxis))
                return PlacementOffset.fail();

            return PlacementOffset.success(newPos, s -> s.setValue(CogWheelBlock.AXIS, newAxis));
        }
    }


    @Override
    public Class<ChronenditeSpeedControllerBlockEntity> getBlockEntityClass() {
        return ChronenditeSpeedControllerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ChronenditeSpeedControllerBlockEntity> getBlockEntityType() {
        return AllBlockEntityTypes.CHRONENDITE_ROTATION_SPEED_CONTROLLER.get();
    }
}
