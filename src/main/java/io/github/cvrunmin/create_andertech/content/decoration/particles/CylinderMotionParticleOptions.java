package io.github.cvrunmin.create_andertech.content.decoration.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class CylinderMotionParticleOptions implements ParticleOptions {

    public static class CylinderMotionParticleOptionsParticleType extends ParticleType<CylinderMotionParticleOptions>{

        public CylinderMotionParticleOptionsParticleType(boolean overrideLimitter) {
            super(overrideLimitter);
        }

        @Override
        public MapCodec<CylinderMotionParticleOptions> codec() {
            return RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            Codec.FLOAT.optionalFieldOf("distance", 0f).forGetter(CylinderMotionParticleOptions::getDistance),
                            Codec.FLOAT.optionalFieldOf("angularSpeed", 0f).forGetter(CylinderMotionParticleOptions::getAngularSpeed),
                            Codec.FLOAT.optionalFieldOf("phase", 0f).forGetter(CylinderMotionParticleOptions::getPhase),
                            Codec.FLOAT.optionalFieldOf("verticalSpeed", 0f).forGetter(CylinderMotionParticleOptions::getVerticalSpeed),
                            Codec.INT.optionalFieldOf("life", 20).forGetter(CylinderMotionParticleOptions::getLife),
                            Codec.INT.optionalFieldOf("emitterLife", 1).forGetter(CylinderMotionParticleOptions::getLife)
                    ).apply(instance,
                            (dist, angSpeed, phase, vertSpeed, life, emitterLife) ->
                                    new CylinderMotionParticleOptions(this, dist, angSpeed, phase, vertSpeed, life, emitterLife)));
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, CylinderMotionParticleOptions> streamCodec() {
            return StreamCodec.composite(
                    ByteBufCodecs.FLOAT, CylinderMotionParticleOptions::getDistance,
                    ByteBufCodecs.FLOAT, CylinderMotionParticleOptions::getAngularSpeed,
                    ByteBufCodecs.FLOAT, CylinderMotionParticleOptions::getPhase,
                    ByteBufCodecs.FLOAT, CylinderMotionParticleOptions::getVerticalSpeed,
                    ByteBufCodecs.INT, CylinderMotionParticleOptions::getLife,
                    ByteBufCodecs.INT, CylinderMotionParticleOptions::getEmitterLife,
                    (dist, angSpeed, phase, vertSpeed, life, emitterLife) ->
                            new CylinderMotionParticleOptions(this, dist, angSpeed, phase, vertSpeed, life, emitterLife)
            );
        }
    }

    private final ParticleType<CylinderMotionParticleOptions> type;
    private final float distance;
    private final float angularSpeed;
    private final float phase;
    private final float verticalSpeed;
    private final int life;
    private final int emitterLife;

    public CylinderMotionParticleOptions(ParticleType<CylinderMotionParticleOptions> type,
                                         float distance,
                                         float angularSpeed,
                                         float phase,
                                         float verticalSpeed,
                                         int life,
                                         int emitterLife
                                      ){
        this.type = type;
        this.distance = distance;
        this.angularSpeed = angularSpeed;
        this.phase = phase;
        this.verticalSpeed = verticalSpeed;
        this.life = life;
        this.emitterLife = emitterLife;
    }

    @Override
    public ParticleType<?> getType() {
        return type;
    }

    public float getDistance() {
        return distance;
    }

    public float getAngularSpeed() {
        return angularSpeed;
    }

    public float getPhase() {
        return phase;
    }

    public float getVerticalSpeed() {
        return verticalSpeed;
    }

    public int getLife() {
        return life;
    }

    public int getEmitterLife() {
        return emitterLife;
    }
}
