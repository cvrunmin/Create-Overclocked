package io.github.cvrunmin.create_andertech.mixin;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import io.github.cvrunmin.create_andertech.foundation.interfaces.IGrandSuperKineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DeployerBlockEntity.class)
public abstract class DeployerBlockEntityMixin extends KineticBlockEntity implements IGrandSuperKineticBlockEntity {
    public DeployerBlockEntityMixin(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public void grandsuperTick() {
        super.tick();
    }

    @Override
    public void grandsuperInitialize() {
        super.initialize();
    }
}
