package io.github.cvrunmin.create_andertech.content.entity;

import io.github.cvrunmin.create_andertech.content.decoration.particles.CylinderMotionParticleOptions;
import io.github.cvrunmin.create_andertech.index.AllEntityTypes;
import io.github.cvrunmin.create_andertech.index.CAAllParticleTypes;
import io.github.cvrunmin.create_andertech.index.CAAllSoundEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.EnumSet;

public class ChronendicBlaze extends Monster {
    public Quaternionf coreRot;
    public Vector3f coreRotV = new Vector3f();
    public Vector3f coreRotA = new Vector3f();
    private int remainingCoreRotAUpdateTick = 0;
    private int remainingEnchantGlyphTick = 0;

    private static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(ChronendicBlaze.class, EntityDataSerializers.BYTE);

    public ChronendicBlaze(EntityType<ChronendicBlaze> entityType, Level level) {
        super(entityType, level);
        this.setPathfindingMalus(PathType.WATER, -4.0F);
        this.setPathfindingMalus(PathType.LAVA, 0.0F);
        this.setPathfindingMalus(PathType.DANGER_FIRE, 0.0F);
        this.setPathfindingMalus(PathType.DAMAGE_FIRE, 0.0F);
        this.xpReward = 12;
        this.coreRot = new Quaternionf().rotateZYX((float) Math.toRadians(-45), 0, 0);
    }

