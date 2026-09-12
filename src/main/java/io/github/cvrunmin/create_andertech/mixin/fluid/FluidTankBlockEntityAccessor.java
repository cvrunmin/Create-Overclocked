package io.github.cvrunmin.create_andertech.mixin.fluid;

import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import net.createmod.catnip.animation.LerpedFloat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FluidTankBlockEntity.class)
public interface FluidTankBlockEntityAccessor {
    @Accessor(value = "fluidLevel", remap = false)
    LerpedFloat createandertech$getFluidLevel();

    @Accessor(value = "fluidLevel", remap = false)
    void createandertech$setFluidLevel(LerpedFloat value);
}
