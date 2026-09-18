package io.github.cvrunmin.create_andertech.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.gauge.GaugeBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import io.github.cvrunmin.create_andertech.Constants;
import io.github.cvrunmin.create_andertech.content.kinetics.chainDrive.CrimschistChainDriveBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.speedController.ChronenditeSpeedControllerBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.speedController.ChronenditeSpeedControllerBlockEntity;
import io.github.cvrunmin.create_andertech.content.kinetics.transmission.CrimschistBlackboxDifferentialBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.transmission.CrimschistBlackboxDifferentialBlockEntity;
import io.github.cvrunmin.create_andertech.foundation.IOverspeedKineticBE;
import io.github.cvrunmin.create_andertech.index.AllBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Comparator;
import java.util.List;

@Mixin(RotationPropagator.class)
public class RotationPropagatorMixin {
    @Shadow
    private static float getRotationSpeedModifier(KineticBlockEntity from, KineticBlockEntity to) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

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

    @Definition(id = "incompatible", local = @Local(type = boolean.class, name = "incompatible"))
    @Expression("incompatible = ?")
    @Inject(method = "propagateNewSource", at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.AFTER))
    private static void propagateNewSourcesForgiveDifferentialRotationToInput(KineticBlockEntity currentTE, CallbackInfo ci, @Local(name = "incompatible") LocalBooleanRef incompatibleWrapper, @Local(name = "neighbourTE") KineticBlockEntity neighbourTE){
        if(currentTE instanceof CrimschistBlackboxDifferentialBlockEntity){
            if(CrimschistBlackboxDifferentialBlock.isInputPort(currentTE.getBlockState(), currentTE.getBlockPos(), neighbourTE.getBlockPos())){
                incompatibleWrapper.set(false);
            }
        }
        if(neighbourTE instanceof CrimschistBlackboxDifferentialBlockEntity){
            if(CrimschistBlackboxDifferentialBlock.isInputPort(neighbourTE.getBlockState(), neighbourTE.getBlockPos(), currentTE.getBlockPos())){
                incompatibleWrapper.set(false);
            }
        }
    }

    @WrapOperation(method = "propagateNewSource", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/kinetics/base/KineticBlockEntity;getTheoreticalSpeed()F"))
    private static float propagateNewSourcesRedirectDifferentialSpeed(KineticBlockEntity instance, Operation<Float> original, KineticBlockEntity currentTE, @Local(name = "neighbourTE") KineticBlockEntity neighbourTE){
        if(instance instanceof CrimschistBlackboxDifferentialBlockEntity){
            if(instance == currentTE){
                if(!instance.hasSource()) return original.call(instance);
                if(CrimschistBlackboxDifferentialBlock.isInputPort(instance.getBlockState(), instance.getBlockPos(), neighbourTE.getBlockPos())){
                    return neighbourTE.getTheoreticalSpeed();
                }
                return original.call(instance);
            }
            if(CrimschistBlackboxDifferentialBlock.isInputPort(instance.getBlockState(), instance.getBlockPos(), currentTE.getBlockPos())){
                return currentTE.getTheoreticalSpeed();
            }
            return original.call(instance);
        }
        return original.call(instance);
    }

    @Definition(id = "abs", method = "Ljava/lang/Math;abs(F)F")
    @Definition(id = "oppositeSpeed", local = @Local(type = float.class, name = "oppositeSpeed"))
    @Definition(id = "speedOfCurrent", local = @Local(type = float.class, name = "speedOfCurrent"))
    @Expression("abs(oppositeSpeed) > abs(speedOfCurrent)")
    @WrapOperation(method = "propagateNewSource", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static boolean propagateNewSourcesPreferDifferential(float left, float right, Operation<Boolean> original, KineticBlockEntity currentTE, @Local(name = "neighbourTE") KineticBlockEntity neighbourTE){
        if(!(currentTE instanceof CrimschistBlackboxDifferentialBlockEntity differential)){
            return original.call(left, right);
        }
        if(CrimschistBlackboxDifferentialBlock.isOutputPort(currentTE.getBlockState(), currentTE.getBlockPos(), neighbourTE.getBlockPos())){
            return false;
        }
        return original.call(left, right);
    }

    @Definition(id = "abs", method = "Ljava/lang/Math;abs(F)F")
    @Definition(id = "neighbourTE", local = @Local(type = KineticBlockEntity.class, name = "neighbourTE"))
    @Definition(id = "getTheoreticalSpeed", method = "Lcom/simibubi/create/content/kinetics/base/KineticBlockEntity;getTheoreticalSpeed()F")
    @Definition(id = "newSpeed", local = @Local(type = float.class, name = "newSpeed"))
    @Expression("abs(neighbourTE.getTheoreticalSpeed() - newSpeed) <= ?")
    @ModifyExpressionValue(method = "propagateNewSource", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static boolean propagateNewSourcesDifferentialFastskip(boolean original, KineticBlockEntity currentTE){
        if(currentTE instanceof CrimschistBlackboxDifferentialBlockEntity){
            return true;
        }
        return original;
    }

    @Inject(method = "getConveyedSpeed", at = @At("TAIL"), cancellable = true)
    private static void propagateForBlackboxDifferential(KineticBlockEntity from, KineticBlockEntity _to, CallbackInfoReturnable<Float> cir){
        final BlockState stateFrom = from.getBlockState();
        final BlockState stateTo = _to.getBlockState();
        if(AllBlocks.CRIMSCHIST_BLACKBOX_DIFFERENTIAL.has(stateFrom)){
            if(CrimschistBlackboxDifferentialBlock.isOutputPort(stateFrom, from.getBlockPos(), _to.getBlockPos())){
                float rotationSpeedModifier = getRotationSpeedModifier(from, _to);
                cir.setReturnValue(from.getSpeed() * rotationSpeedModifier);
            }else{
                cir.setReturnValue(0.0f);
            }
        }
    }

    @ModifyReturnValue(method = "getConnectedNeighbours", at = @At("RETURN"))
    private static List<KineticBlockEntity> injectGetConnectedNeighbours(List<KineticBlockEntity> original, KineticBlockEntity be){
        if(be instanceof CrimschistBlackboxDifferentialBlockEntity){
            original.sort(Comparator.comparingInt(neighbourBE -> {
                var portOpt = CrimschistBlackboxDifferentialBlock.portOf(be.getBlockState(), be.getBlockPos(), neighbourBE.getBlockPos());
                var mode = portOpt.map(port -> CrimschistBlackboxDifferentialBlock.modeOf(be.getBlockState(), port)).orElse(CrimschistBlackboxDifferentialBlock.Mode.NONE);
                return switch (mode){
                    case NONE -> 0;
                    case IN -> 1;
                    case OUT -> 2;
                };
            }));
        }
        return original;
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
