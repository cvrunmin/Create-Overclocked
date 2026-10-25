package io.github.cvrunmin.create_andertech.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.simibubi.create.content.kinetics.RotationPropagator;
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
import io.github.cvrunmin.create_andertech.foundation.OverspeedRotationPropagator;
import io.github.cvrunmin.create_andertech.index.AllBlocks;
import io.github.cvrunmin.create_andertech.index.AllDataComponents;
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

import java.util.ArrayList;
import java.util.List;

@Mixin(RotationPropagator.class)
public class RotationPropagatorMixin {
    @Shadow
    private static float getRotationSpeedModifier(KineticBlockEntity from, KineticBlockEntity to) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    @Shadow
    private static List<KineticBlockEntity> getConnectedNeighbours(KineticBlockEntity be) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    @Shadow
    private static void propagateNewSource(KineticBlockEntity currentTE) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    @WrapMethod(method = "propagateNewSource")
    private static void propagateNewSourceRedirectDifferential(KineticBlockEntity currentTE, Operation<Void> original){
        if(currentTE instanceof CrimschistBlackboxDifferentialBlockEntity differentialBlockEntity){
            OverspeedRotationPropagator.propagateNewSourceBlackboxDifferential(differentialBlockEntity);
        }else {
            original.call(currentTE);
        }
    }

    @ModifyExpressionValue(method = "propagateNewSource", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/kinetics/RotationPropagator;getConnectedNeighbours(Lcom/simibubi/create/content/kinetics/base/KineticBlockEntity;)Ljava/util/List;"))
    private static List<KineticBlockEntity> propagateNewSourceFilterOutNeighbourDifferential(List<KineticBlockEntity> original){
        ArrayList<KineticBlockEntity> newList = null;
        for (int i = 0, originalSize = original.size(); i < originalSize; i++) {
            KineticBlockEntity kineticBlockEntity = original.get(i);
            if(kineticBlockEntity instanceof CrimschistBlackboxDifferentialBlockEntity){
                if(newList == null){
                    newList = new ArrayList<>(originalSize - 1);
                    for (int j = 0; j < i; j++) {
                        newList.add(original.get(j));
                    }
                }
            }else if(newList != null){
                newList.add(kineticBlockEntity);
            }
        }
        return newList != null ? newList : original;
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

    @Inject(method = "propagateNewSource", at = @At("TAIL"))
    private static void propagateNewSourceTryFindDifferentialAndPropagate(KineticBlockEntity currentTE, CallbackInfo ci){
        BlockPos pos = currentTE.getBlockPos();
        for(var neighbourTE : getConnectedNeighbours(currentTE)){
            if(!(neighbourTE instanceof CrimschistBlackboxDifferentialBlockEntity differential)){
                continue;
            }
            OverspeedRotationPropagator.propagateNewSourceWithDifferentialAsNeighbour(currentTE, differential);
        }
    }

    @Definition(id = "neighbourTE", local = @Local(type = KineticBlockEntity.class, name = "neighbourTE"))
    @Definition(id = "setSource", method = "Lcom/simibubi/create/content/kinetics/base/KineticBlockEntity;setSource(Lnet/minecraft/core/BlockPos;)V")
    @Expression("neighbourTE.setSource(?)")
    @WrapOperation(method = "propagateNewSource", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static void propagateNewSourceTryDifferentialNetworkPropagate(KineticBlockEntity instance, BlockPos source, Operation<Void> original, KineticBlockEntity currentTE){
        original.call(instance, source);
        if(currentTE.hasData(AllDataComponents.DIFFERENTIAL_NETWORK_ID)){
            instance.setData(AllDataComponents.DIFFERENTIAL_NETWORK_ID, currentTE.getData(AllDataComponents.DIFFERENTIAL_NETWORK_ID));
        }
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