    public ChronendicBlaze(Level level, double x, double y, double z){
        this(AllEntityTypes.CHRONENDIC_BLAZE.get(), level);
        this.setPos(x, y, z);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(4, new ChronendicBlazeAttackGoal(this));
        this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0, 0.0F));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 25.0f).add(Attributes.ATTACK_DAMAGE, 8.0).add(Attributes.MOVEMENT_SPEED, 0.27F).add(Attributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_FLAGS_ID, (byte)0);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return CAAllSoundEvents.CHRONENDIC_BLAZE_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return CAAllSoundEvents.CHRONENDIC_BLAZE_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return CAAllSoundEvents.CHRONENDIC_BLAZE_DEATH.get();
    }

    @Override
    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    @Override
    public void aiStep() {
        if (!this.onGround() && this.getDeltaMovement().y < 0.0) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.6, 1.0));
        }

        if (this.level().isClientSide) {
            if (this.random.nextInt(24) == 0 && !this.isSilent()) {
                this.level()
                        .playLocalSound(
                                this.getX() + 0.5,
                                this.getY() + 0.5,
                                this.getZ() + 0.5,
                                SoundEvents.BLAZE_BURN,
                                this.getSoundSource(),
                                1.0F + this.random.nextFloat(),
                                this.random.nextFloat() * 0.7F + 0.3F,
                                false
                        );
            }

            for (int i = 0; i < 2; i++) {
                this.level().addParticle(ParticleTypes.SMOKE, this.getRandomX(0.5), this.getRandomY(), this.getRandomZ(0.5), 0.0, 0.0, 0.0);
                this.level()
                        .addParticle(
                                ParticleTypes.PORTAL,
                                this.getRandomX(0.5),
                                this.getRandomY() - 0.25,
                                this.getRandomZ(0.5),
                                (this.random.nextDouble() - 0.5) * 2.0,
                                -this.random.nextDouble(),
                                (this.random.nextDouble() - 0.5) * 2.0
                        );
            }
        }

        super.aiStep();
    }

    @Override
    public boolean isOnFire() {
        return isCharged();
    }

    private boolean isCharged() {
        return (this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
    }


    void setCharged(boolean charged) {
        byte b0 = this.entityData.get(DATA_FLAGS_ID);
        if (charged) {
            b0 = (byte)(b0 | 1);
        } else {
            b0 = (byte)(b0 & -2);
        }

        this.entityData.set(DATA_FLAGS_ID, b0);
    }

    @Override
    public void tick() {
        super.tick();
        if(level().isClientSide){
            if(--remainingCoreRotAUpdateTick <= 0){
                var rand = level().random.fork();
                coreRotA = new Vector3f((float) rand.triangle(0., 5.), (float) rand.triangle(0., 5.), (float) rand.triangle(0., 5.));
                remainingCoreRotAUpdateTick = 10;
            }
            coreRotV = coreRotV.add(coreRotA.mul(0.05f, new Vector3f()));
            float vLen = coreRotV.length();
            if(vLen > 1e-3f){
                var axis = coreRotV.normalize(new Vector3f());
                var dQ = new Quaternionf().fromAxisAngleRad(axis, vLen);
                coreRot.add(dQ).normalize();
            }
            if(remainingEnchantGlyphTick > 0){
                remainingEnchantGlyphTick--;
            }else{
                remainingEnchantGlyphTick = 0;
                if(isCharged()){
                    this.level().addParticle(new CylinderMotionParticleOptions(
                            CAAllParticleTypes.CYLINDER_MOTION_ENCHANT_GLYPH_EMITTER.get(),
                            (this.getBbWidth()),
                            3,
                            0,
                            2f,
                            10,
                            10),
                            this.getX(), this.getY(), this.getZ(), 0, 0, 0);
                    this.level().addParticle(new CylinderMotionParticleOptions(
                            CAAllParticleTypes.CYLINDER_MOTION_ENCHANT_GLYPH_EMITTER.get(),
                            (this.getBbWidth()),
                            -3,
                            0,
                            -2f,
                            10,
                            10),
                            this.getX(), this.getY(1.0), this.getZ(), 0, 0, 0);
                    remainingEnchantGlyphTick = 10;
                }
            }
        }
    }

    @Override
    public boolean isSensitiveToWater() {
        return true;
    }

    static class ChronendicBlazeAttackGoal extends Goal {
        private final ChronendicBlaze blaze;
        private int attackStep;
        private int attackTime;
        private int lastSeen;
        private Vec3[] cachedBallPos;

        public ChronendicBlazeAttackGoal(ChronendicBlaze blaze){
            this.blaze = blaze;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity livingentity = this.blaze.getTarget();
            return livingentity != null && livingentity.isAlive() && this.blaze.canAttack(livingentity);
        }

        @Override
        public void start() {
            this.attackStep = 0;
        }

        @Override
        public void stop() {
            this.blaze.setCharged(false);
            this.lastSeen = 0;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
        @Override
        public void tick() {
            this.attackTime--;
            LivingEntity livingentity = this.blaze.getTarget();
            if (livingentity != null) {
                boolean flag = this.blaze.getSensing().hasLineOfSight(livingentity);
                if (flag) {
                    this.lastSeen = 0;
                } else {
                    this.lastSeen++;
                }

                double d0 = this.blaze.distanceToSqr(livingentity);
                if (d0 < 4.0) {
                    if (!flag) {
                        return;
                    }

                    if (this.attackTime <= 0) {
                        this.attackTime = 20;
                        this.blaze.doHurtTarget(livingentity);
                    }

                    this.blaze.getMoveControl().setWantedPosition(livingentity.getX(), livingentity.getY(), livingentity.getZ(), 1.0);
                } else if (d0 < this.getFollowDistance() * this.getFollowDistance() && flag) {
                    if (this.attackTime <= 0) {
                        this.attackStep++;
                        if (this.attackStep == 1) {
                            this.attackTime = 30;
                            this.blaze.setCharged(true);
                        } else if (this.attackStep <= 6) {
                            if(attackStep % 2 == 0){
                                this.attackTime = 40;
                            }else {
                                this.attackTime = 30;
                            }
                        } else {
                            this.attackTime = 20;
                            this.attackStep = 0;
                            this.blaze.setCharged(false);
                        }

                        if(this.attackStep % 2 == 1){
                            cachedBallPos = new Vec3[3];
                            var targetPos = new Vec3(livingentity.getX(), livingentity.getY(0.5), livingentity.getZ());
                            double baseDist = this.blaze.position().distanceTo(livingentity.position()) * 0.5;
                            for (int i = 0; i < 3; i++) {
                                var dist = (1 + (blaze.random.nextFloat() * 2 - 0.5)) * baseDist;
                                var angle = blaze.random.nextDouble() * 2 * Math.PI;
                                var offsetVec = new Vec3(dist, 0, 0).yRot((float) angle);
                                Vec3 vec3 = targetPos.add(offsetVec);
                                cachedBallPos[i] = vec3;
                                Level level = this.blaze.level();
                                if(this.blaze.level() instanceof ServerLevel serverLevel){
                                    serverLevel.sendParticles(new CylinderMotionParticleOptions(
                                                    CAAllParticleTypes.CYLINDER_MOTION_ENCHANT_GLYPH_EMITTER.get(),
                                                    0.25f,
                                                    6,
                                                    0,
                                                    2f,
                                                    10,
                                                    60),
                                            vec3.x, vec3.y, vec3.z, 0, 0, 0, 0, 0);
                                    serverLevel.sendParticles(new CylinderMotionParticleOptions(
                                                    CAAllParticleTypes.CYLINDER_MOTION_ENCHANT_GLYPH_EMITTER.get(),
                                                    0.25f,
                                                    -6,
                                                    0,
                                                    -2f,
                                                    10,
                                                    60),
                                            vec3.x, vec3.y, vec3.z, 0, 0, 0, 0, 0);
                                }
                            }
                        }else if(this.attackStep > 1 && cachedBallPos != null){
                            for (int i = 0; i < 3; i++) {
                                var fireball = new ChronendicCharge(this.blaze.level(), this.blaze);
                                fireball.setPos(cachedBallPos[i]);
                                this.blaze.level().addFreshEntity(fireball);
                            }
                        }
                    }

                    this.blaze.getLookControl().setLookAt(livingentity, 10.0F, 10.0F);
                } else if (this.lastSeen < 5) {
                    this.blaze.getMoveControl().setWantedPosition(livingentity.getX(), livingentity.getY(), livingentity.getZ(), 1.0);
                }

                super.tick();
            }
        }

        private double getFollowDistance() {
            return this.blaze.getAttributeValue(Attributes.FOLLOW_RANGE);
        }
    }
}
