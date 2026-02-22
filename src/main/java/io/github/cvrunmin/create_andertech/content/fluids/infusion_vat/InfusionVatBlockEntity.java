package io.github.cvrunmin.create_andertech.content.fluids.infusion_vat;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.ICapabilityProvider;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import io.github.cvrunmin.create_andertech.foundation.MyLang;
import io.github.cvrunmin.create_andertech.index.AllBlockEntityTypes;
import io.github.cvrunmin.create_andertech.index.AllBlocks;
import io.github.cvrunmin.create_andertech.index.CAAllRecipeTypes;
import io.github.cvrunmin.create_andertech.infrastructure.SingleRecipeInputWithFluid;
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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Stream;

public class InfusionVatBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation, Clearable {

    protected boolean shouldUpdateConnectivity = false;
    protected BlockPos controllerPos;
    protected int xLen;
    protected int zLen;
    protected int height;
    protected BlockPos lastKnownPos;
    protected boolean shouldUpdateCapability;

    public InfusionVatInventory inputInventory;
    protected InfusionVatInventory outputInventory;
    public InfusionVatFluidTankBehaviour inputTank;
    protected InfusionVatFluidTankBehaviour outputTank;

    @Nullable
    protected ICapabilityProvider<IItemHandler> itemCapabilityProvider = null;
    @Nullable
    protected ICapabilityProvider<IFluidHandler> fluidCapabilityProvider = null;
    protected boolean contentChanged;
    protected boolean forceFluidLevelUpdate;

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
        contentChanged = true;

