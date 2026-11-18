package io.github.cvrunmin.create_andertech.content.decoration.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class CylinderMotionEnchantGlyphParticle extends TextureSheetParticle {

    private final Vec3 origin;
    private final float angularSpeed;
    private final float verticalSpeed;

    protected CylinderMotionEnchantGlyphParticle(
            ClientLevel level, double x, double y, double z, CylinderMotionParticleOptions option){
        super(level, x, y, z);
        this.quadSize = 0.1F * (this.random.nextFloat() * 0.5F + 0.2F);
        float f = this.random.nextFloat() * 0.6F + 0.4F;
        this.rCol = 0.9F * f;
        this.gCol = 0.9F * f;
        this.bCol = f;
        this.hasPhysics = false;

        this.lifetime = option.getLife();
        var dist = option.getDistance();
        origin = new Vec3(x, y, z);
        var offsetVec = new Vec3(dist, 0, 0).yRot(option.getPhase());
        var newPos = origin.add(offsetVec);
        setPos(newPos.x, newPos.y, newPos.z);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.angularSpeed = option.getAngularSpeed() / 20;
        this.verticalSpeed = option.getVerticalSpeed() / 20;
    }

    @Override
    public void tick() {

        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.y += this.verticalSpeed;
            var newPos = origin.subtract(this.x, origin.y, this.z).reverse().yRot(this.angularSpeed).add(origin);
            this.x = newPos.x;
            this.z = newPos.z;
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public static class Provider implements ParticleProvider<CylinderMotionParticleOptions>{
        private final SpriteSet sprite;

        public Provider(SpriteSet sprite) {
            this.sprite = sprite;
        }

        @Override
        public @Nullable Particle createParticle(CylinderMotionParticleOptions option, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            var particle = new CylinderMotionEnchantGlyphParticle(level, x, y, z, option);
            particle.pickSprite(sprite);
            return particle;
        }
    }
}
