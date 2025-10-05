package io.github.cvrunmin.create_andertech.content.kinetics.speedController;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.computercraft.AbstractComputerBehaviour;
import com.simibubi.create.compat.computercraft.ComputerCraftProxy;
import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.motor.KineticScrollValueBehaviour;
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.content.kinetics.speedController.SpeedControllerBlock;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import io.github.cvrunmin.create_andertech.AllBlocks;
import io.github.cvrunmin.create_andertech.Constants;
import io.github.cvrunmin.create_andertech.content.kinetics.motor.OCKineticScrollValueBehaviour;
import io.github.cvrunmin.create_andertech.foundation.IOverspeedKineticBE;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class ChronenditeSpeedControllerBlockEntity extends KineticBlockEntity implements IOverspeedKineticBE {

    public static final int DEFAULT_SPEED = 256;
    public ScrollValueBehaviour targetSpeed;
    public AbstractComputerBehaviour computerBehaviour;

    boolean hasBracket = false;

    public ChronenditeSpeedControllerBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }


    @Override
    public void lazyTick() {
        super.lazyTick();
        updateBracket();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        int max = Constants.MAX_OVERCLOCKING_SPEED;

        targetSpeed = new OCKineticScrollValueBehaviour(CreateLang.translateDirect("kinetics.speed_controller.rotation_speed"),
                this, new ControllerValueBoxTransform());
        targetSpeed.between(-max, max);
        targetSpeed.value = DEFAULT_SPEED;
        targetSpeed.withCallback(i -> this.updateTargetRotation());
        behaviours.add(targetSpeed);
        behaviours.add(computerBehaviour = ComputerCraftProxy.behaviour(this));

        registerAwardables(behaviours, AllAdvancements.SPEED_CONTROLLER);
    }

    private void updateTargetRotation() {
        if (hasNetwork())
            getOrCreateNetwork().remove(this);
        RotationPropagator.handleRemoved(level, worldPosition, this);
        removeSource();
        attachKinetics();

        if (isCogwheelPresent() && getSpeed() != 0)
            award(AllAdvancements.SPEED_CONTROLLER);
    }

    public static float getConveyedSpeed(KineticBlockEntity cogWheel, KineticBlockEntity speedControllerIn,
                                         boolean targetingController) {
        if (!(speedControllerIn instanceof ChronenditeSpeedControllerBlockEntity))
            return 0;

        float speed = speedControllerIn.getTheoreticalSpeed();
        float wheelSpeed = cogWheel.getTheoreticalSpeed();
        float desiredOutputSpeed = getDesiredOutputSpeed(cogWheel, speedControllerIn, targetingController);

        float compareSpeed = targetingController ? speed : wheelSpeed;
        if (desiredOutputSpeed >= 0 && compareSpeed >= 0)
            return Math.max(desiredOutputSpeed, compareSpeed);
        if (desiredOutputSpeed < 0 && compareSpeed < 0)
            return Math.min(desiredOutputSpeed, compareSpeed);

        return desiredOutputSpeed;
    }

    public static float getDesiredOutputSpeed(KineticBlockEntity cogWheel, KineticBlockEntity speedControllerIn,
                                              boolean targetingController) {
        ChronenditeSpeedControllerBlockEntity speedController = (ChronenditeSpeedControllerBlockEntity) speedControllerIn;
        float targetSpeed = speedController.targetSpeed.getValue();
        float speed = speedControllerIn.getTheoreticalSpeed();
        float wheelSpeed = cogWheel.getTheoreticalSpeed();

        if (targetSpeed == 0)
            return 0;
        if (targetingController && wheelSpeed == 0)
            return 0;
        if (!speedController.hasSource()) {
            if (targetingController)
                return targetSpeed;
            return 0;
        }

        boolean wheelPowersController = Objects.equals(speedController.source, cogWheel.getBlockPos());

        if (wheelPowersController) {
            if (targetingController)
                return targetSpeed;
            return wheelSpeed;
        }

        if (targetingController)
            return speed;
        return targetSpeed;
    }

    public void updateBracket() {
        if (level != null && level.isClientSide)
            hasBracket = isCogwheelPresent();
    }

    private boolean isCogwheelPresent() {
        BlockState thisState = level.getBlockState(worldPosition);
        var direction = ChronenditeSpeedControllerBlock.getDirectionFromState(thisState);
        BlockState offsetState = level.getBlockState(worldPosition.relative(direction));
        return ICogWheel.isDedicatedCogWheel(offsetState.getBlock()) && ICogWheel.isLargeCog(offsetState)
                && offsetState.getValue(CogWheelBlock.AXIS) == ChronenditeSpeedControllerBlock.getOrthogonalAxis(direction, thisState.getValue(ChronenditeSpeedControllerBlock.AXIS));
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (computerBehaviour.isPeripheralCap(cap))
            return computerBehaviour.getPeripheralCapability();
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        computerBehaviour.removePeripheral();
    }

    @Override
    public boolean isCustomConnection(KineticBlockEntity other, BlockState state, BlockState otherState) {
        if(ICogWheel.isLargeCog(otherState) && AllBlocks.CHRONENDITE_ROTATION_SPEED_CONTROLLER.has(state)){
            var diff = other.getBlockPos().subtract(getBlockPos());
            var dir = ChronenditeSpeedControllerBlock.getDirectionFromState(state);
            if(!BlockPos.ZERO.relative(dir).equals(diff)) return false;
            var ctrlAxis = state.getValue(ChronenditeSpeedControllerBlock.AXIS);
            var orthoAxis = ChronenditeSpeedControllerBlock.getOrthogonalAxis(dir, ctrlAxis);
            var cogAxis = otherState.getValue(CogWheelBlock.AXIS);
            return orthoAxis == cogAxis;
        }
        return false;
    }

    private class ControllerValueBoxTransform extends ValueBoxTransform.Sided {

        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8, 8f, 15.5f);
        }

        @Override
        public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
            var facing = ChronenditeSpeedControllerBlock.getDirectionFromState(state);
            if(facing.getAxis() == Direction.Axis.Y){
                return super.getLocalOffset(level, pos, state).add(new Vec3(0, 3/16f, 0).scale(facing.getAxisDirection().getStep()));
            }
            return super.getLocalOffset(level, pos, state).add(new Vec3(3/16f, 3/16f, 3/16f).multiply(facing.getStepX(), 0, facing.getStepZ()));
        }

        @Override
        public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
            super.rotate(level, pos, state, ms);
            var facing = ChronenditeSpeedControllerBlock.getDirectionFromState(state);
            if(facing.getAxis() == Direction.Axis.Y) return;
            if(getSide() != Direction.UP) return;
            TransformStack.of(ms).rotateZDegrees(-AngleHelper.horizontalAngle(facing) + 180);
        }

        @Override
        protected boolean isSideActive(BlockState state, Direction direction) {
            return direction.getAxis() == ChronenditeSpeedControllerBlock.getOrthogonalAxis(ChronenditeSpeedControllerBlock.getDirectionFromState(state), state.getValue(ChronenditeSpeedControllerBlock.AXIS));
        }

        @Override
        public float getScale() {
            return 0.5f;
        }

    }

}
