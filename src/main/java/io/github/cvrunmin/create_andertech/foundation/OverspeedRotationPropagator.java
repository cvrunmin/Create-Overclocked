package io.github.cvrunmin.create_andertech.foundation;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.gauge.GaugeBlockEntity;
import com.simibubi.create.infrastructure.config.AllConfigs;
import io.github.cvrunmin.create_andertech.Constants;
import io.github.cvrunmin.create_andertech.content.kinetics.transmission.CrimschistBlackboxDifferentialBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.transmission.CrimschistBlackboxDifferentialBlockEntity;
import io.github.cvrunmin.create_andertech.index.AllDataComponents;
import io.github.cvrunmin.create_andertech.mixin.RotationPropagatorInvoker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class OverspeedRotationPropagator {
    public static void propagateNewSourceBlackboxDifferential(CrimschistBlackboxDifferentialBlockEntity currentTE) {
        BlockPos pos = currentTE.getBlockPos();
        Level world = currentTE.getLevel();
        if(currentTE.getFlickerScore() > 128){
            world.destroyBlock(pos, true);
            return;
        }
        for (KineticBlockEntity neighbourTE : getConnectedNeighboursBlackboxDifferential(currentTE, CrimschistBlackboxDifferentialBlock.Mode.NONE)) {
            if(neighbourTE.hasSource() && neighbourTE.source == pos){
                RotationPropagatorInvoker.invokePropagateMissingSource(neighbourTE);
            }
        }
        var dominantSpeed = 0f;
        KineticBlockEntity dominantTE = null;
        var maybeNetworkId = currentTE.getExistingData(AllDataComponents.DIFFERENTIAL_NETWORK_ID);
        for (var neighbourTE : getConnectedNeighboursBlackboxDifferential(currentTE, CrimschistBlackboxDifferentialBlock.Mode.IN)){
            var maybeNeighbourNetworkId = neighbourTE.getExistingData(AllDataComponents.DIFFERENTIAL_NETWORK_ID);
            if(maybeNetworkId.isPresent() && maybeNeighbourNetworkId.isPresent() && Objects.equals(maybeNetworkId.get(), maybeNeighbourNetworkId.get())){
                world.destroyBlock(neighbourTE.getBlockPos(), true);
                continue;
            }
            float speedOfNeighbour = neighbourTE.getTheoreticalSpeed();
            float oppositeSpeed = RotationPropagatorInvoker.invokeGetConveyedSpeed(neighbourTE, currentTE);
            if(speedOfNeighbour == 0){
                continue;
            }
            if(Math.abs(oppositeSpeed) > Math.abs(dominantSpeed)){
                dominantSpeed = oppositeSpeed;
                dominantTE = neighbourTE;
            }
        }
        //noinspection ConstantValue  wrong inspection from IDEA
        if(dominantSpeed != 0 && dominantTE != null){
            float prevDominantSpeed = currentTE.getDominantSpeed();
            currentTE.setSource(dominantTE.getBlockPos());
            if(dominantTE.hasData(AllDataComponents.DIFFERENTIAL_NETWORK_ID)){
                currentTE.setData(AllDataComponents.DIFFERENTIAL_NETWORK_ID, dominantTE.getData(AllDataComponents.DIFFERENTIAL_NETWORK_ID));
            }
            currentTE.setDominantSpeed(dominantSpeed);
            currentTE.onSpeedChanged(prevDominantSpeed);
            currentTE.sendData();
        }
        float lastSpeed = currentTE.lastCalculatedSpeed;
        float currentSpeed = currentTE.solve();
        boolean speedChanged = lastSpeed != currentSpeed;
        currentTE.lastCalculatedSpeed = currentSpeed;
        for (var neighbourTE : getConnectedNeighboursBlackboxDifferential(currentTE, CrimschistBlackboxDifferentialBlock.Mode.OUT)){
            if(speedChanged){
                // detach first
                neighbourTE.source = currentTE.getBlockPos();
                RotationPropagatorInvoker.invokePropagateMissingSource(neighbourTE);
            }
            float speedOfCurrent = currentTE.getTheoreticalSpeed();
            float speedOfNeighbour = neighbourTE.getTheoreticalSpeed();
            float newSpeed = RotationPropagatorInvoker.invokeGetConveyedSpeed(currentTE, neighbourTE);
            float oppositeSpeed = RotationPropagatorInvoker.invokeGetConveyedSpeed(neighbourTE, currentTE);
            if (newSpeed == 0 && oppositeSpeed == 0)
                continue;
            boolean incompatible =
                    Math.signum(oppositeSpeed) != Math.signum(speedOfCurrent) && (oppositeSpeed != 0 && speedOfCurrent != 0);
            boolean tooFast = Math.abs(newSpeed) > AllConfigs.server().kinetics.maxRotationSpeed.get()
                    || Math.abs(oppositeSpeed) > AllConfigs.server().kinetics.maxRotationSpeed.get();
            if(neighbourTE instanceof IOverspeedKineticBE || neighbourTE instanceof GaugeBlockEntity){
                tooFast = Math.abs(newSpeed) > Constants.MAX_OVERCLOCKING_SPEED || Math.abs(oppositeSpeed) > Constants.MAX_OVERCLOCKING_SPEED;
            }
            boolean speedChangedTooOften = neighbourTE.getFlickerScore() > 128;
            if (tooFast || speedChangedTooOften) {
                world.destroyBlock(neighbourTE.getBlockPos(), true);
                continue;
            }
            if (incompatible) {
                world.destroyBlock(neighbourTE.getBlockPos(), true);
                continue;
            } else {
                if (Math.abs(oppositeSpeed) > Math.abs(speedOfCurrent) && neighbourTE.hasSource() && neighbourTE.source != currentTE.getBlockPos()) {
                    world.destroyBlock(neighbourTE.getBlockPos(), true);
                    continue;
                }
                if (!currentTE.hasNetwork()) {
                    continue;
                }

                if (currentTE.hasSource() && currentTE.source.equals(neighbourTE.getBlockPos()))
                    currentTE.removeSource();

                if(!currentTE.hasData(AllDataComponents.DIFFERENTIAL_NETWORK_ID)){
                    currentTE.setData(AllDataComponents.DIFFERENTIAL_NETWORK_ID, currentTE.getBlockPos().asLong());
                }

                float prevSpeed = neighbourTE.getSpeed();
                neighbourTE.setSource(currentTE.getBlockPos());
                neighbourTE.setData(AllDataComponents.DIFFERENTIAL_NETWORK_ID, currentTE.getData(AllDataComponents.DIFFERENTIAL_NETWORK_ID));
                neighbourTE.setSpeed(newSpeed);
                neighbourTE.onSpeedChanged(prevSpeed);
                neighbourTE.sendData();
                RotationPropagatorInvoker.invokePropagateNewSource(neighbourTE);
            }
        }
    }

    public static void propagateNewSourceWithDifferentialAsNeighbour(KineticBlockEntity currentTE, CrimschistBlackboxDifferentialBlockEntity neighbourTE){
        Level world = currentTE.getLevel();
        var optionalMode = CrimschistBlackboxDifferentialBlock.modeOf(neighbourTE.getBlockState(), neighbourTE.getBlockPos(), currentTE.getBlockPos());
        if(optionalMode.isPresent()){
            var mode = optionalMode.get();
            if(mode == CrimschistBlackboxDifferentialBlock.Mode.NONE && currentTE.hasSource() && currentTE.source == neighbourTE.getBlockPos()){
                RotationPropagatorInvoker.invokePropagateMissingSource(currentTE);
            }else if(mode == CrimschistBlackboxDifferentialBlock.Mode.IN){
                propagateNewSourceBlackboxDifferential(neighbourTE);
            }else if(mode == CrimschistBlackboxDifferentialBlock.Mode.OUT){
                float speedOfCurrent = currentTE.getTheoreticalSpeed();
                float speedOfNeighbour = neighbourTE.getTheoreticalSpeed();
                float newSpeed = RotationPropagatorInvoker.invokeGetConveyedSpeed(currentTE, neighbourTE);
                float oppositeSpeed = RotationPropagatorInvoker.invokeGetConveyedSpeed(neighbourTE, currentTE);
                if (newSpeed == 0 && oppositeSpeed == 0)
                    return;
                boolean incompatible =
                        Math.signum(newSpeed) != Math.signum(speedOfNeighbour) && (newSpeed != 0 && speedOfNeighbour != 0);
                boolean tooFast = Math.abs(newSpeed) > AllConfigs.server().kinetics.maxRotationSpeed.get()
                        || Math.abs(oppositeSpeed) > AllConfigs.server().kinetics.maxRotationSpeed.get();
                if(currentTE instanceof IOverspeedKineticBE || currentTE instanceof GaugeBlockEntity){
                    tooFast = Math.abs(newSpeed) > Constants.MAX_OVERCLOCKING_SPEED || Math.abs(oppositeSpeed) > Constants.MAX_OVERCLOCKING_SPEED;
                }
                boolean speedChangedTooOften = currentTE.getFlickerScore() > 128;
                if (tooFast || speedChangedTooOften) {
                    world.destroyBlock(currentTE.getBlockPos(), true);
                    return;
                }
                if(Math.abs(speedOfCurrent) > Math.abs(oppositeSpeed) && currentTE.hasSource() && currentTE.source != neighbourTE.getBlockPos()){
                    world.destroyBlock(currentTE.getBlockPos(), true);
                    return;
                }
                if (incompatible) {
                    world.destroyBlock(currentTE.getBlockPos(), true);
                    return;
                }
                if(oppositeSpeed != speedOfCurrent){
                    float prevSpeed = currentTE.getSpeed();
                    currentTE.setSource(neighbourTE.getBlockPos());
                    currentTE.setSpeed(RotationPropagatorInvoker.invokeGetConveyedSpeed(neighbourTE, currentTE));
                    currentTE.onSpeedChanged(prevSpeed);
                    currentTE.sendData();

                    RotationPropagatorInvoker.invokePropagateNewSource(currentTE);
                }
            }
        }
    }


    private static List<KineticBlockEntity> getConnectedNeighboursBlackboxDifferential(CrimschistBlackboxDifferentialBlockEntity be, @Nullable CrimschistBlackboxDifferentialBlock.Mode mode) {
        List<KineticBlockEntity> neighbours = new LinkedList<>();
        BlockState blockState = be.getBlockState();
        BlockPos selfPos = be.getBlockPos();
        for (BlockPos neighbourPos : RotationPropagatorInvoker.invokeGetPotentialNeighbourLocations(be)) {
            if(mode != null) {
                if (CrimschistBlackboxDifferentialBlock.modeOf(blockState, selfPos, neighbourPos).filter(mode::equals).isEmpty()) {
                    continue;
                }
            }
            final KineticBlockEntity neighbourBE = RotationPropagatorInvoker.invokeFindConnectedNeighbour(be, neighbourPos);
            if (neighbourBE == null)
                continue;

            neighbours.add(neighbourBE);
        }
        return neighbours;
    }
}
