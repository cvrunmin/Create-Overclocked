package io.github.cvrunmin.create_andertech.content.kinetics.press;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.Create;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.kinetics.press.PressingBehaviour;
import com.simibubi.create.content.processing.basin.BasinBlock;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour.ProcessingResult.HOLD;
import static com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour.ProcessingResult.PASS;

public class OCPressingBehaviour extends BeltProcessingBehaviour {

    public static final int CYCLE = 240;
    public static final int ENTITY_SCAN = 10;

    public List<ItemStack> particleItems = new ArrayList<>();

    public PressingBehaviour.PressingBehaviourSpecifics specifics;
    public int prevRunningTicks;
    public int runningTicks;
    public int pendingAction;
    public boolean running;
    public boolean finished;
    public PressingBehaviour.Mode mode;

    int entityScanCooldown;
    public <T extends SmartBlockEntity & PressingBehaviour.PressingBehaviourSpecifics> OCPressingBehaviour(T be) {
        super(be);
        this.specifics = be;
        mode = PressingBehaviour.Mode.WORLD;
        entityScanCooldown = ENTITY_SCAN;
        whenItemEnters(this::onItemReceived);
        whileItemHeld(this::whenItemHeld);
    }


    @Override
    public void read(CompoundTag compound, boolean clientPacket) {
        running = compound.getBoolean("Running");
        mode = PressingBehaviour.Mode.values()[compound.getInt("Mode")];
        finished = compound.getBoolean("Finished");
        prevRunningTicks = runningTicks = compound.getInt("Ticks");
        pendingAction = compound.getInt("Actions");
        super.read(compound, clientPacket);

        if (clientPacket) {
            NBTHelper.iterateCompoundList(compound.getList("ParticleItems", Tag.TAG_COMPOUND),
                    c -> particleItems.add(ItemStack.of(c)));
            spawnParticles();
        }
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        compound.putBoolean("Running", running);
        compound.putInt("Mode", mode.ordinal());
        compound.putBoolean("Finished", finished);
        compound.putInt("Ticks", runningTicks);
        compound.putInt("Actions", pendingAction);
        super.write(compound, clientPacket);

        if (clientPacket) {
            compound.put("ParticleItems", NBTHelper.writeCompoundList(particleItems, ItemStack::serializeNBT));
            particleItems.clear();
        }
    }

    public float getRenderedHeadOffset(float partialTicks) {
        if (!running)
            return 0;
        int runningTicks = Math.abs(this.runningTicks);
        float ticks = Mth.lerp(partialTicks, prevRunningTicks, runningTicks);
        if (runningTicks < (CYCLE * 2) / 3)
            return (float) Mth.clamp(Math.pow(ticks / CYCLE * 2, 3), 0, 1);
        return Mth.clamp((CYCLE - ticks) / CYCLE * 3, 0, 1);
    }

    public void start(PressingBehaviour.Mode mode) {
        this.mode = mode;
        running = true;
        prevRunningTicks = 0;
        runningTicks = 0;
        pendingAction = 0;
        particleItems.clear();
        blockEntity.sendData();
    }

    public boolean inWorld() {
        return mode == PressingBehaviour.Mode.WORLD;
    }

    public boolean onBasin() {
        return mode == PressingBehaviour.Mode.BASIN;
    }

