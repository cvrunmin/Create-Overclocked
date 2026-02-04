package io.github.cvrunmin.create_andertech.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltSlope;
import com.simibubi.create.content.logistics.funnel.BeltFunnelBlock;
import io.github.cvrunmin.create_andertech.AllBlocks;
import io.github.cvrunmin.create_andertech.content.kinetics.belt.OCBeltBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BeltFunnelBlock.class)
public class BeltFunnelBlockMixin {
    @Definition(id = "BELT", field = "Lcom/simibubi/create/AllBlocks;BELT:Lcom/tterrag/registrate/util/entry/BlockEntry;")
    @Definition(id = "has", method = "Lcom/tterrag/registrate/util/entry/BlockEntry;has(Lnet/minecraft/world/level/block/state/BlockState;)Z")
    @Expression("BELT.has(?)")
    @ModifyExpressionValue(method = "getShapeForPosition", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static boolean createandertech$getShapeForPositionAlsoCheckForOCBelt(boolean original, @Local(ordinal = 0) BlockState stateBelow){
        return original || AllBlocks.OC_BELT.has(stateBelow);
    }

    @Inject(method = "isOnValidBelt", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/LevelReader;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"), cancellable = true)
    private static void createandertech$isOnValidBeltalsoCheckOCBelt(BlockState state, LevelReader world, BlockPos pos, CallbackInfoReturnable<Boolean> cir){
        BlockState stateBelow = world.getBlockState(pos.below());
        if ((stateBelow.getBlock() instanceof OCBeltBlock))
            cir.setReturnValue(OCBeltBlock.canTransportObjects(stateBelow));
    }

    @Definition(id = "BeltBlock", type = BeltBlock.class)
    @Expression("? instanceof BeltBlock")
    @ModifyExpressionValue(method = "onWrenched", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static boolean createandertech$onWrenchedAlsoCheckForOCBelt(boolean original, @Local(ordinal = 0) BlockState belt){
        return original || belt.getBlock() instanceof OCBeltBlock;
    }

    @Definition(id = "getValue", method = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;")
    @Definition(id = "SLOPE", field = "Lcom/simibubi/create/content/kinetics/belt/BeltBlock;SLOPE:Lnet/minecraft/world/level/block/state/properties/Property;")
    @Definition(id = "HORIZONTAL", field = "Lcom/simibubi/create/content/kinetics/belt/BeltSlope;HORIZONTAL:Lcom/simibubi/create/content/kinetics/belt/BeltSlope;")
    @Expression("?.getValue(SLOPE) != HORIZONTAL")
    @ModifyExpressionValue(method = "onWrenched", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static boolean createandertech$onWrenchedReplaceWithOCBeltProperty(boolean original, @Local(ordinal = 0) BlockState belt){
        if(belt.getBlock() instanceof OCBeltBlock){
            return belt.getValue(OCBeltBlock.SLOPE) != BeltSlope.HORIZONTAL;
        }
        return original;
    }
}
