package io.github.cvrunmin.create_andertech.content.fluids.infusion_vat;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import io.github.cvrunmin.create_andertech.foundation.MyLang;
import io.github.cvrunmin.create_andertech.index.AllBlockEntityTypes;
import io.github.cvrunmin.create_andertech.index.AllBlocks;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.lang.LangBuilder;
import net.createmod.catnip.lang.LangNumberFormat;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Clearable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

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

    public InfusionVatInventory inputInventory;
    protected InfusionVatInventory outputInventory;
    public InfusionVatFluidTankBehaviour inputTank;
    protected InfusionVatFluidTankBehaviour outputTank;

    protected IItemHandlerModifiable itemCapability;
    protected IFluidHandler fluidCapability;
    protected boolean contentChanged;

    private Couple<InfusionVatInventory> invs;
    private final Couple<InfusionVatFluidTankBehaviour> tanks;

    public InfusionVatBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        xLen = 1;
        zLen = 1;
        height = 1;
        inputInventory = new InfusionVatInventory(getExpectedItemSlotCount(1), this);
        inputInventory.whenContentsChanged($ -> contentChanged = true);
        outputInventory = ((InfusionVatInventory) new InfusionVatInventory(getExpectedItemSlotCount(1), this).forbidInsertion().withMaxStackSize(64));
        // initialization of tanks done in addBehaviours, called in super constructor
        itemCapability = new CombinedInvWrapper(inputInventory, outputInventory);
        fluidCapability = new CombinedTankWrapper(outputTank, inputTank);
        contentChanged = true;

        invs = Couple.create(inputInventory, outputInventory);
        tanks = Couple.create(inputTank, outputTank);
    }

    public IItemHandler getActualItemCapability(){
        if(isController()) return itemCapability;
        if(getControllerBE() != null) return getControllerBE().itemCapability;
        return itemCapability;
    }

    public IFluidHandler getActualFluidCapability(){
        if(isController()) return fluidCapability;
        if(getControllerBE() != null) return getControllerBE().fluidCapability;
        return fluidCapability;
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                AllBlockEntityTypes.INFUSION_VAT.get(),
                (be, context) -> be.getActualItemCapability()
        );
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                AllBlockEntityTypes.INFUSION_VAT.get(),
                (be, context) -> be.getActualFluidCapability()
        );
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        inputTank = new InfusionVatFluidTankBehaviour(InfusionVatFluidTankBehaviour.INPUT, this, getExpectedFluidCapacity(1));
        outputTank = new InfusionVatFluidTankBehaviour(InfusionVatFluidTankBehaviour.OUTPUT, this, getExpectedFluidCapacity(1)).forbidInsertion();
        behaviours.add(inputTank);
        behaviours.add(outputTank);
    }

    @Override
    public void clearContent() {
        inputInventory.clearContent();
        outputInventory.clearContent();
    }

    @Override
    public void destroy() {
        super.destroy();
        ItemHelper.dropContents(level, worldPosition, inputInventory);
        ItemHelper.dropContents(level, worldPosition, outputInventory);
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
        removeController(true);
        lastKnownPos = worldPosition;
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

    public int getBlockAmount(){
        return Math.max(0, xLen) * Math.max(0, zLen) * Math.max(0, height);
    }

    public int getMaxWidth(){
        return 7;
    }

    public int getMaxHeight(){
        return 7;
    }

    protected int getExpectedItemSlotCount(int blockAmount){
        return 2 * blockAmount;
    }

    protected int getExpectedFluidCapacity(int blockAmount){
        return 2000 * blockAmount;
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

    public List<ItemStack> setCapacities(int blockAmount){
        var itemStackSize = getExpectedItemSlotCount(blockAmount);
        var fluidStackSize = getExpectedFluidCapacity(blockAmount);
        var droppedInputs = inputInventory.resizeCapacity(itemStackSize);
        var droppedOutputs = outputInventory.resizeCapacity(itemStackSize);
        inputTank.setCapacity(fluidStackSize);
        outputTank.setCapacity(fluidStackSize);
        inputTank.ensureLegalSpace();
        outputTank.ensureLegalSpace();
        return Stream.concat(droppedInputs.stream(), droppedOutputs.stream()).toList();
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
            var blockAmount = getBlockAmount();
            inputInventory.resizeCapacity(getExpectedItemSlotCount(blockAmount));
            outputInventory.resizeCapacity(getExpectedItemSlotCount(blockAmount));
            inputInventory.deserializeNBT(registries, tag.getCompound("InputItems"));
            outputInventory.deserializeNBT(registries, tag.getCompound("OutputItems"));
            inputTank.setCapacity(getExpectedFluidCapacity(blockAmount));
            outputTank.setCapacity(getExpectedFluidCapacity(blockAmount));
            inputTank.ensureLegalSpace();
            outputTank.ensureLegalSpace();
        }

        shouldUpdateCapability = true;
        if(!clientPacket) return;
        boolean controllerChanged = !Objects.equals(controllerBefore, controllerPos);
        if(controllerChanged || prevXLen != xLen || prevZLen != zLen || prevHeight != height){
            if(hasLevel())
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 16);
            if(isController()){
                var blockAmount = getBlockAmount();
                inputInventory.resizeCapacity(getExpectedItemSlotCount(blockAmount));
                outputInventory.resizeCapacity(getExpectedItemSlotCount(blockAmount));
                inputTank.setCapacity(getExpectedFluidCapacity(blockAmount));
                outputTank.setCapacity(getExpectedFluidCapacity(blockAmount));
            }
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
            tag.put("InputItems", inputInventory.serializeNBT(registries));
            tag.put("OutputItems", outputInventory.serializeNBT(registries));
            tag.putInt("XLength", xLen);
            tag.putInt("ZLength", zLen);
            tag.putInt("Height", height);
        }
        super.write(tag, registries, clientPacket);
        if (!clientPacket)
            return;
        if (queuedSync)
            tag.putBoolean("LazySync", true);
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
            inputInventory.resizeCapacity(getExpectedItemSlotCount(1));
            outputInventory.resizeCapacity(getExpectedItemSlotCount(1));
            inputTank.setCapacity(getExpectedFluidCapacity(1));
            outputTank.setCapacity(getExpectedFluidCapacity(1));
            inputTank.ensureLegalSpace();
            outputTank.ensureLegalSpace();
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

    public void notifyChangeOfContents() {
        contentChanged = true;
    }

    public float getTotalFluidUnits(float partialTicks){
        int renderedFluids = 0;
        float totalUnits = 0;
        for(var tank : tanks){
            if(tank == null) continue;
            if(tank.tank.getRenderedFluid().isEmpty()) continue;
            float unit = tank.tank.getTotalUnits(partialTicks);
            if(unit < 1) continue;
            totalUnits += unit;
            renderedFluids++;
        }
        if(renderedFluids == 0) return 0;
        if(totalUnits < 1) return 0;
        return totalUnits;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        MyLang.builder().translate("gui.goggles.vat_contents").forGoggles(tooltip);
        var itemCap = getActualItemCapability();
        var fluidCap = getActualFluidCapability();
        boolean isEmpty = true;
        for (int i = 0; i < itemCap.getSlots(); i++) {
            var stack = itemCap.getStackInSlot(i);
            if(stack.isEmpty()) continue;
            MyLang.builder().text("").add(Component.translatable(stack.getDescriptionId()).withStyle(ChatFormatting.GRAY))
                    .add(MyLang.builder().text(" x" + stack.getCount()).style(ChatFormatting.GREEN))
                    .forGoggles(tooltip, 1);
            isEmpty = false;
        }
        LangBuilder mb = CreateLang.translate("generic.unit.millibuckets");
        for (int i = 0; i < fluidCap.getTanks(); i++) {
            var stack = fluidCap.getFluidInTank(i);
            if(stack.isEmpty()) continue;
            MyLang.builder().text("").add(stack.getHoverName().copy().withStyle(ChatFormatting.GRAY))
                    .add(MyLang.builder().text(" "))
                    .add(MyLang.builder().text(LangNumberFormat.format(stack.getAmount())).add(mb).style(ChatFormatting.BLUE))
                    .forGoggles(tooltip, 1);
            isEmpty = false;
        }
        if(isEmpty) tooltip.removeFirst(); // do not show any tooltips if empty
        return true;
    }
}
