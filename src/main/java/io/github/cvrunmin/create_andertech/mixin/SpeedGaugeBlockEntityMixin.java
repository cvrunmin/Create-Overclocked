package io.github.cvrunmin.create_andertech.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.simibubi.create.content.kinetics.gauge.SpeedGaugeBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SpeedGaugeBlockEntity.class)
public class SpeedGaugeBlockEntityMixin {
    @ModifyReturnValue(method = "getDialTarget", at = @At("RETURN"))
    private static float modifyGaugeDialTargetValue(float original, float speed){
        return Math.min(original, 1.5f);
    }
}
