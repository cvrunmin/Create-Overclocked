package io.github.cvrunmin.create_andertech.mixin.fluid;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.fluids.pump.PumpBlockEntity;
import io.github.cvrunmin.create_andertech.content.fluids.pump.CrimschistPumpBlock;
import io.github.cvrunmin.create_andertech.content.fluids.pump.CrimschistPumpBlockEntity;
import net.createmod.catnip.math.BlockFace;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PumpBlockEntity.class)
public class PumpBlockEntityMixin {
    @Inject(method = "hasReachedValidEndpoint", at = @At("HEAD"), cancellable = true)
    void injectHasReachedValidEndpoint(LevelAccessor world, BlockFace blockFace, boolean pull, CallbackInfoReturnable<Boolean> cir){
        BlockPos connectedPos = blockFace.getConnectedPos();
        BlockState connectedState = world.getBlockState(connectedPos);
        BlockEntity blockEntity = world.getBlockEntity(connectedPos);
        Direction face = blockFace.getFace();
        if (CrimschistPumpBlock.isPump(connectedState) && connectedState.getValue(CrimschistPumpBlock.FACING)
                .getAxis() == face.getAxis() && blockEntity instanceof CrimschistPumpBlockEntity pumpBE) {
            cir.setReturnValue(pumpBE.isPullingOnSide(pumpBE.isFront(blockFace.getOppositeFace())) != pull);
        }
    }

    @Definition(id = "pipeBehaviour", local = @Local(type = FluidTransportBehaviour.class, name = "pipeBehaviour"))
    @Expression("pipeBehaviour == null")
    @ModifyExpressionValue(method = "distributePressureTo", at = @At("MIXINEXTRAS:EXPRESSION"))
    boolean distributePressureToAddCrimschistCheck(boolean original, @Local(name = "pipeBehaviour") FluidTransportBehaviour pipeBehaviour){
        return original || pipeBehaviour instanceof CrimschistPumpBlockEntity.PumpFluidTransferBehaviour;
    }
}
