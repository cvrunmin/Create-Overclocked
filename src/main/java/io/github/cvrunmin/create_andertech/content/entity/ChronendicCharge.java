package io.github.cvrunmin.create_andertech.content.entity;

import io.github.cvrunmin.create_andertech.index.AllEntityTypes;
import io.github.cvrunmin.create_andertech.index.AllItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ChronendicCharge extends Projectile implements ItemSupplier {

    private int lifeTick;

    public ChronendicCharge(EntityType<ChronendicCharge> entityType, Level level) {
        super(entityType, level);
        lifeTick = 0;
        noPhysics = true;
    }

    public ChronendicCharge(Level level, LivingEntity shooter){
        this(AllEntityTypes.CHRONENDIC_CHARGE.get(), level);
        this.setOwner(shooter);
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        lifeTick = compound.getInt("LifeTick");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("LifeTick", lifeTick);
    }

    @Override
    protected double getDefaultGravity() {
        if(lifeTick < 40)
            return 0.01;
        return 0.04;
    }

    @Override
    public void tick() {
        super.tick();
        if(!level().isClientSide){
            if(lifeTick == 0 || lifeTick >= 40) {
                applyGravity();
            }
            lifeTick++;

            HitResult hitresult = level().clip(new ClipContext(position(), position().add(0, -0.1, 0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            if (hitresult.getType() != HitResult.Type.MISS) {
                if (!net.neoforged.neoforge.event.EventHooks.onProjectileImpact(this, hitresult)) {
                    this.hitTargetOrDeflectSelf(hitresult);
                }
            }else{
                var aabb = this.getBoundingBox().inflate(0.25);
                var hit = false;
                for (Entity entity : this.level().getEntities(this, aabb)) {
                    if(canHitEntity(entity)){
                        hit = true;
                        onHitEntity(new EntityHitResult(entity));
                    }
                }
                if(hit){
                    this.level().gameEvent(GameEvent.PROJECTILE_LAND, position(), GameEvent.Context.of(this, null));
                    this.discard();
                }
            }
        }
        this.checkInsideBlocks();
        Vec3 deltaMovement = this.getDeltaMovement();
        this.setPos(this.getX() + deltaMovement.x, this.getY() + deltaMovement.y, this.getZ() + deltaMovement.z);
        ProjectileUtil.rotateTowardsMovement(this, 0.2F);
    }

    @Override
    public float getLightLevelDependentMagicValue() {
        return 1.0f;
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        return super.canHitEntity(target) && !target.noPhysics;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity entity = result.getEntity();
        Entity entity1 = this.getOwner();
        LivingEntity livingentity = entity1 instanceof LivingEntity ? (LivingEntity)entity1 : null;
        DamageSource damagesource = this.damageSources().mobProjectile(this, livingentity);
        boolean flag = entity.hurt(damagesource, 4.0F);
        if (flag) {
            if (this.level() instanceof ServerLevel serverlevel) {
                EnchantmentHelper.doPostAttackEffects(serverlevel, entity, damagesource);
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        ((ServerLevel)this.level()).sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 2, 0.2, 0.2, 0.2, 0.0);
        this.playSound(SoundEvents.SHULKER_BULLET_HIT, 1.0F, 1.0F);
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        Vec3 vec3 = new Vec3(packet.getXa(), packet.getYa(), packet.getZa());
        this.setDeltaMovement(vec3);
    }

    @Override
    public ItemStack getItem() {
        return AllItems.CHRONENDIC_PEARL.asStack();
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            this.discard();
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }
}
