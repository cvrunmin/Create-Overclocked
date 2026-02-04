package io.github.cvrunmin.create_andertech.mixin;

import com.simibubi.create.content.kinetics.fan.AirCurrent;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@SuppressWarnings("unused")
@Mixin(AirCurrent.Client.class)
public interface AirCurrentInvoker {
    @Invoker
    static void invokeEnableClientPlayerSound(Entity e, float maxVolume) {

    }
}