    @Override
    public void tick() {
        /**
         * Steps:
         * 1. when not running, check if anything to process (10 ticks), if yes, set as running
         * 1.5. (Client only) if runningTick is neg half cycle, return.
         * 2. if runningTicks is half of cycle, apply recipe
         * 3. if runningTicks leave the cycle, set this press as not running and end this cycle
         * 4. tick runningTicks
         * 5. if runningTicks was not half of cycle but now pass half cycle, set it as half cycle
         **/
        super.tick();

        Level level = getWorld();
        BlockPos worldPosition = getPos();

        if (!running || level == null) {
            if (level != null && !level.isClientSide) {

                if (specifics.getKineticSpeed() == 0)
                    return;
                if (entityScanCooldown > 0)
                    entityScanCooldown--;
                if (entityScanCooldown <= 0) {
                    entityScanCooldown = ENTITY_SCAN;

                    if (BlockEntityBehaviour.get(level, worldPosition.below(2),
                            TransportedItemStackHandlerBehaviour.TYPE) != null)
                        return;
                    if (BasinBlock.isBasin(level, worldPosition.below(2)))
                        return;

                    for (ItemEntity itemEntity : level.getEntitiesOfClass(ItemEntity.class,
                            new AABB(worldPosition.below()).deflate(.125f))) {
                        if (!itemEntity.isAlive() || !itemEntity.onGround())
                            continue;
                        if (!specifics.tryProcessInWorld(itemEntity, true))
                            continue;
                        start(PressingBehaviour.Mode.WORLD);
                        return;
                    }
                }

            }
            return;
        }


        if (level.isClientSide && runningTicks == -CYCLE / 2) {
            prevRunningTicks = CYCLE / 2;
            return;
        }

        prevRunningTicks = runningTicks;
        runningTicks += getRunningTickSpeed();
        if (prevRunningTicks < CYCLE / 2 && runningTicks >= CYCLE / 2) {
//            runningTicks = CYCLE / 2;
            pendingAction = 1;
            // Pause the ticks until a packet is received
//            if (level.isClientSide && !blockEntity.isVirtual())
//                runningTicks = -(CYCLE / 2);
        }

        if (prevRunningTicks < CYCLE / 2 && runningTicks >= CYCLE / 2 && specifics.getKineticSpeed() != 0) {
            if (inWorld())
                applyInWorld();
            if (onBasin())
                applyOnBasin();

            if (level.getBlockState(worldPosition.below(2))
                    .getSoundType() == SoundType.WOOL)
                AllSoundEvents.MECHANICAL_PRESS_ACTIVATION_ON_BELT.playOnServer(level, worldPosition);
            else
                AllSoundEvents.MECHANICAL_PRESS_ACTIVATION.playOnServer(level, worldPosition, .5f,
                        .75f + (Math.abs(specifics.getKineticSpeed()) / 1024f));

            if (!level.isClientSide)
                blockEntity.sendData();
        }

        if (!level.isClientSide && runningTicks > CYCLE) {
            finished = true;
            running = false;
            particleItems.clear();
            specifics.onPressingCompleted();
            blockEntity.sendData();
            return;
        }

    }

    protected void applyOnBasin() {
        Level level = getWorld();
        if (level.isClientSide)
            return;
        particleItems.clear();
        if (specifics.tryProcessInBasin(false)) {
            pendingAction--;
            blockEntity.sendData();
        }
    }

    protected void applyInWorld() {
        Level level = getWorld();
        BlockPos worldPosition = getPos();
        AABB bb = new AABB(worldPosition.below(1));
        boolean bulk = specifics.canProcessInBulk();

        particleItems.clear();

        if (level.isClientSide)
            return;

        for (Entity entity : level.getEntities(null, bb)) {
            if (!(entity instanceof ItemEntity itemEntity))
                continue;
            if (!entity.isAlive() || !entity.onGround())
                continue;

            entityScanCooldown = 0;
            if (specifics.tryProcessInWorld(itemEntity, false)) {
                pendingAction--;
                blockEntity.sendData();
            }
            if (!bulk)
                break;
        }
    }

    public int getRunningTickSpeed() {
        float speed = specifics.getKineticSpeed();
        if (speed == 0)
            return 0;
        return (int) Mth.lerp(Math.max(Math.abs(speed) / 512f, 0), 1, 60);
    }

