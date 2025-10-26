package io.github.cvrunmin.create_andertech.content.kinetics.fan;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ChronenditeHVEncasedFanBlockEntity extends ChronenditeEncasedFanBlockEntity{
    public ChronenditeHVEncasedFanBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public float getFlowDistanceCalculatingSpeed(){
        return this.getSpeed() / 8;
    }

    @Override
    public int getFanProcessingSpeedMultiplier(){
        return Math.min((int)Math.floor(Math.abs(this.getSpeed() / 8)), 40);
    }
}
