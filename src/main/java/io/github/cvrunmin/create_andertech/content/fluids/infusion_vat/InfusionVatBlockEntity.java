package io.github.cvrunmin.create_andertech.content.fluids.infusion_vat;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import io.github.cvrunmin.create_andertech.index.AllBlocks;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Objects;

public class InfusionVatBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation, Clearable {
    protected boolean shouldUpdateConnectivity = false;
    protected BlockPos controllerPos;
    protected int xLen;
    protected int zLen;
    protected int height;
    protected BlockPos lastKnownPos;
    protected int syncCooldown;
    protected boolean queuedSync;
    protected boolean shouldUpdateCapability;

    public InfusionVatBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }

    @Override
    public void clearContent() {

    }

    public boolean isController(){
        return controllerPos == null ||
                (worldPosition.getX() == controllerPos.getX() &&
                        worldPosition.getY() == controllerPos.getY() &&
                        worldPosition.getZ() == controllerPos.getZ());
    }

    protected void updateConnectivity(){
        shouldUpdateConnectivity = false;
        if(level.isClientSide) return;
        if(!isController()) return;
        InfusionVatConnectivityHandler.tryFormMultiBlock(this);
    }

    public BlockPos getController() {
        return isController() ? worldPosition : controllerPos;
    }

    public InfusionVatBlockEntity getControllerBE(){
        if(isController() || !hasLevel()) return this;
        BlockEntity be = level.getBlockEntity(controllerPos);
        if(be instanceof InfusionVatBlockEntity) return ((InfusionVatBlockEntity) be);
        return null;
    }

    @Override
    public void tick() {
        super.tick();
        if (syncCooldown > 0) {
            syncCooldown--;
            if (syncCooldown == 0 && queuedSync)
                sendData();
        }

        if (lastKnownPos == null)
            lastKnownPos = getBlockPos();
        else if (!lastKnownPos.equals(worldPosition) && worldPosition != null) {
            onPositionChanged();
            return;
        }

        if (shouldUpdateCapability) {
            shouldUpdateCapability = false;
            refreshCapability();
        }
        if (shouldUpdateConnectivity)
            updateConnectivity();
    }

    private void onPositionChanged() {

    }

    public int getXLength() {
        return xLen;
    }

    public int getZLength() {
        return zLen;
    }

    public int getHeight() {
        return height;
    }

    public int getMaxWidth(){
        return 7;
    }

    public int getMaxHeight(){
        return 7;
    }

    public void setController(BlockPos controller) {
        if (level.isClientSide && !isVirtual())
            return;
        if (controller.equals(this.controllerPos))
            return;
        this.controllerPos = controller;
        refreshCapability();
        setChanged();
        sendData();
    }

    void refreshCapability() {
        invalidateCapabilities();
    }

    public void preventConnectivityUpdate() {
        shouldUpdateConnectivity = false;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setXLength(int xLen) {
        this.xLen = xLen;
    }

    public void setZLength(int zLen) {
        this.zLen = zLen;
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);

        BlockPos controllerBefore = controllerPos;
        int prevXLen = xLen;
        int prevZLen = zLen;
        int prevHeight = height;

        shouldUpdateConnectivity = tag.contains("Uninitialized");

        lastKnownPos = null;
        if (tag.contains("LastKnownPos"))
            lastKnownPos = NBTHelper.readBlockPos(tag, "LastKnownPos");
        controllerPos = null;
        if (tag.contains("Controller"))
            controllerPos = NBTHelper.readBlockPos(tag, "Controller");
        if(isController()){
            xLen = tag.getInt("XLength");
            zLen = tag.getInt("ZLength");
            height = tag.getInt("Height");
        }

        shouldUpdateConnectivity = true;
        if(!clientPacket) return;
        boolean controllerChanged = !Objects.equals(controllerBefore, controllerPos);
        if(controllerChanged || prevXLen != xLen || prevZLen != zLen || prevHeight != height){
            if(hasLevel())
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 16);
            invalidateRenderBoundingBox();
        }
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        if(shouldUpdateConnectivity)
            tag.putBoolean("Uninitialized", true);
        if (lastKnownPos != null)
            tag.put("LastKnownPos", NbtUtils.writeBlockPos(lastKnownPos));
        if (!isController())
            tag.put("Controller", NbtUtils.writeBlockPos(controllerPos));
        if (isController()) {
//            tag.put("TankContent", tankInventory.writeToNBT(registries, new CompoundTag()));
            tag.putInt("XLength", xLen);
            tag.putInt("ZLength", zLen);
            tag.putInt("Height", height);
        }
        super.write(tag, registries, clientPacket);
    }

    @Override
    public void writeSafe(CompoundTag tag, HolderLookup.Provider registries) {
        if (isController()) {
            tag.putInt("XLength", xLen);
            tag.putInt("ZLength", zLen);
            tag.putInt("Height", height);
        }
    }

    public void notifyMultiUpdated() {
        var state = getBlockState();
        if(AllBlocks.INFUSION_VAT.has(state)){
            state = state.setValue(InfusionVatBlock.TOP, getController().getY() + height - 1 == getBlockPos().getY());
            state = state.setValue(InfusionVatBlock.BOTTOM, getController().getY() == getBlockPos().getY());
            level.setBlock(getBlockPos(), state, Block.UPDATE_CLIENTS | Block.UPDATE_INVISIBLE);
        }
        if(isController()){
            updateShape();
        }
        // TODO: update stacks
        setChanged();
    }

    public void updateShape(){
        for (int yOffset = 0; yOffset < height; yOffset++) {
            for (int xOffset = 0; xOffset < xLen; xOffset++) {
                for (int zOffset = 0; zOffset < zLen; zOffset++) {
                    BlockPos pos = getBlockPos().offset(xOffset, yOffset, zOffset);
                    var state = level.getBlockState(pos);
                    if(!AllBlocks.INFUSION_VAT.has(state)) continue;

                    boolean east = xOffset == xLen - 1;
                    boolean west = xOffset == 0;
                    boolean south = zOffset == zLen - 1;
                    boolean north = zOffset == 0;
                    level.setBlock(pos, state.setValue(InfusionVatBlock.EAST, east)
                                             .setValue(InfusionVatBlock.WEST, west)
                                             .setValue(InfusionVatBlock.NORTH, north)
                                             .setValue(InfusionVatBlock.SOUTH, south),
                            Block.UPDATE_CLIENTS | Block.UPDATE_INVISIBLE | Block.UPDATE_KNOWN_SHAPE);
                    level.getLightEngine().checkBlock(pos);
                }
            }
        }
    }

    public void removeController(boolean keepContents) {
        if (level.isClientSide)
            return;
        shouldUpdateConnectivity = true;
        if(!keepContents){
            // TODO: clear contents
        }
        controllerPos = null;
        xLen = 1;
        zLen = 1;
        height = 1;
        BlockState state = getBlockState();
        if(AllBlocks.INFUSION_VAT.has(state)){
            state = state.setValue(InfusionVatBlock.TOP, true);
            state = state.setValue(InfusionVatBlock.BOTTOM, true);
            state = state.setValue(InfusionVatBlock.EAST, true)
                    .setValue(InfusionVatBlock.WEST, true)
                    .setValue(InfusionVatBlock.NORTH, true)
                    .setValue(InfusionVatBlock.SOUTH, true);
            level.setBlock(getBlockPos(), state, Block.UPDATE_CLIENTS | Block.UPDATE_INVISIBLE | Block.UPDATE_KNOWN_SHAPE);
        }
        refreshCapability();
        setChanged();
        sendData();
    }


    @Override
    protected AABB createRenderBoundingBox() {
        if (isController())
            return super.createRenderBoundingBox().expandTowards(xLen - 1, height - 1, zLen - 1);
        else
            return super.createRenderBoundingBox();
    }
}
