package io.github.cvrunmin.create_andertech.content.decoration.particles;

import io.github.cvrunmin.create_andertech.index.CAAllParticleTypes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import org.jetbrains.annotations.Nullable;

public class CylinderMotionEnchantGlyphEmitterParticle extends NoRenderParticle {
    private final CylinderMotionParticleOptions options;

    private final int tickForNewParticle;

    protected CylinderMotionEnchantGlyphEmitterParticle(ClientLevel level, double x, double y, double z, CylinderMotionParticleOptions options) {
        super(level, x, y, z);
        this.options = options;
        this.lifetime = this.options.getEmitterLife();
        tickForNewParticle = (int) Math.ceil(0.25 / (this.options.getVerticalSpeed() / 20));
    }

    @Override
    public void tick() {
        if(age == 0 || age % tickForNewParticle == 0){
            var numPoints = (int)(2 * Math.PI * options.getDistance() / 0.2);
            for (int i = 0; i < numPoints; i++) {
                var phase = (float)(2 * Math.PI * i / numPoints);
                this.level.addParticle(new CylinderMotionParticleOptions(CAAllParticleTypes.CYLINDER_MOTION_ENCHANT_GLYPH.get(),
                        options.getDistance(), options.getAngularSpeed(), phase, options.getVerticalSpeed(), options.getLife(), options.getEmitterLife()),
                        x, y, z, 0, 0, 0);
            }
        }
        this.age++;
        if (this.age == this.lifetime) {
            this.remove();
        }
    }

    public static class Provider implements ParticleProvider<CylinderMotionParticleOptions>{

        @Override
        public @Nullable Particle createParticle(CylinderMotionParticleOptions type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new CylinderMotionEnchantGlyphEmitterParticle(level, x, y, z, type);
        }
    }
}
