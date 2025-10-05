package io.github.cvrunmin.create_andertech.content.kinetics.belt;

import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.kinetics.belt.BeltSlope;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.BeltInventory;
import com.simibubi.create.content.kinetics.belt.transport.BeltMovementHandler;
import com.simibubi.create.content.kinetics.belt.transport.BeltTunnelInteractionHandler;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.tunnel.BrassTunnelBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import io.github.cvrunmin.create_andertech.AllBlocks;
import io.github.cvrunmin.create_andertech.foundation.IOverspeedKineticBE;
import io.github.cvrunmin.create_andertech.foundation.interfaces.IGrandSuperKineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static com.simibubi.create.content.kinetics.belt.BeltPart.MIDDLE;
import static com.simibubi.create.content.kinetics.belt.BeltSlope.HORIZONTAL;

public class OCBeltBlockEntity extends BeltBlockEntity implements IOverspeedKineticBE {
    public OCBeltBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public BeltInventory getInventoryRaw() {
        return this.inventory;
    }

    public void setInventoryRaw(BeltInventory inv){
        this.inventory = inv;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        behaviours.removeIf(behaviour -> behaviour instanceof DirectBeltInputBehaviour);
        behaviours.add(new DirectBeltInputBehaviour(this).onlyInsertWhen(this::canInsertFrom)
                .setInsertionHandler(this::tryInsertingFromSide).considerOccupiedWhen(this::isOccupied));
    }

    @Override
    public void tick() {
        if (beltLength == 0)
            OCBeltBlock.initBelt(level, worldPosition);
        ((IGrandSuperKineticBlockEntity) (this)).grandsuperTick();

        if (!AllBlocks.OC_BELT.has(level.getBlockState(worldPosition)))
            return;

        initializeItemHandler();

        if (!isController())
            return;

        invalidateRenderBoundingBox();

        getInventory().tick();

        if (getSpeed() == 0)
            return;

        // Move Entities
        if (passengers == null)
            passengers = new HashMap<>();

        List<Entity> toRemove = new ArrayList<>();
        passengers.forEach((entity, info) -> {
            boolean canBeTransported = BeltMovementHandler.canBeTransported(entity);
            boolean leftTheBelt =
                    info.getTicksSinceLastCollision() > ((getBlockState().getValue(BeltBlock.SLOPE) != HORIZONTAL) ? 3 : 1);
            if (!canBeTransported || leftTheBelt) {
                toRemove.add(entity);
                return;
            }

            info.tick();
            BeltMovementHandler.transportEntity(this, entity, info);
        });
        toRemove.forEach(passengers::remove);
    }

    @Override
    public OCBeltBlockEntity getControllerBE() {
        var be = super.getControllerBE();
        if(!(be instanceof OCBeltBlockEntity)) return null;
        return (OCBeltBlockEntity) be;
    }

    @Override
    public boolean hasPulley() {
        if (!AllBlocks.OC_BELT.has(getBlockState()))
            return false;
        return getBlockState().getValue(OCBeltBlock.PART) != MIDDLE;
    }

    @Override
    public boolean applyColor(DyeColor colorIn) {
        if (colorIn == null) {
            if (!color.isPresent())
                return false;
        } else if (color.isPresent() && color.get() == colorIn)
            return false;
        if (level.isClientSide())
            return true;

        for (BlockPos blockPos : OCBeltBlock.getBeltChain(level, getController())) {
            BeltBlockEntity belt = BeltHelper.getSegmentBE(level, blockPos);
            if (belt == null)
                continue;
            belt.color = Optional.ofNullable(colorIn);
            belt.setChanged();
            belt.sendData();
        }

        return true;
    }

    private boolean canInsertFrom(Direction side) {
        if (getSpeed() == 0)
            return false;
        BlockState state = getBlockState();
        if (state.hasProperty(OCBeltBlock.SLOPE) && (state.getValue(OCBeltBlock.SLOPE) == BeltSlope.SIDEWAYS
                || state.getValue(OCBeltBlock.SLOPE) == BeltSlope.VERTICAL))
            return false;
        return getMovementFacing() != side.getOpposite();
    }

    private boolean isOccupied(Direction side) {
        BeltBlockEntity nextBeltController = getControllerBE();
        if (nextBeltController == null)
            return true;
        BeltInventory nextInventory = nextBeltController.getInventory();
        if (nextInventory == null)
            return true;
        if (getSpeed() == 0)
            return true;
        if (getMovementFacing() == side.getOpposite())
            return true;
        if (!nextInventory.canInsertAtFromSide(index, side))
            return true;
        return false;
    }

    private ItemStack tryInsertingFromSide(TransportedItemStack transportedStack, Direction side, boolean simulate) {
        BeltBlockEntity nextBeltController = getControllerBE();
        ItemStack inserted = transportedStack.stack;
        ItemStack empty = ItemStack.EMPTY;

        if (!OCBeltBlock.canTransportObjects(getBlockState()))
            return inserted;
        if (nextBeltController == null)
            return inserted;
        BeltInventory nextInventory = nextBeltController.getInventory();
        if (nextInventory == null)
            return inserted;

        BlockEntity teAbove = level.getBlockEntity(worldPosition.above());
        if (teAbove instanceof BrassTunnelBlockEntity tunnelBE) {
            if (tunnelBE.hasDistributionBehaviour()) {
                if (!tunnelBE.getStackToDistribute()
                        .isEmpty())
                    return inserted;
                if (!tunnelBE.testFlapFilter(side.getOpposite(), inserted))
                    return inserted;
                if (!simulate) {
                    BeltTunnelInteractionHandler.flapTunnel(nextInventory, index, side.getOpposite(), true);
                    tunnelBE.setStackToDistribute(inserted, side.getOpposite());
                }
                return empty;
            }
        }

        if (isOccupied(side))
            return inserted;
        if (simulate)
            return empty;

        transportedStack = transportedStack.copy();
        transportedStack.beltPosition = index + .5f - Math.signum(getDirectionAwareBeltMovementSpeed()) / 16f;

        Direction movementFacing = getMovementFacing();
        if (!side.getAxis()
                .isVertical()) {
            if (movementFacing != side) {
                transportedStack.sideOffset = side.getAxisDirection()
                        .getStep() * .675f;
                if (side.getAxis() == Direction.Axis.X)
                    transportedStack.sideOffset *= -1;
            } else {
                // This creates a smoother transition from belt to belt
                float extraOffset = transportedStack.prevBeltPosition != 0
                        && BeltHelper.getSegmentBE(level, worldPosition.relative(movementFacing.getOpposite())) != null
                        ? .26f
                        : 0;
                transportedStack.beltPosition =
                        getDirectionAwareBeltMovementSpeed() > 0 ? index - extraOffset : index + 1 + extraOffset;
            }
        }

        transportedStack.prevSideOffset = transportedStack.sideOffset;
        transportedStack.insertedAt = index;
        transportedStack.insertedFrom = side;
        transportedStack.prevBeltPosition = transportedStack.beltPosition;

        BeltTunnelInteractionHandler.flapTunnel(nextInventory, index, side.getOpposite(), true);

        nextInventory.addItem(transportedStack);
        nextBeltController.setChanged();
        nextBeltController.sendData();
        return empty;
    }


    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (!isItemHandlerCap(cap))
            return super.getCapability(cap, side);
        if (!OCBeltBlock.canTransportObjects(getBlockState()))
            return super.getCapability(cap, side);
        if (!isRemoved() && !itemHandler.isPresent())
            initializeItemHandler();
        return itemHandler.cast();
    }
}
