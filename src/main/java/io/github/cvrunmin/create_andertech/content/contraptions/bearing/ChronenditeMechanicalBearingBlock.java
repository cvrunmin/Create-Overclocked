package io.github.cvrunmin.create_andertech.content.contraptions.bearing;

import com.simibubi.create.content.contraptions.bearing.BearingBlock;
import com.simibubi.create.foundation.block.IBE;
import io.github.cvrunmin.create_andertech.AllBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class ChronenditeMechanicalBearingBlock extends BearingBlock implements IBE<ChronenditeMechanicalBearingBlockEntity> {
    public ChronenditeMechanicalBearingBlock(Properties properties) {
        super(properties);
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn,
                                           BlockHitResult hit) {
        if (!player.mayBuild())
            return ItemInteractionResult.FAIL;
        if (player.isShiftKeyDown())
            return ItemInteractionResult.FAIL;
        if (stack.isEmpty()) {
            if (worldIn.isClientSide)
                return ItemInteractionResult.SUCCESS;
            withBlockEntityDo(worldIn, pos, be -> {
                if (be.isRunning()) {
                    be.disassemble();
                    return;
                }
                be.setAssembleNextTick(true);
            });
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public Class<ChronenditeMechanicalBearingBlockEntity> getBlockEntityClass() {
        return ChronenditeMechanicalBearingBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ChronenditeMechanicalBearingBlockEntity> getBlockEntityType() {
        return AllBlockEntityTypes.CHRONENDITE_MECHANICAL_BEARING.get();
    }
}
