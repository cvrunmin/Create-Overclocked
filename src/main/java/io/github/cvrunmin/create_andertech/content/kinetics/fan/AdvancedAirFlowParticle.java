package io.github.cvrunmin.create_andertech.content.kinetics.fan;

import com.simibubi.create.content.kinetics.fan.AirFlowParticle;
import com.simibubi.create.content.kinetics.fan.IAirCurrentSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

public class AdvancedAirFlowParticle extends AirFlowParticle {
    protected AdvancedAirFlowParticle(ClientLevel world, IAirCurrentSource source, double x, double y, double z, SpriteSet sprite) {
        super(world, source, x, y, z, sprite);
        this.lifetime = (int)(40 * Math.max(1, source.getMaxDistance() / 20f));
    }


    public static class Factory implements ParticleProvider<AdvancedAirFlowParticleData> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet animatedSprite) {
            this.spriteSet = animatedSprite;
        }

        @Override
        public Particle createParticle(AdvancedAirFlowParticleData data, ClientLevel worldIn, double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            BlockEntity be = worldIn.getBlockEntity(new BlockPos(data.getPosX(), data.getPosY(), data.getPosZ()));
            if (!(be instanceof IAirCurrentSource))
                be = null;
            return new AdvancedAirFlowParticle(worldIn, (IAirCurrentSource) be, x, y, z, this.spriteSet);
        }
    }
}
