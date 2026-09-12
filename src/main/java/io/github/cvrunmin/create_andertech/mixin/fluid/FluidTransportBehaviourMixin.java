package io.github.cvrunmin.create_andertech.mixin.fluid;

import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import io.github.cvrunmin.create_andertech.content.fluids.pump.CrimschistPumpBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FluidTransportBehaviour.class)
public class FluidTransportBehaviourMixin {

    @Inject(method = "getRenderedRimAttachment", at = @At("HEAD"), cancellable = true)
    private void getRenderedRimAttachmentCheckCrimschistPump(BlockAndTintGetter world, BlockPos pos, BlockState state, Direction direction, CallbackInfoReturnable<FluidTransportBehaviour.AttachmentTypes> cir){
        BlockPos offsetPos = pos.relative(direction);
        BlockState facingState = world.getBlockState(offsetPos);
        if (facingState.getBlock() instanceof CrimschistPumpBlock
                && facingState.getValue(CrimschistPumpBlock.FACING) == direction.getOpposite())
            cir.setReturnValue(FluidTransportBehaviour.AttachmentTypes.NONE);
    }
}
