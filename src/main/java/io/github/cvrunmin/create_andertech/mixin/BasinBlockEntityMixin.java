package io.github.cvrunmin.create_andertech.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.mixer.ChronenditeMechanicalMixerBlockEntity;
import io.github.cvrunmin.create_andertech.content.processing.burner.ChronendicBlazeBurnerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BasinBlockEntity.class)
public class BasinBlockEntityMixin {
    @Inject(method = "getHeatLevelOf", at = @At("HEAD"), cancellable = true)
    private static void injectGetHeatLevelOf(BlockState state, CallbackInfoReturnable<BlazeBurnerBlock.HeatLevel> cir){
        if(state.hasProperty(ChronendicBlazeBurnerBlock.HEAT_LEVEL)){
            cir.setReturnValue(state.getValue(ChronendicBlazeBurnerBlock.HEAT_LEVEL));
        }
    }

    @WrapOperation(method = "lazyTick", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;setAreFluidsMoving(Z)Z"))
    private boolean modifySetAreFluidFlowingFlag(BasinBlockEntity instance, boolean areFluidsMoving, Operation<Boolean> original, @Local(name = "blockEntity") BlockEntity blockEntity){
        if(blockEntity instanceof ChronenditeMechanicalMixerBlockEntity mixer){
            return original.call(instance, mixer.running && mixer.runningTicks <= 20);
        }
        return original.call(instance, areFluidsMoving);
    }
}
