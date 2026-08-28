package io.github.cvrunmin.create_andertech.content.kinetics.fan;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.cvrunmin.create_andertech.index.CAAllParticleTypes;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class AdvancedAirFlowParticleData implements ParticleOptions {

    public static final MapCodec<AdvancedAirFlowParticleData> CODEC = RecordCodecBuilder.mapCodec(i ->
            i.group(
                            Codec.INT.fieldOf("x").forGetter(p -> p.posX),
                            Codec.INT.fieldOf("y").forGetter(p -> p.posY),
                            Codec.INT.fieldOf("z").forGetter(p -> p.posZ))
                    .apply(i, AdvancedAirFlowParticleData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AdvancedAirFlowParticleData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, p -> p.posX,
            ByteBufCodecs.INT, p -> p.posY,
            ByteBufCodecs.INT, p -> p.posZ,
            AdvancedAirFlowParticleData::new
    );

    final int posX;
    final int posY;
    final int posZ;

    public AdvancedAirFlowParticleData(Vec3i pos) {
        this(pos.getX(), pos.getY(), pos.getZ());
    }

    public AdvancedAirFlowParticleData(int posX, int posY, int posZ) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
    }

    public AdvancedAirFlowParticleData() {
        this(0, 0, 0);
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public int getPosZ() {
        return posZ;
    }

    @Override
    public ParticleType<?> getType() {
        return CAAllParticleTypes.ADVANCED_AIR_FLOW.get();
    }
}
