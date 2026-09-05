package io.github.cvrunmin.create_andertech.content.kinetics.spout;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.api.behaviour.spouting.BlockSpoutingBehaviour;
import com.simibubi.create.content.fluids.FluidFX;
import com.simibubi.create.content.fluids.spout.FillingBySpout;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import io.github.cvrunmin.create_andertech.CreateAndertech;
import io.github.cvrunmin.create_andertech.foundation.IOverspeedKineticBE;
import net.createmod.catnip.lang.FontHelper;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour.ProcessingResult.HOLD;
import static com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour.ProcessingResult.PASS;

public class ChronenditeMechanicalSpoutBlockEntity extends KineticBlockEntity implements IOverspeedKineticBE {
    public static final int FILLING_TIME = 200;

    protected BeltProcessingBehaviour beltProcessing;

    public int processingTime;
    public boolean sendSplash;

    private boolean createdSweetRoll, createdHoneyApple, createdChocolateBerries;

    public ChronenditeMechanicalSpoutBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        processingTime = -1;
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return super.createRenderBoundingBox().expandTowards(0, -2, 0);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);

        beltProcessing = new BeltProcessingBehaviour(this).whenItemEnters(this::onItemReceived)
                .whileItemHeld(this::whenItemHeld);
        behaviours.add(beltProcessing);

        registerAwardables(behaviours, AllAdvancements.SPOUT, AllAdvancements.FOODS);
    }

    protected BeltProcessingBehaviour.ProcessingResult onItemReceived(TransportedItemStack transported,
                                                                      TransportedItemStackHandlerBehaviour handler) {
        if (handler.blockEntity.isVirtual())
            return PASS;
        if (!FillingBySpout.canItemBeFilled(level, transported.stack))
            return PASS;
        if(!hasFluidTankReady()) return PASS;
        FluidStack fluid = getCurrentFluidInTank();
        if (fluid.isEmpty())
            return HOLD;
        if (FillingBySpout.getRequiredAmountForItem(level, transported.stack, fluid) == -1)
            return PASS;
        return HOLD;
    }

    protected BeltProcessingBehaviour.ProcessingResult whenItemHeld(TransportedItemStack transported,
                                                                    TransportedItemStackHandlerBehaviour handler) {
        if (processingTime != -1 && processingTime != 50)
            return HOLD;
        if (!FillingBySpout.canItemBeFilled(level, transported.stack))
            return PASS;
        if(!hasFluidTankReady()) return PASS;
        FluidStack origFluid = getCurrentFluidInTank();
        FluidStack fluid = origFluid.copy();
        if (fluid.isEmpty())
            return HOLD;
        int requiredAmountForItem = FillingBySpout.getRequiredAmountForItem(level, transported.stack, fluid.copy());
        if (requiredAmountForItem == -1)
            return PASS;
        if (requiredAmountForItem > fluid.getAmount())
            return HOLD;

        if (processingTime == -1) {
            processingTime = FILLING_TIME;
            notifyUpdate();
            AllSoundEvents.SPOUTING.playOnServer(level, worldPosition, 0.75f, 0.9f + 0.2f * (float) Math.random());
            return HOLD;
        }

        // Process finished
        ItemStack out = FillingBySpout.fillItem(level, requiredAmountForItem, transported.stack, fluid);
        if (!out.isEmpty()) {
            transported.clearFanProcessingData();
            List<TransportedItemStack> outList = new ArrayList<>();
            TransportedItemStack held = null;
            TransportedItemStack result = transported.copy();
            result.stack = out;
            if (!transported.stack.isEmpty())
                held = transported.copy();
            outList.add(result);
            handler.handleProcessingOnItem(transported, TransportedItemStackHandlerBehaviour.TransportedResult.convertToAndLeaveHeld(outList, held));
        }

        award(AllAdvancements.SPOUT);
        if (trackFoods()) {
            createdChocolateBerries |= AllItems.CHOCOLATE_BERRIES.isIn(out);
            createdHoneyApple |= AllItems.HONEYED_APPLE.isIn(out);
            createdSweetRoll |= AllItems.SWEET_ROLL.isIn(out);
            if (createdChocolateBerries && createdHoneyApple && createdSweetRoll)
                award(AllAdvancements.FOODS);
        }

        var delta = origFluid.getAmount() - fluid.getAmount();
        getFluidTank().drain(delta, IFluidHandler.FluidAction.EXECUTE);
        sendSplash = true;
        notifyUpdate();
        return HOLD;
    }

    @Nullable
    protected IFluidHandler getFluidTank(){
        BlockPos thisPos = getBlockPos();
        var maybeTankPos = thisPos.above();
        var maybeHandler = level.getCapability(Capabilities.FluidHandler.BLOCK, maybeTankPos, Direction.DOWN);
        if(maybeHandler != null && maybeHandler.getTanks() > 0){
            return maybeHandler;
        }
        return null;
    }

    protected FluidStack getCurrentFluidInTank(){
        var tank = getFluidTank();
        if(tank == null) return FluidStack.EMPTY;
        if(tank.getTanks() <= 0) return FluidStack.EMPTY;
        return tank.getFluidInTank(0);
    }

    public boolean hasFluidTankReady(){
        return getFluidTank() != null;
    }

    private boolean trackFoods() {
        return getBehaviour(AdvancementBehaviour.TYPE).isOwnerPresent();
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);

        compound.putInt("ProcessingTime", processingTime);
        if (sendSplash && clientPacket) {
            compound.putBoolean("Splash", true);
            sendSplash = false;
        }

        if (!trackFoods())
            return;
        if (createdChocolateBerries)
            NBTHelper.putMarker(compound, "ChocolateBerries");
        if (createdHoneyApple)
            NBTHelper.putMarker(compound, "HoneyApple");
        if (createdSweetRoll)
            NBTHelper.putMarker(compound, "SweetRoll");
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        processingTime = compound.getInt("ProcessingTime");

        createdChocolateBerries = compound.contains("ChocolateBerries");
        createdHoneyApple = compound.contains("HoneyApple");
        createdSweetRoll = compound.contains("SweetRoll");

        if (!clientPacket)
            return;
        if (compound.contains("Splash")) {
            spawnSplash(getCurrentFluidInTank());
        }
    }

    @Override
    public void tick() {
        super.tick();

        if(!hasFluidTankReady()) processingTime = -1;

        if (processingTime >= 0) {
            var lastProcessingTime = processingTime;
            processingTime = Math.max(-1, processingTime - getProcessingSpeed());
            if(lastProcessingTime > 50 && processingTime <= 50) processingTime = 50;
        }

        if (processingTime >= 80 && level.isClientSide) {
            spawnProcessingParticles(getCurrentFluidInTank());
        }
    }

    public int getProcessingSpeed(){
        var speed = Math.abs(getSpeed());
        if(speed == 0) return 0;
        return Math.clamp(Math.round(speed / 64f * 10), 1, FILLING_TIME);
    }

    protected void spawnProcessingParticles(FluidStack fluid) {
        if (isVirtual() || fluid.isEmpty())
            return;
        Vec3 vec = VecHelper.getCenterOf(worldPosition);
        vec = vec.subtract(0, 8 / 16f, 0);
        ParticleOptions particle = FluidFX.getFluidParticle(fluid);
        level.addAlwaysVisibleParticle(particle, vec.x, vec.y, vec.z, 0, -.1f, 0);
    }

    protected static int SPLASH_PARTICLE_COUNT = 20;

    protected void spawnSplash(FluidStack fluid) {
        if (isVirtual() || fluid.isEmpty())
            return;
        Vec3 vec = VecHelper.getCenterOf(worldPosition);
        vec = vec.subtract(0, 2 - 5 / 16f, 0);
        ParticleOptions particle = FluidFX.getFluidParticle(fluid);
        for (int i = 0; i < SPLASH_PARTICLE_COUNT; i++) {
            Vec3 m = VecHelper.offsetRandomly(Vec3.ZERO, level.random, 0.125f);
            m = new Vec3(m.x, Math.abs(m.y), m.z);
            level.addAlwaysVisibleParticle(particle, vec.x, vec.y, vec.z, m.x, m.y, m.z);
        }
    }

    @Override
    public boolean addToTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        var superValue = super.addToTooltip(tooltip, isPlayerSneaking);
        if(!hasFluidTankReady()){
            CreateLang.builder(CreateAndertech.MODID).translate("tooltip.noTank")
                    .style(ChatFormatting.GOLD)
                    .forGoggles(tooltip);
            MutableComponent hint = CreateLang.builder(CreateAndertech.MODID)
                    .translate("tooltip.noTank.hint", I18n.get(getBlockState().getBlock().getDescriptionId()))
                    .component();
            List<Component> cutString = TooltipHelper.cutTextComponent(hint, FontHelper.Palette.GRAY_AND_WHITE);
            for (Component component : cutString) {
                CreateLang.builder(CreateAndertech.MODID).add(component.copy()).forGoggles(tooltip);
            }
            return true;
        }
        return superValue;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        var filled = super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        filled |= containedFluidTooltip(tooltip, isPlayerSneaking, getFluidTank());
        return filled;
    }
}
