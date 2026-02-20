package io.github.cvrunmin.create_andertech.content.fluids.infusion_vat;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.function.Consumer;

public class InfusionVatFluidTankBehaviour extends BlockEntityBehaviour implements IFluidHandler {

    public static final BehaviourType<InfusionVatFluidTankBehaviour> TYPE = new BehaviourType<>(),
            INPUT = new BehaviourType<>("Input"),
            OUTPUT = new BehaviourType<>("Output");

    private static final int SYNC_RATE = 8;

    protected int syncCooldown;
    protected boolean queuedSync;
    protected ResizeableTank tank;
    protected boolean extractionAllowed;
    protected boolean insertionAllowed;
    protected Runnable fluidUpdateCallback;
    protected InfusionVatBlockEntity vat;

    private BehaviourType<InfusionVatFluidTankBehaviour> behaviourType;

    public InfusionVatFluidTankBehaviour(BehaviourType<InfusionVatFluidTankBehaviour> type, InfusionVatBlockEntity be, int tankCapacity) {
        super(be);
        vat = be;
        insertionAllowed = true;
        extractionAllowed = true;
        this.behaviourType = type;
        tank = new ResizeableTank(tankCapacity);

        fluidUpdateCallback = () -> {
        };
    }

    @Override
    public BehaviourType<?> getType() {
        return behaviourType;
    }

    public InfusionVatFluidTankBehaviour withFluidUpdates(Runnable fluidUpdateCallback){
        this.fluidUpdateCallback = fluidUpdateCallback;
        return this;
    }

    public InfusionVatFluidTankBehaviour allowInsertion(){
        insertionAllowed = true;
        return this;
    }

    public InfusionVatFluidTankBehaviour allowExtraction(){
        extractionAllowed = true;
        return this;
    }

    public InfusionVatFluidTankBehaviour forbidInsertion(){
        insertionAllowed = false;
        return this;
    }

    public InfusionVatFluidTankBehaviour forbidExtraction(){
        extractionAllowed = false;
        return this;
    }


    @Override
    public void initialize() {
        super.initialize();
        if (getWorld().isClientSide)
            return;

        tank.fluidLevel.forceNextSync();
        tank.onFluidStackChanged();
    }

    @Override
    public void tick() {
        super.tick();

        if (syncCooldown > 0) {
            syncCooldown--;
            if (syncCooldown == 0 && queuedSync)
                updateFluids();
        }

        if(tank.fluidLevel != null){
            tank.fluidLevel.tickChaser();
        }
    }

    public void sendDataImmediately() {
        syncCooldown = 0;
        queuedSync = false;
        updateFluids();
    }

    public void sendDataLazily() {
        if (syncCooldown > 0) {
            queuedSync = true;
            return;
        }
        updateFluids();
        queuedSync = false;
        syncCooldown = SYNC_RATE;
    }

    protected void updateFluids() {
        fluidUpdateCallback.run();
        blockEntity.sendData();
        blockEntity.setChanged();
    }

    @Override
    public void unload() {
        super.unload();
        blockEntity.getLevel().invalidateCapabilities(getPos());
    }

    public ResizeableTank getInternalTank(){
        return tank;
    }

    public boolean isEmpty(){
        return tank.tank.isEmpty();
    }

    public void forTankDo(Consumer<ResizeableTank> action){
        action.accept(tank);
    }

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        if(!vat.isController()) return; // do not safe content for children block
        super.write(nbt, registries, clientPacket);
        nbt.put(getType().getName() + "Tank", tank.writeNBT(registries));
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        if(!vat.isController()) return; // do not safe content for children block
        super.read(nbt, registries, clientPacket);
        String key = getType().getName() + "Tank";
        if(nbt.contains(key, Tag.TAG_COMPOUND)){
            tank.readNBT(nbt.getCompound(key), registries, clientPacket);
        }
    }

    public void ensureLegalSpace(){
        if(tank.tank.getSpace() < 0) tank.tank.drain(-tank.tank.getSpace(), FluidAction.EXECUTE);
    }

    public void setCapacity(int capacity){
        tank.tank.setCapacity(capacity);
        if(tank.tank.getSpace() < 0){
            tank.tank.drain(-tank.tank.getSpace(), IFluidHandler.FluidAction.EXECUTE);
        }
    }

    @Override
    public int getTanks() {
        return tank.tank.getTanks();
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return this.tank.tank.getFluidInTank(tank);
    }

    @Override
    public int getTankCapacity(int tank) {
        return this.tank.tank.getTankCapacity(tank);
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return this.tank.tank.isFluidValid(tank, stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if(!insertionAllowed) return 0;
        return this.tank.tank.fill(resource, action);
    }

    public int forceFill(FluidStack resource, FluidAction action){
        return this.tank.tank.fill(resource, action);
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if(!extractionAllowed) return FluidStack.EMPTY;
        return this.tank.tank.drain(resource, action);
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        if(!extractionAllowed) return FluidStack.EMPTY;
        return this.tank.tank.drain(maxDrain, action);
    }

    public class ResizeableTank {
        protected SmartFluidTank tank;
        protected LerpedFloat fluidLevel;
        protected FluidStack renderedFluid;

        public ResizeableTank(int capacity){
            tank = new SmartFluidTank(capacity, f -> onFluidStackChanged());
            fluidLevel = LerpedFloat.linear().startWithValue(0).chase(0, .25, LerpedFloat.Chaser.EXP);
            renderedFluid = FluidStack.EMPTY;
        }

        public void onFluidStackChanged(){
            if (!blockEntity.hasLevel())
                return;
            fluidLevel.chase(tank.getFluidAmount() / (float) tank.getCapacity(), .25, LerpedFloat.Chaser.EXP);
            if (!getWorld().isClientSide)
                sendDataLazily();
            if (blockEntity.isVirtual() && !tank.getFluid()
                    .isEmpty())
                renderedFluid = tank.getFluid();
        }

        public FluidStack getRenderedFluid() {
            return renderedFluid;
        }

        public LerpedFloat getFluidLevel() {
            return fluidLevel;
        }

        public float getTotalUnits(float partialTicks){
            return fluidLevel.getValue(partialTicks) * tank.getCapacity();
        }

        public CompoundTag writeNBT(HolderLookup.Provider registries){
            CompoundTag compound = new CompoundTag();
            compound.put("TankContent", tank.writeToNBT(registries, new CompoundTag()));
            compound.put("Level", fluidLevel.writeNBT());
            return compound;
        }

        public void readNBT(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
            tank.readFromNBT(registries, compound.getCompound("TankContent"));
            fluidLevel.readNBT(compound.getCompound("Level"), clientPacket);
            if (!tank.getFluid()
                    .isEmpty())
                renderedFluid = tank.getFluid();
        }

        public boolean isEmpty(float partialTicks) {
            FluidStack renderedFluid = getRenderedFluid();
            if (renderedFluid.isEmpty())
                return true;
            float units = getTotalUnits(partialTicks);
            return units < 1;
        }
    }
}
