package io.github.cvrunmin.create_andertech.content.misc;

import com.google.common.collect.Sets;
import io.github.cvrunmin.create_andertech.content.decoration.RichNyliumBlock;
import io.github.cvrunmin.create_andertech.index.AllBlocks;
import io.github.cvrunmin.create_andertech.index.AllEntityTypes;
import io.github.cvrunmin.create_andertech.index.AllTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

public class NyliumTransmuterEntity extends Entity implements TraceableEntity {
    private static final EntityDataAccessor<Integer> DATA_FUSE_ID = SynchedEntityData.defineId(NyliumTransmuterEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_RADIUS_ID = SynchedEntityData.defineId(NyliumTransmuterEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<BlockState> DATA_BLOCK_STATE_ID = SynchedEntityData.defineId(NyliumTransmuterEntity.class, EntityDataSerializers.BLOCK_STATE);

    @Nullable
    private LivingEntity owner;
    private boolean usedPortal;

    public NyliumTransmuterEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.blocksBuilding = true;
    }

    public NyliumTransmuterEntity(Level level, double x, double y, double z, BlockState state, @Nullable LivingEntity owner){
        this(AllEntityTypes.NYLIUM_TRANSMUTER.get(), level);
        this.setPos(x, y, z);
        this.setFuse(80);
        this.setRadius(2);
        this.setBlockState(state);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.owner = owner;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_FUSE_ID, 80);
        builder.define(DATA_RADIUS_ID, 2);
        builder.define(DATA_BLOCK_STATE_ID, AllBlocks.WRAPPED_NYLIUM_TRANSMUTER.getDefaultState());
    }

