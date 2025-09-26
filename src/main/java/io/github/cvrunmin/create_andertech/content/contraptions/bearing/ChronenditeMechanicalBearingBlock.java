package io.github.cvrunmin.create_andertech.content.contraptions.bearing;

import com.simibubi.create.content.contraptions.bearing.BearingBlock;
import com.simibubi.create.foundation.block.IBE;
import io.github.cvrunmin.create_andertech.AllBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class ChronenditeMechanicalBearingBlock extends BearingBlock implements IBE<ChronenditeMechanicalBearingBlockEntity> {
    public ChronenditeMechanicalBearingBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn,
                                 BlockHitResult hit) {
        if (!player.mayBuild())
            return InteractionResult.FAIL;
        if (player.isShiftKeyDown())
            return InteractionResult.FAIL;
        if (player.getItemInHand(handIn)
                .isEmpty()) {
            if (worldIn.isClientSide)
                return InteractionResult.SUCCESS;
            withBlockEntityDo(worldIn, pos, be -> {
                if (be.isRunning()) {
                    be.disassemble();
                    return;
                }
                be.setAssembleNextTick(true);
            });
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
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
