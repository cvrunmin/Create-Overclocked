package io.github.cvrunmin.create_andertech.content.misc;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import io.github.cvrunmin.create_andertech.content.processing.burner.ChronendicBlazeBurnerBlock;
import io.github.cvrunmin.create_andertech.content.processing.burner.ChronendicBlazeBurnerBlockEntity;
import io.github.cvrunmin.create_andertech.index.AllBlockEntityTypes;
import io.github.cvrunmin.create_andertech.index.AllBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class EndopyroticCinderPasteItem extends Item {
    public EndopyroticCinderPasteItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        BlockPos clickedPos = context.getClickedPos();
        Level level = context.getLevel();
        BlockState clickState = level.getBlockState(clickedPos);
        if(clickState.is(com.simibubi.create.AllBlocks.BLAZE_BURNER)){
            if(clickState.getValue(BlazeBurnerBlock.HEAT_LEVEL) != BlazeBurnerBlock.HeatLevel.NONE){
                if(!level.isClientSide) {
                    level.setBlockAndUpdate(clickedPos, AllBlocks.CHRONENDIC_BLAZE_BURNER.getDefaultState()
                            .setValue(ChronendicBlazeBurnerBlock.HEAT_LEVEL, BlazeBurnerBlock.HeatLevel.KINDLED)
                            .setValue(ChronendicBlazeBurnerBlock.INSTABLE, true)
                    );
                    var be = level.getBlockEntity(clickedPos);
                    if(be instanceof ChronendicBlazeBurnerBlockEntity burner){
                        burner.startInstableMagic();
                    }
                    level.playSound(null, clickedPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0f, 1.0f);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.onItemUseFirst(stack, context);
    }
}
