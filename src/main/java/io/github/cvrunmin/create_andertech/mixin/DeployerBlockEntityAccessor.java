package io.github.cvrunmin.create_andertech.mixin;

import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DeployerBlockEntity.class)
public interface DeployerBlockEntityAccessor {
//    @Accessor
//    LazyOptional<IItemHandlerModifiable> getInvHandler();
//    @Accessor
//    void setInvHandler(LazyOptional<IItemHandlerModifiable> value);
//
//    @Accessor
//    ListTag getDeferredInventoryList();
//    @Accessor
//    void setDeferredInventoryList(ListTag value);
}
