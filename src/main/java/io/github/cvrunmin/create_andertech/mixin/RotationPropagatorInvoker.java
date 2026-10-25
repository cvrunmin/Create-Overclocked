package io.github.cvrunmin.create_andertech.mixin;

import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

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

    @Invoker(value = "getPotentialNeighbourLocations", remap = false)
    static List<BlockPos> invokeGetPotentialNeighbourLocations(KineticBlockEntity be) {
        throw new AssertionError("RotationPropagatorInvoker mixin did not apply");
    }

    @Invoker(value = "findConnectedNeighbour", remap = false)
    static KineticBlockEntity invokeFindConnectedNeighbour(KineticBlockEntity currentTE, BlockPos neighbourPos){
        throw new AssertionError("RotationPropagatorInvoker mixin did not apply");
    }

    @Invoker(value = "propagateMissingSource", remap = false)
    static void invokePropagateMissingSource(KineticBlockEntity updateTE){
        throw new AssertionError("RotationPropagatorInvoker mixin did not apply");
    }

    @Invoker(value = "propagateNewSource", remap = false)
    static void invokePropagateNewSource(KineticBlockEntity updateTE){
        throw new AssertionError("RotationPropagatorInvoker mixin did not apply");
    }

    @Invoker(value = "getConveyedSpeed", remap = false)
    static float invokeGetConveyedSpeed(KineticBlockEntity from, KineticBlockEntity to){
        throw new AssertionError("RotationPropagatorInvoker mixin did not apply");
    }
}
