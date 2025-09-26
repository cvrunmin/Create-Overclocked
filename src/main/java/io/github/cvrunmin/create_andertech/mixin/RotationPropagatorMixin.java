package io.github.cvrunmin.create_andertech.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.gauge.GaugeBlockEntity;
import io.github.cvrunmin.create_andertech.Constants;
import io.github.cvrunmin.create_andertech.foundation.IOverspeedKineticBE;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RotationPropagator.class)
public class RotationPropagatorMixin {
    @Inject(method = "propagateNewSource", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/kinetics/base/KineticBlockEntity;getFlickerScore()I", shift = At.Shift.BY, by = -2))
    private static void releaseSpeedCheckForOverspeedable(KineticBlockEntity currentTE, CallbackInfo cinfo, @Local(name = "newSpeed") float newSpeed, @Local(name = "oppositeSpeed") float oppositeSpeed, @Local(name = "tooFast") LocalBooleanRef tooFast){
        if(tooFast.get() && (currentTE instanceof IOverspeedKineticBE || currentTE instanceof GaugeBlockEntity)){
            if(Math.abs(newSpeed) <= Constants.MAX_OVERCLOCKING_SPEED && Math.abs(oppositeSpeed) <= Constants.MAX_OVERCLOCKING_SPEED){
                tooFast.set(false);
            }
        }
    }
}
