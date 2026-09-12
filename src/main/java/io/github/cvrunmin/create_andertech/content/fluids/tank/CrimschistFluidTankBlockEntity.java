package io.github.cvrunmin.create_andertech.content.fluids.tank;

import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import io.github.cvrunmin.create_andertech.foundation.interfaces.IGrandSuperSmartBlockEntity;
import io.github.cvrunmin.create_andertech.index.AllBlockEntityTypes;
import io.github.cvrunmin.create_andertech.mixin.fluid.FluidTankBlockEntityAccessor;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import java.util.Objects;

import static java.lang.Math.abs;

public class CrimschistFluidTankBlockEntity extends FluidTankBlockEntity {

    private static final int MAX_WIDTH = 5;

    public CrimschistFluidTankBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }


    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                AllBlockEntityTypes.CRIMSCHIST_FLUID_TANK.get(),
                (be, context) -> {
                    if (be.fluidCapability == null)
                        be.refreshCapability();
                    return be.fluidCapability;
                }
        );
    }


    protected SmartFluidTank createInventory() {
        return new SmartFluidTank(getMyCapacityMultiplier(), this::onFluidStackChanged);
    }


    public void applyFluidTankSize(int blocks) {
        tankInventory.setCapacity(blocks * getMyCapacityMultiplier());
        int overflow = tankInventory.getFluidAmount() - tankInventory.getCapacity();
        if (overflow > 0)
            tankInventory.drain(overflow, IFluidHandler.FluidAction.EXECUTE);
        forceFluidLevelUpdate = true;
    }

    @Override
    public void removeController(boolean keepFluids) {
        if (level.isClientSide)
            return;
        updateConnectivity = true;
        if (!keepFluids)
            applyFluidTankSize(1);
        controller = null;
        width = 1;
        height = 1;
        boiler.clear();
        onFluidStackChanged(tankInventory.getFluid());

        BlockState state = getBlockState();
        if (CrimschistFluidTankBlock.isTank(state)) {
            state = state.setValue(CrimschistFluidTankBlock.BOTTOM, true);
            state = state.setValue(CrimschistFluidTankBlock.TOP, true);
            state = state.setValue(CrimschistFluidTankBlock.SHAPE, window ? FluidTankBlock.Shape.WINDOW : FluidTankBlock.Shape.PLAIN);
            state = state.setValue(CrimschistFluidTankBlock.LARGE_WINDOW, false);
            getLevel().setBlock(worldPosition, state, Block.UPDATE_CLIENTS | Block.UPDATE_INVISIBLE | Block.UPDATE_KNOWN_SHAPE);
        }

        refreshCapability();
        setChanged();
        sendData();
    }

    @Override
    public void setWindows(boolean window) {
        this.window = window;
        for (int yOffset = 0; yOffset < height; yOffset++) {
            for (int xOffset = 0; xOffset < width; xOffset++) {
                for (int zOffset = 0; zOffset < width; zOffset++) {

                    BlockPos pos = this.worldPosition.offset(xOffset, yOffset, zOffset);
                    BlockState blockState = level.getBlockState(pos);
                    if (!CrimschistFluidTankBlock.isTank(blockState))
                        continue;

                    FluidTankBlock.Shape shape = FluidTankBlock.Shape.PLAIN;
                    boolean isLargeWindow = false;
                    Direction largeWindowDirection = Direction.NORTH;
                    if (window) {
                        switch (width){
                            case 1:
                                shape = FluidTankBlock.Shape.WINDOW;
                                break;
                            case 2:
                                if (xOffset == 0)
                                    shape = zOffset == 0 ? FluidTankBlock.Shape.WINDOW_NW : FluidTankBlock.Shape.WINDOW_SW;
                                else
                                    shape = zOffset == 0 ? FluidTankBlock.Shape.WINDOW_NE : FluidTankBlock.Shape.WINDOW_SE;
                                break;
                            case 3:
                                if(abs(abs(xOffset) - abs(zOffset)) == 1)
                                    shape = FluidTankBlock.Shape.WINDOW;
                                break;
                            default:
                                if(xOffset == 0){
                                    if(zOffset == 0)
                                        shape = FluidTankBlock.Shape.WINDOW_NW;
                                    else if(zOffset == width - 1)
                                        shape = FluidTankBlock.Shape.WINDOW_SW;
                                    else {
                                        shape = FluidTankBlock.Shape.WINDOW;
                                        isLargeWindow = true;
                                        largeWindowDirection = Direction.WEST;
                                    }
                                } else if(xOffset == width - 1){
                                    if(zOffset == 0)
                                        shape = FluidTankBlock.Shape.WINDOW_NE;
                                    else if(zOffset == width - 1)
                                        shape = FluidTankBlock.Shape.WINDOW_SE;
                                    else {
                                        shape = FluidTankBlock.Shape.WINDOW;
                                        isLargeWindow = true;
                                        largeWindowDirection = Direction.EAST;
                                    }
                                } else if (zOffset == 0) {
                                    shape = FluidTankBlock.Shape.WINDOW;
                                    isLargeWindow = true;
                                    largeWindowDirection = Direction.NORTH;
                                } else if (zOffset == width - 1) {
                                    shape = FluidTankBlock.Shape.WINDOW;
                                    isLargeWindow = true;
                                    largeWindowDirection = Direction.SOUTH;
                                }
                        }
                    }

                    level.setBlock(pos, blockState.setValue(CrimschistFluidTankBlock.SHAPE, shape)
                                                .setValue(CrimschistFluidTankBlock.LARGE_WINDOW, isLargeWindow)
                                                .setValue(CrimschistFluidTankBlock.LARGE_WINDOW_DIRECTION, largeWindowDirection),
                            Block.UPDATE_CLIENTS | Block.UPDATE_INVISIBLE | Block.UPDATE_KNOWN_SHAPE);
                    level.getChunkSource().getLightEngine().checkBlock(pos);
                }
            }
        }
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        ((IGrandSuperSmartBlockEntity)this).grandsuperRead(compound, registries, clientPacket);

        BlockPos controllerBefore = controller;
        int prevSize = width;
        int prevHeight = height;
        int prevLum = luminosity;

        updateConnectivity = compound.contains("Uninitialized");
        luminosity = compound.getInt("Luminosity");

        lastKnownPos = null;
        if (compound.contains("LastKnownPos"))
            lastKnownPos = NBTHelper.readBlockPos(compound, "LastKnownPos");

        controller = null;
        if (compound.contains("Controller"))
            controller = NBTHelper.readBlockPos(compound, "Controller");

        if (isController()) {
            window = compound.getBoolean("Window");
            width = compound.getInt("Size");
            height = compound.getInt("Height");
            tankInventory.setCapacity(getTotalTankSize() * getMyCapacityMultiplier());

            tankInventory.readFromNBT(registries, compound.getCompound("TankContent"));
            if (tankInventory.getSpace() < 0)
                tankInventory.drain(-tankInventory.getSpace(), IFluidHandler.FluidAction.EXECUTE);
        }

        boiler.read(compound.getCompound("Boiler"), width * width * height);

        if (compound.contains("ForceFluidLevel") || ((FluidTankBlockEntityAccessor)this).createandertech$getFluidLevel() == null)
            ((FluidTankBlockEntityAccessor)this).createandertech$setFluidLevel(LerpedFloat.linear().startWithValue(getFillState()));

        updateCapability = true;

        if (!clientPacket)
            return;

        boolean changeOfController = !Objects.equals(controllerBefore, controller);
        if (changeOfController || prevSize != width || prevHeight != height) {
            if (hasLevel())
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 16);
            if (isController())
                tankInventory.setCapacity(getMyCapacityMultiplier() * getTotalTankSize());
            invalidateRenderBoundingBox();
        }
        if (isController()) {
            float fillState = getFillState();
            if (compound.contains("ForceFluidLevel") || ((FluidTankBlockEntityAccessor)this).createandertech$getFluidLevel() == null)
                ((FluidTankBlockEntityAccessor)this).createandertech$setFluidLevel(LerpedFloat.linear().startWithValue(getFillState()));
            ((FluidTankBlockEntityAccessor)this).createandertech$getFluidLevel().chase(fillState, 0.5f, LerpedFloat.Chaser.EXP);
        }
        if (luminosity != prevLum && hasLevel())
            level.getChunkSource().getLightEngine().checkBlock(worldPosition);

        if (compound.contains("LazySync")) {
            LerpedFloat lerpedFloat = ((FluidTankBlockEntityAccessor) this).createandertech$getFluidLevel();
            lerpedFloat.chase(lerpedFloat.getChaseTarget(), 0.125f, LerpedFloat.Chaser.EXP);
        }
    }


    protected void updateConnectivity() {
        super.updateConnectivity();
    }

    @Override
    public int getMaxWidth() {
        return MAX_WIDTH;
    }

    public boolean isWindowOpened(){
        return window;
    }

    public int getLuminosity(){
        return luminosity;
    }

    public static int getMyCapacityMultiplier(){
        return getCapacityMultiplier() * 2;
    }

    @Override
    public int getTankSize(int tank) {
        return getMyCapacityMultiplier();
    }

    @Override
    public void setController(BlockPos controller) {
        if (level.isClientSide && !isVirtual())
            return;
        if (controller.equals(this.controller))
            return;
        this.controller = controller;
        refreshCapability();
        setChanged();
        sendData();
    }

    void refreshCapability() {
        fluidCapability = handlerForCapability();
        invalidateCapabilities();
    }

    private IFluidHandler handlerForCapability() {
        return isController() ? (boiler.isActive() ? boiler.createHandler() : tankInventory)
                : ((getControllerBE() != null) ? getControllerBE().handlerForCapability() : new FluidTank(0));
    }

    @Override
    public CrimschistFluidTankBlockEntity getControllerBE() {
        if (isController() || !hasLevel())
            return this;
        BlockEntity blockEntity = level.getBlockEntity(controller);
        if (blockEntity instanceof CrimschistFluidTankBlockEntity)
            return (CrimschistFluidTankBlockEntity) blockEntity;
        return null;
    }


    @Override
    public void notifyMultiUpdated() {
        BlockState state = this.getBlockState();
        if (CrimschistFluidTankBlock.isTank(state)) { // safety
            state = state.setValue(CrimschistFluidTankBlock.BOTTOM, getController().getY() == getBlockPos().getY());
            state = state.setValue(CrimschistFluidTankBlock.TOP, getController().getY() + height - 1 == getBlockPos().getY());
            level.setBlock(getBlockPos(), state, Block.UPDATE_CLIENTS | Block.UPDATE_INVISIBLE);
        }
        if (isController())
            setWindows(window);
        onFluidStackChanged(tankInventory.getFluid());
        updateBoilerState();
        setChanged();
    }
}