        invs = Couple.create(inputInventory, outputInventory);
        tanks = Couple.create(inputTank, outputTank);
    }

    public IItemHandler getActualItemCapability(){
        initCapability();
        return Optional.ofNullable(itemCapabilityProvider).map(ICapabilityProvider::getCapability).orElse(new ItemStackHandler());
    }

    public IFluidHandler getActualFluidCapability(){
        initCapability();
        return Optional.ofNullable(fluidCapabilityProvider).map(ICapabilityProvider::getCapability).orElse(new FluidTank(0));
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                AllBlockEntityTypes.INFUSION_VAT.get(),
                (be, context) -> {
                    be.initCapability();
                    if(be.itemCapabilityProvider == null) return null;
                    return be.itemCapabilityProvider.getCapability();
                }
        );
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                AllBlockEntityTypes.INFUSION_VAT.get(),
                (be, context) -> {
                    be.initCapability();
                    if(be.fluidCapabilityProvider == null) return null;
                    return be.fluidCapabilityProvider.getCapability();
                }
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

        if (lastKnownPos == null)
            lastKnownPos = getBlockPos();
        else if (!lastKnownPos.equals(worldPosition) && worldPosition != null) {
            onPositionChanged();
            return;
        }

        boolean doRefreshingThisTick = false;
        if (shouldUpdateCapability) {
            shouldUpdateCapability = false;
            refreshCapability();
            doRefreshingThisTick = true;
        }
        if (shouldUpdateConnectivity) {
            updateConnectivity();
            doRefreshingThisTick = true;
        }
        if(doRefreshingThisTick || !isController()) return;
        if(!level.isClientSide){
            if(contentChanged){
                refreshRecipeProcessingList();
                contentChanged = false;
            }
            boolean hasRecipeProcessing = false;
            boolean hasUpdate = false;
            for (Iterator<Map.Entry<Integer, InfusingInfo>> iterator = processingRecipes.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<Integer, InfusingInfo> entry = iterator.next();
                InfusingInfo info = entry.getValue();
                info.processedTicks++;
                if(info.processedTicks >= info.requiredProcessingTicks){
                    var processedAmount = info.processedAmount;
                    inputInventory.extractItem(entry.getKey(), processedAmount, false);
                    outputInventory.allowInsertion();
                    outputInventory.insertItem(info.getSlot(), info.reservedOutputItem, false);
                    outputInventory.forbidInsertion();
                    inputTank.drain(info.reservedInputFluid, IFluidHandler.FluidAction.EXECUTE);
                    outputTank.allowInsertion();
                    outputTank.fill(info.reservedOutputFluid, IFluidHandler.FluidAction.EXECUTE);
                    outputTank.forbidInsertion();
                    iterator.remove();
                    hasUpdate = true;
                }else{
                    hasRecipeProcessing = true;
                }
            }
            if(hasRecipeProcessing){
                level.playSound(null, worldPosition.offset(xLen / 2, height / 2, zLen / 2), SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_AMBIENT,
                        SoundSource.BLOCKS, .75f, .5f);
            }
            if(hasUpdate) sendData();
        }
    }
    
    private HashMap<Integer, InfusingInfo> processingRecipes = new HashMap<>();

    private void refreshRecipeProcessingList(){
        var inputFluid = inputTank.getFluidInTank(0);
        var availableFluid = inputFluid.getAmount();
        var reservedOutputSlots = new HashSet<Integer>();
        var reservedOutputFluid = outputTank.getFluidInTank(0).copy();
        for (int slot = 0; slot < inputInventory.getSlots(); slot++) {
            ItemStack slotStack = inputInventory.getStackInSlot(slot);
            if(slotStack.isEmpty()) continue;
            if(processingRecipes.containsKey(slot)){
                boolean hardRefresh = false;
                var info = processingRecipes.get(slot);
                if(!ItemStack.isSameItemSameComponents(info.lastSeenStack, slotStack)){
                    hardRefresh = true;
                } else if (slotStack.isStackable()) {
                    var currentStackRelativeSizeBar = (int)(slotStack.getCount() / ((float)slotStack.getMaxStackSize()) * 4);
                    var lastSeenStackRelativeSizeBar = (int)(info.lastSeenStack.getCount() / ((float)info.lastSeenStack.getMaxStackSize()) * 4);
                    if(currentStackRelativeSizeBar != lastSeenStackRelativeSizeBar) hardRefresh = true;
                }
                if(!hardRefresh){
                    int requiredFluids = 0;
                    int processedAmount = slotStack.getCount();
                    var outputItems = info.getRecipe().getRollableResultsAsItemStacks().stream().filter(stack -> !stack.isEmpty()).findFirst().orElse(ItemStack.EMPTY);
                    var outputFluids = info.getRecipe().getFluidResults().stream().filter(stack -> !stack.isEmpty()).findFirst().orElse(FluidStack.EMPTY);
                    if(outputItems.isEmpty() && outputFluids.isEmpty()) hardRefresh = true;
                    else{
                        if(!outputItems.isEmpty()){
                            processedAmount = Math.min(outputItems.getMaxStackSize() / outputItems.getCount(), processedAmount);
                        }
                        if(!info.getRecipe().getFluidIngredients().isEmpty()){
                            var ingre = info.getRecipe().getFluidIngredients().getFirst();
                            if(!ingre.ingredient().test(inputFluid)) hardRefresh = true;
                            var fluidPerRecipe = ingre.amount();
                            requiredFluids = processedAmount * fluidPerRecipe;
                            if(availableFluid - requiredFluids < 0) hardRefresh = true;
                        }
                    }
                    var reservedOutSlot = -1;
                    if(!hardRefresh){
                        outputItems = outputItems.copyWithCount(outputItems.getCount() * processedAmount);
                        outputFluids = outputFluids.copyWithAmount(outputFluids.getAmount() * processedAmount);
                        if(info.reservedOutputSlot < outputInventory.getSlots() && outputInventory.testCanFullyInsert(info.reservedOutputSlot, outputItems)){
                            reservedOutSlot = info.reservedOutputSlot;
                        }else{
                            for (int outSlot = 0; outSlot < outputInventory.getSlots(); outSlot++) {
                                if(reservedOutputSlots.contains(outSlot)) continue;
                                if(outputInventory.testCanFullyInsert(outSlot, outputItems)){
                                    reservedOutSlot = outSlot;
                                    break;
                                }
                            }
                        }
                        if(reservedOutSlot == -1) hardRefresh = true;
                        if(!reservedOutputFluid.isEmpty() && !FluidStack.isSameFluidSameComponents(reservedOutputFluid, outputFluids)) hardRefresh = true;
                        if(reservedOutputFluid.getAmount() + outputFluids.getAmount() > outputTank.tank.tank.getCapacity()) hardRefresh = true;
                    }
                    if(!hardRefresh){
                        availableFluid -= requiredFluids;
                        reservedOutputSlots.add(reservedOutSlot);
                        if(reservedOutputFluid.isEmpty()) reservedOutputFluid = outputFluids.copy();
                        else reservedOutputFluid.setAmount(reservedOutputFluid.getAmount() + outputFluids.getAmount());
                        info.refresh(slotStack, inputFluid.copyWithAmount(requiredFluids), processedAmount, reservedOutSlot, outputItems, outputFluids);
                        continue;
                    }
                }
                processingRecipes.remove(slot);
            }
            var matchedRecipeOptional = CAAllRecipeTypes.INFUSING.<SingleRecipeInputWithFluid,InfusionVatRecipe>find(new SingleRecipeInputWithFluid(slotStack, inputFluid.copyWithAmount(availableFluid)), level);
            if(matchedRecipeOptional.isPresent()){
                var matchedRecipe = matchedRecipeOptional.get().value();
                int requiredFluids = 0;
                int processedAmount = slotStack.getCount();
                var outputItems = matchedRecipe.getRollableResultsAsItemStacks().stream().filter(stack -> !stack.isEmpty()).findFirst().orElse(ItemStack.EMPTY);
                var outputFluids = matchedRecipe.getFluidResults().stream().filter(stack -> !stack.isEmpty()).findFirst().orElse(FluidStack.EMPTY);
                if(outputItems.isEmpty() && outputFluids.isEmpty()) continue;
                if(!outputItems.isEmpty()){
                    processedAmount = Math.min(outputItems.getMaxStackSize() / outputItems.getCount(), processedAmount);
                    if(processedAmount < 1) continue;
                }
                if(!matchedRecipe.getFluidIngredients().isEmpty()){
                    var ingre = matchedRecipe.getFluidIngredients().getFirst();
                    var fluidPerRecipe = ingre.amount();
                    processedAmount = Math.min(availableFluid / fluidPerRecipe, processedAmount);
                    if(processedAmount < 1) continue;
                    requiredFluids = processedAmount * fluidPerRecipe;
                    var reservedOutSlot = -1;
                    outputItems = outputItems.copyWithCount(outputItems.getCount() * processedAmount);
                    outputFluids = outputFluids.copyWithAmount(outputFluids.getAmount() * processedAmount);
                    for (int outSlot = 0; outSlot < outputInventory.getSlots(); outSlot++) {
                        if(reservedOutputSlots.contains(outSlot)) continue;
                        if(outputInventory.testCanFullyInsert(outSlot, outputItems)){
                            reservedOutSlot = outSlot;
                            break;
                        }
                    }
                    if(reservedOutSlot == -1) continue;
                    if(!reservedOutputFluid.isEmpty() && !FluidStack.isSameFluidSameComponents(reservedOutputFluid, outputFluids)) continue;
                    if(reservedOutputFluid.getAmount() + outputFluids.getAmount() > outputTank.tank.tank.getCapacity()) continue;
                    availableFluid -= requiredFluids;
                    reservedOutputSlots.add(reservedOutSlot);
                    if(reservedOutputFluid.isEmpty()) reservedOutputFluid = outputFluids.copy();
                    else reservedOutputFluid.setAmount(reservedOutputFluid.getAmount() + outputFluids.getAmount());
                    var info = new InfusingInfo(slot, matchedRecipe);
                    info.refresh(slotStack, inputFluid.copyWithAmount(requiredFluids), processedAmount, reservedOutSlot, outputItems, outputFluids);
                    processingRecipes.put(slot, info);
                }
            }
        }
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

    void initCapability(){
        if(itemCapabilityProvider != null && itemCapabilityProvider.getCapability() != null
                && fluidCapabilityProvider != null && fluidCapabilityProvider.getCapability() != null) return;
        if(!isController()){
            var controllerBE = getControllerBE();
            if(controllerBE == null) return;
            controllerBE.initCapability();
            itemCapabilityProvider = ICapabilityProvider.of(() -> {
                if(controllerBE.isRemoved()) return null;
                if(controllerBE.itemCapabilityProvider == null) return null;
                return controllerBE.itemCapabilityProvider.getCapability();
            });
            fluidCapabilityProvider = ICapabilityProvider.of(() -> {
                if(controllerBE.isRemoved()) return null;
                if(controllerBE.fluidCapabilityProvider == null) return null;
                return controllerBE.fluidCapabilityProvider.getCapability();
            });
            return;
        }
        itemCapabilityProvider = ICapabilityProvider.of(new CombinedInvWrapper(inputInventory, outputInventory));
        fluidCapabilityProvider = ICapabilityProvider.of(new CombinedTankWrapper(outputTank, inputTank));
    }

    void refreshCapability() {
        itemCapabilityProvider = null;
        fluidCapabilityProvider = null;
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
        itemCapabilityProvider = null;
        fluidCapabilityProvider = null;
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
        if(isController()){
            if(tag.contains("ForceFluidLevel")){
                inputTank.tank.forceFluidLevel();
                outputTank.tank.forceFluidLevel();
            }
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
        if (forceFluidLevelUpdate)
            tag.putBoolean("ForceFluidLevel", true);
        forceFluidLevelUpdate = false;
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
        inputTank.tank.onFluidStackChanged();
        outputTank.tank.onFluidStackChanged();
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
        inputTank.tank.onFluidStackChanged();
        outputTank.tank.onFluidStackChanged();

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

    public class InfusingInfo{
        private final int slot;
        private final InfusionVatRecipe recipe;
        ItemStack lastSeenStack;
        ItemStack reservedOutputItem;
        FluidStack reservedOutputFluid;
        FluidStack reservedInputFluid;
        int reservedOutputSlot;

        int processedTicks;
        int requiredProcessingTicks;
        int processedAmount;

        InfusingInfo(int slot, InfusionVatRecipe recipe){
            this.slot = slot;
            this.recipe = recipe;
            this.processedTicks = -1;
        }

        public InfusionVatRecipe getRecipe() {
            return recipe;
        }

        public int getSlot() {
            return slot;
        }

        public void refresh(ItemStack lastSeenStack, FluidStack reservedInputFluid, int processedAmount, int reservedOutputSlot, ItemStack reservedOutputItem, FluidStack reservedOutputFluid){
            this.lastSeenStack = lastSeenStack.copy();
            this.reservedInputFluid = reservedInputFluid;
            this.processedAmount = processedAmount;
            this.reservedOutputSlot = reservedOutputSlot;
            this.reservedOutputItem = reservedOutputItem;
            this.reservedOutputFluid = reservedOutputFluid;

            this.requiredProcessingTicks = Math.max(20, recipe.getProcessingDuration()) * (processedAmount / (lastSeenStack.getMaxStackSize() / 4) + 1);
        }
    }
}
