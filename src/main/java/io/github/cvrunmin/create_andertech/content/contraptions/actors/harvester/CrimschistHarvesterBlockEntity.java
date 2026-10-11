package io.github.cvrunmin.create_andertech.content.contraptions.actors.harvester;

import com.simibubi.create.foundation.blockEntity.CachedRenderBBBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class CrimschistHarvesterBlockEntity extends CachedRenderBBBlockEntity {
    private float demonstrationAnimatedSpeed;

    public CrimschistHarvesterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return new AABB(worldPosition);
    }

    public float getAnimatedSpeed() {
        return demonstrationAnimatedSpeed;
    }

    public void setAnimatedSpeed(float speed) {
        demonstrationAnimatedSpeed = speed;
    }

}
