package io.github.cvrunmin.create_andertech.content.contraptions.bearing;

import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity;
import io.github.cvrunmin.create_andertech.foundation.IOverspeedKineticBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ChronenditeMechanicalBearingBlockEntity extends MechanicalBearingBlockEntity implements IOverspeedKineticBE {
    public ChronenditeMechanicalBearingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void setAssembleNextTick(boolean flag){
        this.assembleNextTick = flag;
    }
}
