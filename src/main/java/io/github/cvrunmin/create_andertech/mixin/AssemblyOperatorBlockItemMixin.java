package io.github.cvrunmin.create_andertech.mixin;

import com.simibubi.create.content.kinetics.belt.BeltSlope;
import com.simibubi.create.content.processing.AssemblyOperatorBlockItem;
import io.github.cvrunmin.create_andertech.index.AllBlocks;
import io.github.cvrunmin.create_andertech.content.kinetics.belt.OCBeltBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AssemblyOperatorBlockItem.class)
public class AssemblyOperatorBlockItemMixin {
    @Inject(method = "operatesOn", at = @At("HEAD"), cancellable = true)
    private void createandertech$alsoCheckForOCBelt(LevelReader world, BlockPos pos, BlockState placedOnState, CallbackInfoReturnable<Boolean> cir){
        if(AllBlocks.OC_BELT.has(placedOnState)){
            cir.setReturnValue(placedOnState.getValue(OCBeltBlock.SLOPE) == BeltSlope.HORIZONTAL);
        }
    }
}
