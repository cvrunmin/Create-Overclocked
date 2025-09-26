package io.github.cvrunmin.create_andertech.content.kinetics.simpleRelays;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import io.github.cvrunmin.create_andertech.foundation.IOverspeedKineticBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class OCKineticBlockEntity extends KineticBlockEntity implements IOverspeedKineticBE {
    public OCKineticBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }
}
