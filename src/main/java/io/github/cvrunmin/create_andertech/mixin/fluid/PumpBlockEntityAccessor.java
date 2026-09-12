package io.github.cvrunmin.create_andertech.mixin.fluid;

import com.simibubi.create.content.fluids.pump.PumpBlockEntity;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PumpBlockEntity.class)
public interface PumpBlockEntityAccessor {
    @Invoker("isFront")
    boolean invokeIsFront(Direction side);
}
