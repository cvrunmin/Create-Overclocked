package io.github.cvrunmin.create_andertech.content.kinetics.gearbox;

import com.simibubi.create.content.kinetics.base.DirectionalShaftHalvesBlockEntity;
import io.github.cvrunmin.create_andertech.foundation.IOverspeedKineticBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class CrimschistGearboxBlockEntity extends DirectionalShaftHalvesBlockEntity implements IOverspeedKineticBE {
    public CrimschistGearboxBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    @Override
    protected boolean isNoisy() {
        return false;
    }
}
