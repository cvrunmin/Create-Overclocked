package io.github.cvrunmin.create_andertech.mixin;

import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * Exposes RotationPropagator's private vanilla connection factor, so kinetic block entities can ask what a
 * neighbour would convey to them without reimplementing Create's rules.
 */
@SuppressWarnings("unused")
@Mixin(RotationPropagator.class)
public interface RotationPropagatorInvoker {

    @Invoker(value = "getRotationSpeedModifier", remap = false)
    static float invokeGetRotationSpeedModifier(KineticBlockEntity from, KineticBlockEntity to) {
        throw new AssertionError("RotationPropagatorInvoker mixin did not apply");
    }
}