    protected void spawnParticles() {
        if (particleItems.isEmpty())
            return;

        BlockPos worldPosition = getPos();

        if (mode == PressingBehaviour.Mode.BASIN)
            particleItems
                    .forEach(stack -> makeCompactingParticleEffect(VecHelper.getCenterOf(worldPosition.below(2)), stack));
        if (mode == PressingBehaviour.Mode.BELT)
            particleItems.forEach(stack -> makePressingParticleEffect(VecHelper.getCenterOf(worldPosition.below(2))
                    .add(0, 8 / 16f, 0), stack));
        if (mode == PressingBehaviour.Mode.WORLD)
            particleItems.forEach(stack -> makePressingParticleEffect(VecHelper.getCenterOf(worldPosition.below(1))
                    .add(0, -1 / 4f, 0), stack));

        particleItems.clear();
    }

    public void makePressingParticleEffect(Vec3 pos, ItemStack stack) {
        makePressingParticleEffect(pos, stack, specifics.getParticleAmount());
    }

    public void makePressingParticleEffect(Vec3 pos, ItemStack stack, int amount) {
        Level level = getWorld();
        if (level == null || !level.isClientSide)
            return;
        for (int i = 0; i < amount; i++) {
            Vec3 motion = VecHelper.offsetRandomly(Vec3.ZERO, level.random, .125f)
                    .multiply(1, 0, 1);
            motion = motion.add(0, amount != 1 ? 0.125f : 1 / 16f, 0);
            level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, stack), pos.x, pos.y - .25f, pos.z, motion.x,
                    motion.y, motion.z);
        }
    }

    public void makeCompactingParticleEffect(Vec3 pos, ItemStack stack) {
        Level level = getWorld();
        if (level == null || !level.isClientSide)
            return;
        for (int i = 0; i < 20; i++) {
            Vec3 motion = VecHelper.offsetRandomly(Vec3.ZERO, level.random, .175f)
                    .multiply(1, 0, 1);
            level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, stack), pos.x, pos.y, pos.z, motion.x,
                    motion.y + .25f, motion.z);
        }
    }

    public ProcessingResult onItemReceived(TransportedItemStack stack, TransportedItemStackHandlerBehaviour handler){
        if(this.specifics.getKineticSpeed() == 0) return ProcessingResult.PASS;
        if(this.running) return ProcessingResult.HOLD;
        if(!this.specifics.tryProcessOnBelt(stack, null, true)) return ProcessingResult.PASS;
        this.start(PressingBehaviour.Mode.BELT);
        return ProcessingResult.HOLD;
    }


    public ProcessingResult whenItemHeld(TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler) {

        if (this.specifics.getKineticSpeed() == 0)
            return PASS;
        if (!this.running && this.pendingAction <= 0)
            return PASS;
        if (this.pendingAction <= 0)
            return HOLD;

        this.particleItems.clear();
        ArrayList<ItemStack> results = new ArrayList<>();
        if (!this.specifics.tryProcessOnBelt(transported, results, false))
            return PASS;

        boolean bulk = this.specifics.canProcessInBulk() || transported.stack.getCount() == 1;

        transported.clearFanProcessingData();

        List<TransportedItemStack> collect = results.stream()
                .map(stack -> {
                    TransportedItemStack copy = transported.copy();
                    boolean centered = BeltHelper.isItemUpright(stack);
                    copy.stack = stack;
                    copy.locked = true;
                    copy.angle = centered ? 180 : Create.RANDOM.nextInt(360);
                    return copy;
                })
                .collect(Collectors.toList());

        if (bulk) {
            if (collect.isEmpty())
                handler.handleProcessingOnItem(transported, TransportedItemStackHandlerBehaviour.TransportedResult.removeItem());
            else
                handler.handleProcessingOnItem(transported, TransportedItemStackHandlerBehaviour.TransportedResult.convertTo(collect));
            pendingAction = 0;
        } else {
            TransportedItemStack left = transported.copy();
            left.stack.shrink(1);

            if (collect.isEmpty())
                handler.handleProcessingOnItem(transported, TransportedItemStackHandlerBehaviour.TransportedResult.convertTo(left));
            else
                handler.handleProcessingOnItem(transported, TransportedItemStackHandlerBehaviour.TransportedResult.convertToAndLeaveHeld(collect, left));
            pendingAction--;
        }

        this.blockEntity.sendData();
        return HOLD;
    }
}
