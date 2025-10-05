package io.github.cvrunmin.create_andertech.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.gauge.GaugeBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import io.github.cvrunmin.create_andertech.AllBlocks;
import io.github.cvrunmin.create_andertech.Constants;
import io.github.cvrunmin.create_andertech.content.kinetics.chainDrive.CrimschistChainDriveBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.speedController.ChronenditeSpeedControllerBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.speedController.ChronenditeSpeedControllerBlockEntity;
import io.github.cvrunmin.create_andertech.foundation.IOverspeedKineticBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

    @Inject(method = "getRotationSpeedModifier", at = @At("TAIL"), cancellable = true)
    private static void propagateForCrimschistChainDrive(KineticBlockEntity from, KineticBlockEntity _to, CallbackInfoReturnable<Float> cir){
        final BlockState stateFrom = from.getBlockState();
        final BlockState stateTo = _to.getBlockState();

        Block fromBlock = stateFrom.getBlock();
        Block toBlock = stateTo.getBlock();
        final BlockPos diff = _to.getBlockPos()
                .subtract(from.getBlockPos());
        final Direction direction = Direction.getNearest(diff.getX(), diff.getY(), diff.getZ());
        if (fromBlock instanceof CrimschistChainDriveBlock && toBlock instanceof CrimschistChainDriveBlock) {
            boolean connected = CrimschistChainDriveBlock.areBlocksConnected(stateFrom, stateTo, direction);
            cir.setReturnValue(connected ? CrimschistChainDriveBlock.getRotationSpeedModifier(from, _to) : 0);
        }
    }

    @Inject(method = "getConveyedSpeed", at = @At("TAIL"), cancellable = true)
    private static void propagateForChronoenditeSpeedController(KineticBlockEntity from, KineticBlockEntity _to, CallbackInfoReturnable<Float> cir){
        final BlockState stateFrom = from.getBlockState();
        final BlockState stateTo = _to.getBlockState();
        if (createandertech$isLargeCogToChronenditeSpeedController(stateFrom, stateTo, _to.getBlockPos()
                .subtract(from.getBlockPos())))
            cir.setReturnValue(ChronenditeSpeedControllerBlockEntity.getConveyedSpeed(from, _to, true));
        if (createandertech$isLargeCogToChronenditeSpeedController(stateTo, stateFrom, from.getBlockPos()
                .subtract(_to.getBlockPos())))
            cir.setReturnValue(ChronenditeSpeedControllerBlockEntity.getConveyedSpeed(_to, from, false));
    }

    @Unique
    private static boolean createandertech$isLargeCogToChronenditeSpeedController(BlockState from, BlockState to, BlockPos diff) {
        if (!ICogWheel.isLargeCog(from) || !AllBlocks.CHRONENDITE_ROTATION_SPEED_CONTROLLER.has(to))
            return false;
        var direction = ChronenditeSpeedControllerBlock.getDirectionFromState(to);
        if (!diff.equals(BlockPos.ZERO.relative(direction.getOpposite())))
            return false;
        Direction.Axis axis = from.getValue(CogWheelBlock.AXIS);
        var ctrlAxis = to.getValue(ChronenditeSpeedControllerBlock.AXIS);
        var orthoAxis = ChronenditeSpeedControllerBlock.getOrthogonalAxis(direction, ctrlAxis);
        return axis == orthoAxis;
    }
}
