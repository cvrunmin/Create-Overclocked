package io.github.cvrunmin.create_andertech.content.kinetics.transmission;

import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;
import io.github.cvrunmin.create_andertech.foundation.IOverspeedKineticBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class OCSplitShaftBlockEntity extends SplitShaftBlockEntity implements IOverspeedKineticBE {
    public OCSplitShaftBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
}
