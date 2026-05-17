package io.github.cvrunmin.create_andertech.content.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ChronendicBlaze extends Monster {
    public Quaternionf coreRot;
    public Vector3f coreRotV = new Vector3f();
    public Vector3f coreRotA = new Vector3f();
    private int remainingCoreRotAUpdateTick = 0;

    public ChronendicBlaze(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.setPathfindingMalus(PathType.WATER, -4.0F);
        this.setPathfindingMalus(PathType.LAVA, 0.0F);
        this.setPathfindingMalus(PathType.DANGER_FIRE, 0.0F);
        this.setPathfindingMalus(PathType.DAMAGE_FIRE, 0.0F);
        this.xpReward = 12;
        this.coreRot = new Quaternionf().rotateZYX((float) Math.toRadians(-45), 0, 0);
    }

    @Override
    protected void registerGoals() {

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
    protected SoundEvent getAmbientSound() {
        return SoundEvents.BLAZE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.BLAZE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BLAZE_DEATH;
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
        }
    }

    @Override
    public boolean isSensitiveToWater() {
        return true;
    }
}
