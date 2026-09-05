package io.github.cvrunmin.create_andertech.content.kinetics.mechanicalArm;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlock;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.foundation.block.IBE;
import io.github.cvrunmin.create_andertech.index.AllBlockEntityTypes;
import io.github.cvrunmin.create_andertech.mixin.ArmBlockEntityAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class ChronenditeArmBlock extends KineticBlock implements IBE<ChronenditeArmBlockEntity>, ICogWheel {
    public static final BooleanProperty CEILING = ArmBlock.CEILING;
//    public static final BooleanProperty CEILING = BooleanProperty.create("ceiling");

    public ChronenditeArmBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(CEILING, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_206840_1_) {
        super.createBlockStateDefinition(p_206840_1_.add(CEILING));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(CEILING, ctx.getClickedFace() == Direction.DOWN);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter p_220053_2_, BlockPos p_220053_3_,
                               CollisionContext p_220053_4_) {
        return state.getValue(CEILING) ? AllShapes.MECHANICAL_ARM_CEILING : AllShapes.MECHANICAL_ARM;
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, world, pos, oldState, isMoving);
        withBlockEntityDo(world, pos, ChronenditeArmBlockEntity::redstoneUpdate);
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block p_220069_4_,
                                BlockPos p_220069_5_, boolean p_220069_6_) {
        withBlockEntityDo(world, pos, ChronenditeArmBlockEntity::redstoneUpdate);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return Direction.Axis.Y;
    }

    @Override
    public Class<ChronenditeArmBlockEntity> getBlockEntityClass() {
        return ChronenditeArmBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ChronenditeArmBlockEntity> getBlockEntityType() {
        return AllBlockEntityTypes.CHRONENDITE_MECHANICAL_ARM.get();
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player,
                                     InteractionHand hand, BlockHitResult ray) {
        if (AllItems.GOGGLES.isIn(stack)) {
            ItemInteractionResult gogglesResult = onBlockEntityUseItemOn(world, pos, ate -> {
                if (((ArmBlockEntityAccessor)ate).isGoggles())
                    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                ((ArmBlockEntityAccessor)ate).setGoggles(true);
                ate.notifyUpdate();
                return ItemInteractionResult.SUCCESS;
            });
            if (gogglesResult.consumesAction())
                return gogglesResult;
        }

        MutableBoolean success = new MutableBoolean(false);
        withBlockEntityDo(world, pos, be -> {
            if (((ArmBlockEntityAccessor)be).getHeldItem().isEmpty())
                return;
            success.setTrue();
            if (world.isClientSide)
                return;
            player.getInventory().placeItemBackInInventory(((ArmBlockEntityAccessor)be).getHeldItem());
            ((ArmBlockEntityAccessor)be).setHeldItem(ItemStack.EMPTY);
            ((ArmBlockEntityAccessor)be).setPhase(ArmBlockEntity.Phase.SEARCH_INPUTS);
            be.setChanged();
            be.sendData();
        });

        return success.booleanValue() ? ItemInteractionResult.SUCCESS : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
