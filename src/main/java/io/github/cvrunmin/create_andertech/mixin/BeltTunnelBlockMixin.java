package io.github.cvrunmin.create_andertech.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.kinetics.belt.BeltSlope;
import com.simibubi.create.content.logistics.tunnel.BeltTunnelBlock;
import io.github.cvrunmin.create_andertech.AllBlocks;
import io.github.cvrunmin.create_andertech.content.kinetics.belt.OCBeltBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BeltTunnelBlock.class)
public class BeltTunnelBlockMixin {
    @Inject(method = "isValidPositionForPlacement", at = @At("HEAD"), cancellable = true)
    private static void createandertech$alsoCheckForOCBelt(BlockState state, LevelReader worldIn, BlockPos pos, CallbackInfoReturnable<Boolean> cir){
        BlockState blockState = worldIn.getBlockState(pos.below());
        if(AllBlocks.OC_BELT.has(blockState)){
            cir.setReturnValue(blockState.getValue(OCBeltBlock.SLOPE) == BeltSlope.HORIZONTAL);
        }
    }

    @Definition(id = "CASING", field = "Lcom/simibubi/create/content/kinetics/belt/BeltBlock;CASING:Lnet/minecraft/world/level/block/state/properties/BooleanProperty;")
    @Expression("CASING")
    @ModifyExpressionValue(method = "canSurvive", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static BooleanProperty createandertech$replaceWithOCBeltProperty(BooleanProperty original, @Local(name = "blockState") BlockState blockState){
        if(AllBlocks.OC_BELT.has(blockState)){
            return OCBeltBlock.CASING;
        }
        return original;
    }
}
