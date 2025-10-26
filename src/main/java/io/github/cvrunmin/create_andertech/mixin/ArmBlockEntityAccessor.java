package io.github.cvrunmin.create_andertech.mixin;

import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ArmBlockEntity.class)
public interface ArmBlockEntityAccessor {
    @Accessor
    boolean isGoggles();

    @Accessor
    void setGoggles(boolean flag);

    @Accessor
    ItemStack getHeldItem();

    @Accessor
    void setHeldItem(ItemStack stack);

    @Accessor
    void setPhase(ArmBlockEntity.Phase phase);
}
