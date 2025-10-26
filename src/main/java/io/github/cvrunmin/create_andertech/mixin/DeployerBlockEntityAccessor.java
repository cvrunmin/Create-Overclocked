package io.github.cvrunmin.create_andertech.mixin;

import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import net.minecraft.nbt.ListTag;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DeployerBlockEntity.class)
public interface DeployerBlockEntityAccessor {
    @Accessor
    LazyOptional<IItemHandlerModifiable> getInvHandler();
    @Accessor
    void setInvHandler(LazyOptional<IItemHandlerModifiable> value);

    @Accessor
    ListTag getDeferredInventoryList();
    @Accessor
    void setDeferredInventoryList(ListTag value);
}
