package io.github.cvrunmin.create_andertech.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import io.github.cvrunmin.create_andertech.content.kinetics.transmission.CrimschistBlackboxDifferentialBlockEntity;
import io.github.cvrunmin.create_andertech.index.AllDataComponents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KineticBlockEntity.class)
public class KineticBlockEntityMixin {
    @WrapOperation(method = "validateKinetics", at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/kinetics/base/KineticBlockEntity;speed:F", opcode = 180))
    private float validateKineticRerouteDifferentialSpeed(KineticBlockEntity instance, Operation<Float> original){
        if(instance instanceof CrimschistBlackboxDifferentialBlockEntity differential){
            return differential.getSpeed();
        }
        return original.call(instance);
    }

    @Inject(method = "removeSource", at = @At("RETURN"))
    private void removeSourceTryNukeDifferentialNetwork(CallbackInfo ci){
        if(((KineticBlockEntity) (Object) this).hasData(AllDataComponents.DIFFERENTIAL_NETWORK_ID)){
            ((KineticBlockEntity) (Object) this).removeData(AllDataComponents.DIFFERENTIAL_NETWORK_ID);
        }
    }
}