    @Override
    protected MovementEmission getMovementEmission() {
        return MovementEmission.NONE;
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    protected double getDefaultGravity() {
        return 0.04;
    }

    @Override
    public void tick() {
        this.handlePortal();
        this.applyGravity();
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(0.98));
        if (this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.7, -0.5, 0.7));
        }

        int i = this.getFuse() - 1;
        this.setFuse(i);
        if (i <= 0) {
            this.discard();
            if (!this.level().isClientSide) {
                this.explode();
            }
            this.afterExplosion();
        } else {
            this.updateInWaterStateAndDoFluidPushing();
            if (this.level().isClientSide) {
                this.level().addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5, this.getZ(), 0.0, 0.0, 0.0);
            }
        }
    }

    @Nullable
    protected Function<BlockState, BlockState> getTransmuteMapper(){
        if(this.getBlockState().is(AllBlocks.WRAPPED_NYLIUM_TRANSMUTER)){
            return src -> {
                if(src.is(AllBlocks.RICH_CRIMSON_NYLIUM)) return AllBlocks.POOR_CRIMSON_NYLIUM.getDefaultState();
                if(src.is(Blocks.CRIMSON_NYLIUM)) return AllBlocks.POOR_CRIMSON_NYLIUM.getDefaultState();
                if(src.is(AllBlocks.UNTOPPED_CRIMSON_NYLIUM)) return Blocks.NETHERRACK.defaultBlockState();
                if(src.is(AllBlocks.POOR_CRIMSON_NYLIUM)) return Blocks.NETHERRACK.defaultBlockState();
                if(src.is(Blocks.NETHERRACK)) return AllBlocks.POOR_WARPED_NYLIUM.getDefaultState();
                if(src.is(AllBlocks.POOR_WARPED_NYLIUM)) return AllBlocks.RICH_WARPED_NYLIUM.getDefaultState();
                if(src.is(AllBlocks.UNTOPPED_WARPED_NYLIUM)) return AllBlocks.RICH_WARPED_NYLIUM.getDefaultState();
                if(src.is(Blocks.WARPED_NYLIUM)) return AllBlocks.RICH_WARPED_NYLIUM.getDefaultState();
                return src;
            };
        }
        if(this.getBlockState().is(AllBlocks.CRIMSON_NYLIUM_TRANSMUTER)){
            return src -> {
                if(src.is(AllBlocks.RICH_WARPED_NYLIUM)) return AllBlocks.POOR_WARPED_NYLIUM.getDefaultState();
                if(src.is(Blocks.WARPED_NYLIUM)) return AllBlocks.POOR_WARPED_NYLIUM.getDefaultState();
                if(src.is(AllBlocks.UNTOPPED_WARPED_NYLIUM)) return Blocks.NETHERRACK.defaultBlockState();
                if(src.is(AllBlocks.POOR_WARPED_NYLIUM)) return Blocks.NETHERRACK.defaultBlockState();
                if(src.is(Blocks.NETHERRACK)) return AllBlocks.POOR_CRIMSON_NYLIUM.getDefaultState();
                if(src.is(AllBlocks.POOR_CRIMSON_NYLIUM)) return AllBlocks.RICH_CRIMSON_NYLIUM.getDefaultState();
                if(src.is(AllBlocks.UNTOPPED_CRIMSON_NYLIUM)) return AllBlocks.RICH_CRIMSON_NYLIUM.getDefaultState();
                if(src.is(Blocks.CRIMSON_NYLIUM)) return AllBlocks.RICH_CRIMSON_NYLIUM.getDefaultState();
                return src;
            };
        }
        return null;
    }

    protected void explode() {
        this.level().gameEvent(this, GameEvent.EXPLODE, position());
        var pseudoExplosionInst = new Explosion(level(), this, getX(), getY(), getZ(), getRadius(), false, Explosion.BlockInteraction.DESTROY);
        var transmuterFunc = getTransmuteMapper();
        if (transmuterFunc != null && this.getRadius() >= 1) {
            var walkQueue = new ArrayDeque<ExplosionWalkInfo>();
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    for (int k = -1; k <= 1; k++) {
                        if (i == 0 && j == 0 && k == 0) continue;
                        var dirs = new HashSet<Direction>();
                        var dir = Direction.fromDelta(i, 0, 0);
                        if (dir != null) dirs.add(dir);
                        dir = Direction.fromDelta(0, j, 0);
                        if (dir != null) dirs.add(dir);
                        dir = Direction.fromDelta(0, 0, k);
                        if (dir != null) dirs.add(dir);
                        var pos = this.blockPosition().offset(i, j, k);
                        walkQueue.add(new ExplosionWalkInfo(dirs, pos, this.getRadius() - 1));
                    }
                }
            }
            while (!walkQueue.isEmpty()) {
                var info = walkQueue.pop();
                BlockState blockState = this.level().getBlockState(info.pos);
                var shouldPropagate = blockState.isAir();
                if (blockState.is(AllTags.EXPLOSIVE_TO_TRANSMUTER)){
                    shouldPropagate = true;
                    blockState.onExplosionHit(level(), info.pos, pseudoExplosionInst, (stack1, pos1) -> {});
                }
                if (shouldPropagate) {
                    if (info.age == 0) continue;
                    for (Set<Direction> directionSet : Sets.powerSet(info.propagateDirections)) {
                        if (directionSet.isEmpty()) continue;
                        var pos = info.pos.offset(directionSet.stream().map(Direction::getNormal).reduce(Vec3i.ZERO, Vec3i::offset));
                        walkQueue.add(new ExplosionWalkInfo(directionSet, pos, info.age - 1));
                    }
                    continue;
                }
                if (RichNyliumBlock.canBeNylium(blockState, level(), info.pos)) {
                    var transmutedState = transmuterFunc.apply(blockState);
                    if (!blockState.equals(transmutedState)) {
                        level().setBlockAndUpdate(info.pos, transmutedState);
                    }
                }
            }
        }
    }
    protected void afterExplosion(){
        var pseudoExplosionInst = new Explosion(level(), this, getX(), getY(), getZ(), getRadius(), false, Explosion.BlockInteraction.TRIGGER_BLOCK);
        pseudoExplosionInst.finalizeExplosion(true);
        if(this.level() instanceof ServerLevel){
            for (ServerPlayer player : (((ServerLevel) this.level())).players()) {
                if (player.distanceToSqr(getX(), getY(), getZ()) < 4096.0){
                    player.connection.send(new ClientboundExplodePacket(getX(),
                            getY(),
                            getZ(),
                            getRadius(),
                            new ArrayList<>(),
                            Vec3.ZERO,
                            Explosion.BlockInteraction.TRIGGER_BLOCK,
                            ParticleTypes.EXPLOSION,
                            ParticleTypes.EXPLOSION_EMITTER,
                            SoundEvents.GENERIC_EXPLODE
                            ));
                }
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        compound.putShort("fuse", (short)this.getFuse());
        compound.putShort("radius", (short)this.getRadius());
        compound.put("block_state", NbtUtils.writeBlockState(this.getBlockState()));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        this.setFuse(compound.getShort("fuse"));
        this.setRadius(compound.getShort("radius"));
        if (compound.contains("block_state", 10)) {
            this.setBlockState(NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), compound.getCompound("block_state")));
        }
    }

    @Nullable
    public LivingEntity getOwner() {
        return this.owner;
    }

    @Override
    public void restoreFrom(Entity entity) {
        super.restoreFrom(entity);
        if(entity instanceof NyliumTransmuterEntity transmuter){
            this.owner = transmuter.owner;
        }
    }

    public void setFuse(int life) {
        this.entityData.set(DATA_FUSE_ID, life);
    }

    public int getFuse() {
        return this.entityData.get(DATA_FUSE_ID);
    }

    public void setRadius(int radius) {
        this.entityData.set(DATA_RADIUS_ID, radius);
    }

    public int getRadius() {
        return this.entityData.get(DATA_RADIUS_ID);
    }

    public void setBlockState(BlockState blockState) {
        this.entityData.set(DATA_BLOCK_STATE_ID, blockState);
    }

    public BlockState getBlockState() {
        return this.entityData.get(DATA_BLOCK_STATE_ID);
    }

    private void setUsedPortal(boolean usedPortal) {
        this.usedPortal = usedPortal;
    }

    @Override
    public Entity changeDimension(DimensionTransition transition) {
        Entity entity = super.changeDimension(transition);
        if(entity instanceof NyliumTransmuterEntity transmuter){
            transmuter.setUsedPortal(true);
        }
        return entity;
    }

    private record ExplosionWalkInfo(Set<Direction> propagateDirections, BlockPos pos, int age){

    }
}
