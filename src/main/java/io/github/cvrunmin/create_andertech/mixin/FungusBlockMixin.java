package io.github.cvrunmin.create_andertech.mixin;

import io.github.cvrunmin.create_andertech.content.decoration.RichNyliumBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.FungusBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FungusBlock.class)
public class FungusBlockMixin {
    @Inject(method = "isValidBonemealTarget", at = @At("HEAD"), cancellable = true)
    private void alsoGrowForRichNylium(LevelReader level, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir){
        BlockState blockstate = level.getBlockState(pos.below());
        if(blockstate.getBlock() instanceof RichNyliumBlock) cir.setReturnValue(true);
    }
}
