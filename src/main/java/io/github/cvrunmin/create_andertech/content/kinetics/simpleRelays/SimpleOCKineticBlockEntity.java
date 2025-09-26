package io.github.cvrunmin.create_andertech.content.kinetics.simpleRelays;

import com.simibubi.create.content.kinetics.simpleRelays.SimpleKineticBlockEntity;
import io.github.cvrunmin.create_andertech.foundation.IOverspeedKineticBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SimpleOCKineticBlockEntity extends SimpleKineticBlockEntity implements IOverspeedKineticBE {
    public SimpleOCKineticBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
}
