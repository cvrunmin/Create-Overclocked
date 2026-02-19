package io.github.cvrunmin.create_andertech.foundation.interfaces;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public interface IGrandSuperSmartBlockEntity {
    void grandsuperRead(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket);
}
