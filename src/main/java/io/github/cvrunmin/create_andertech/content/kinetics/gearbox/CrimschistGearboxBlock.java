package io.github.cvrunmin.create_andertech.content.kinetics.gearbox;

import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import io.github.cvrunmin.create_andertech.AllBlockEntityTypes;
import io.github.cvrunmin.create_andertech.AllItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.HitResult;

import java.util.Arrays;
import java.util.List;

public class CrimschistGearboxBlock extends RotatedPillarKineticBlock implements IBE<CrimschistGearboxBlockEntity> {

    public CrimschistGearboxBlock(Properties properties) {
        super(properties);
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.PUSH_ONLY;
    }

    @SuppressWarnings("deprecation")
    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        if (state.getValue(AXIS).isVertical())
            return super.getDrops(state, builder);
        return Arrays.asList(new ItemStack(AllItems.VERTICAL_CRIMSCHIST_GEARBOX.get()));
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos,
                                       Player player) {
        if (state.getValue(AXIS).isVertical())
            return super.getCloneItemStack(state, target, world, pos, player);
        return new ItemStack(AllItems.VERTICAL_CRIMSCHIST_GEARBOX.get());
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(AXIS, Direction.Axis.Y);
    }

    // IRotate:

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() != state.getValue(AXIS);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(AXIS);
    }

    @Override
    public Class<CrimschistGearboxBlockEntity> getBlockEntityClass() {
        return CrimschistGearboxBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CrimschistGearboxBlockEntity> getBlockEntityType() {
        return AllBlockEntityTypes.CRIMSCHIST_GEARBOX.get();
    }
}
