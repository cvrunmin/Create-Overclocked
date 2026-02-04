package io.github.cvrunmin.create_andertech.content.kinetics.crafter;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
import io.github.cvrunmin.create_andertech.AllBlockEntityTypes;
import io.github.cvrunmin.create_andertech.AllBlocks;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.Pointing;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class ChronenditeMechanicalCrafterBlock extends HorizontalKineticBlock implements IBE<ChronenditeMechanicalCrafterBlockEntity>, ICogWheel {
    public static final EnumProperty<Pointing> POINTING = EnumProperty.create("pointing", Pointing.class);
    public ChronenditeMechanicalCrafterBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(POINTING, Pointing.UP));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(POINTING));
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(HORIZONTAL_FACING)
                .getAxis();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction face = context.getClickedFace();
        BlockPos placedOnPos = context.getClickedPos()
                .relative(face.getOpposite());
        BlockState blockState = context.getLevel()
                .getBlockState(placedOnPos);

        if ((blockState.getBlock() != this) || (context.getPlayer() != null && context.getPlayer()
                .isShiftKeyDown())) {
            BlockState stateForPlacement = super.getStateForPlacement(context);
            Direction direction = stateForPlacement.getValue(HORIZONTAL_FACING);
            if (direction != face)
                stateForPlacement = stateForPlacement.setValue(POINTING, pointingFromFacing(face, direction));
            return stateForPlacement;
        }

        Direction otherFacing = blockState.getValue(HORIZONTAL_FACING);
        Pointing pointing = pointingFromFacing(face, otherFacing);
        return defaultBlockState().setValue(HORIZONTAL_FACING, otherFacing)
                .setValue(POINTING, pointing);
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() == newState.getBlock()) {
            if (getTargetDirection(state) != getTargetDirection(newState)) {
                ChronenditeMechanicalCrafterBlockEntity crafter = OCCrafterHelper.getCrafter(worldIn, pos);
                if (crafter != null)
                    crafter.blockChanged();
            }
        }

        if (state.hasBlockEntity() && !state.is(newState.getBlock())) {
            ChronenditeMechanicalCrafterBlockEntity crafter = OCCrafterHelper.getCrafter(worldIn, pos);
            if (crafter != null) {
                if (crafter.covered)
                    Block.popResource(worldIn, pos, AllItems.CRAFTER_SLOT_COVER.asStack());
                if (!isMoving)
                    crafter.ejectWholeGrid();
            }

            for (Direction direction : Iterate.directions) {
                if (direction.getAxis() == state.getValue(HORIZONTAL_FACING)
                        .getAxis())
                    continue;

                BlockPos otherPos = pos.relative(direction);
                var thisInput = OCCrafterHelper.getInput(worldIn, pos);
                var otherInput = OCCrafterHelper.getInput(worldIn, otherPos);

                if (thisInput == null || otherInput == null)
                    continue;
                if (!pos.offset(thisInput.data.get(0))
                        .equals(otherPos.offset(otherInput.data.get(0))))
                    continue;

                OCConnectedInputHandler.toggleConnection(worldIn, pos, otherPos);
            }
        }

        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    public static Pointing pointingFromFacing(Direction pointingFace, Direction blockFacing) {
        boolean positive = blockFacing.getAxisDirection() == Direction.AxisDirection.POSITIVE;

        Pointing pointing = pointingFace == Direction.DOWN ? Pointing.UP : Pointing.DOWN;
        if (pointingFace == Direction.EAST)
            pointing = positive ? Pointing.LEFT : Pointing.RIGHT;
        if (pointingFace == Direction.WEST)
            pointing = positive ? Pointing.RIGHT : Pointing.LEFT;
        if (pointingFace == Direction.NORTH)
            pointing = positive ? Pointing.LEFT : Pointing.RIGHT;
        if (pointingFace == Direction.SOUTH)
            pointing = positive ? Pointing.RIGHT : Pointing.LEFT;
        return pointing;
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        if (context.getClickedFace() == state.getValue(HORIZONTAL_FACING)) {
            if (!context.getLevel().isClientSide)
                KineticBlockEntity.switchToBlockState(context.getLevel(), context.getClickedPos(),
                        state.cycle(POINTING));
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn,
                                           BlockHitResult hit) {
        BlockEntity blockEntity = worldIn.getBlockEntity(pos);
        if (!(blockEntity instanceof ChronenditeMechanicalCrafterBlockEntity crafter))
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        if (com.simibubi.create.AllBlocks.MECHANICAL_ARM.isIn(stack) || AllBlocks.CHRONENDITE_MECHANICAL_ARM.isIn(stack))
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        boolean isHand = stack.isEmpty() && handIn == InteractionHand.MAIN_HAND;
        boolean wrenched = AllItems.WRENCH.isIn(stack);

        if (hit.getDirection() == state.getValue(HORIZONTAL_FACING)) {

            if (crafter.phase != ChronenditeMechanicalCrafterBlockEntity.Phase.IDLE && !wrenched) {
                crafter.ejectWholeGrid();
                return ItemInteractionResult.SUCCESS;
            }

            if (crafter.phase == ChronenditeMechanicalCrafterBlockEntity.Phase.IDLE && !isHand && !wrenched) {
                if (worldIn.isClientSide)
                    return ItemInteractionResult.SUCCESS;

                if (AllItems.CRAFTER_SLOT_COVER.isIn(stack)) {
                    if (crafter.covered)
                        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                    if (!crafter.inventory.isEmpty())
                        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                    crafter.covered = true;
                    crafter.setChanged();
                    crafter.sendData();
                    if (!player.isCreative())
                        stack.shrink(1);
                    return ItemInteractionResult.SUCCESS;
                }

                IItemHandler capability = worldIn.getCapability(Capabilities.ItemHandler.BLOCK, crafter.getBlockPos(), null);
                if (capability == null)
                    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                ItemStack remainder =
                        ItemHandlerHelper.insertItem(capability, stack.copy(), false);
                if (remainder.getCount() != stack.getCount())
                    player.setItemInHand(handIn, remainder);
                return ItemInteractionResult.SUCCESS;
            }

            ItemStack inSlot = crafter.getInventory()
                    .getItem(0);
            if (inSlot.isEmpty()) {
                if (crafter.covered && !wrenched) {
                    if (worldIn.isClientSide)
                        return ItemInteractionResult.SUCCESS;
                    crafter.covered = false;
                    crafter.setChanged();
                    crafter.sendData();
                    if (!player.isCreative())
                        player.getInventory()
                                .placeItemBackInInventory(AllItems.CRAFTER_SLOT_COVER.asStack());
                    return ItemInteractionResult.SUCCESS;
                }
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            if (!isHand && !ItemStack.isSameItemSameComponents(stack, inSlot))
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            if (worldIn.isClientSide)
                return ItemInteractionResult.SUCCESS;
            player.getInventory()
                    .placeItemBackInInventory(inSlot);
            crafter.getInventory()
                    .setStackInSlot(0, ItemStack.EMPTY);
            return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos,
                                boolean isMoving) {
        InvManipulationBehaviour behaviour = BlockEntityBehaviour.get(worldIn, pos, InvManipulationBehaviour.TYPE);
        if (behaviour != null)
            behaviour.onNeighborChanged(fromPos);
    }

    @Override
    public float getParticleTargetRadius() {
        return .85f;
    }

    @Override
    public float getParticleInitialRadius() {
        return .75f;
    }

    public static Direction getTargetDirection(BlockState state) {
        if (!AllBlocks.CHRONENDITE_MECHANICAL_CRAFTER.has(state))
            return Direction.UP;
        Direction facing = state.getValue(HORIZONTAL_FACING);
        Pointing point = state.getValue(POINTING);
        Vec3 targetVec = new Vec3(0, 1, 0);
        targetVec = VecHelper.rotate(targetVec, -point.getXRotation(), Direction.Axis.Z);
        targetVec = VecHelper.rotate(targetVec, AngleHelper.horizontalAngle(facing), Direction.Axis.Y);
        return Direction.getNearest(targetVec.x, targetVec.y, targetVec.z);
    }

    public static boolean isValidTarget(Level world, BlockPos targetPos, BlockState crafterState) {
        BlockState targetState = world.getBlockState(targetPos);
        if (!world.isLoaded(targetPos))
            return false;
        if (!AllBlocks.CHRONENDITE_MECHANICAL_CRAFTER.has(targetState))
            return false;
        if (crafterState.getValue(HORIZONTAL_FACING) != targetState.getValue(HORIZONTAL_FACING))
            return false;
        if (Math.abs(crafterState.getValue(POINTING)
                .getXRotation()
                - targetState.getValue(POINTING)
                .getXRotation()) == 180)
            return false;
        return true;
    }

    @Override
    public Class<ChronenditeMechanicalCrafterBlockEntity> getBlockEntityClass() {
        return ChronenditeMechanicalCrafterBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ChronenditeMechanicalCrafterBlockEntity> getBlockEntityType() {
        return AllBlockEntityTypes.CHRONENDITE_MECHANICAL_CRAFTER.get();
    }
}
