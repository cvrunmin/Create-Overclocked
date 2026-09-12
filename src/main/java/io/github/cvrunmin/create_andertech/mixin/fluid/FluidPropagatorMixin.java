package io.github.cvrunmin.create_andertech.mixin.fluid;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.simibubi.create.content.fluids.FluidPropagator;
import com.simibubi.create.content.fluids.pump.PumpBlock;
import io.github.cvrunmin.create_andertech.content.fluids.pump.CrimschistPumpBlock;
import io.github.cvrunmin.create_andertech.content.fluids.pump.CrimschistPumpBlockEntity;
import net.createmod.catnip.data.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Set;

@Mixin(FluidPropagator.class)
public class FluidPropagatorMixin {
    @Inject(method = "propagateChangedPipe", at = @At("HEAD"))
    private static void injectPropagate$head(LevelAccessor world, BlockPos pipePos, BlockState pipeState, CallbackInfo ci,
                                             @Share("discoveredCrimschistPumps")LocalRef<Set<Pair<CrimschistPumpBlockEntity, Direction>>> discoveredCrimschistPumps){
        discoveredCrimschistPumps.set(new HashSet<>());
    }

    @Definition(id = "visited", local = @Local(type = Set.class, name = "visited"))
    @Definition(id = "contains", method = "Ljava/util/Set;contains(Ljava/lang/Object;)Z")
    @Definition(id = "target", local = @Local(type = BlockPos.class, name = "target"))
    @Expression("visited.contains(target)")
    @ModifyExpressionValue(method = "propagateChangedPipe", at = @At(value = "MIXINEXTRAS:EXPRESSION"))
    private static boolean injectPropagate$update(boolean original,
                                                  @Local(name = "direction") Direction direction,
                                                  @Local(name = "blockEntity") BlockEntity blockEntity,
                                                  @Local(name = "targetState") BlockState targetState,
                                                  @Share("discoveredCrimschistPumps")LocalRef<Set<Pair<CrimschistPumpBlockEntity, Direction>>> discoveredCrimschistPumps){
        var realSet = discoveredCrimschistPumps.get();
        if (blockEntity instanceof CrimschistPumpBlockEntity) {
            if (targetState.getBlock() instanceof CrimschistPumpBlock && targetState.getValue(CrimschistPumpBlock.FACING).getAxis() == direction.getAxis()) {
                realSet.add(Pair.of((CrimschistPumpBlockEntity) blockEntity, direction.getOpposite()));
            }
            return true;
        }
        return original;
    }

    @Inject(method = "propagateChangedPipe", at = @At("TAIL"))
    private static void injectPropagate$tail(LevelAccessor world, BlockPos pipePos, BlockState pipeState, CallbackInfo ci,
                                             @Share("discoveredCrimschistPumps")LocalRef<Set<Pair<CrimschistPumpBlockEntity, Direction>>> discoveredCrimschistPumps){
        var realSet = discoveredCrimschistPumps.get();
        realSet.forEach(pair -> pair.getFirst().updatePipesOnSide(pair.getSecond()));
    }

    @Definition(id = "otherBlock", local = @Local(type = Block.class, argsOnly = true))
    @Definition(id = "PumpBlock", type = PumpBlock.class)
    @Expression("otherBlock instanceof PumpBlock")
    @ModifyExpressionValue(method = "validateNeighbourChange", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static boolean validateNeighbourChangeIncludeCrimschist(boolean original, @Local(type = Block.class, argsOnly = true) Block otherBlock){
        return original || otherBlock instanceof CrimschistPumpBlock;
    }

    @Inject(method = "isOpenEnd", at = @At("HEAD"), cancellable = true)
    private static void injectIsOpenEnd(BlockGetter reader, BlockPos pos, Direction side, CallbackInfoReturnable<Boolean> cir){
        BlockPos connectedPos = pos.relative(side);
        BlockState connectedState = reader.getBlockState(connectedPos);
        if (CrimschistPumpBlock.isPump(connectedState) && connectedState.getValue(PumpBlock.FACING)
                .getAxis() == side.getAxis())
            cir.setReturnValue(false);
    }

    @Inject(method = "getStraightPipeAxis", at = @At("HEAD"), cancellable = true)
    private static void injectGetStraightPipeAxis(BlockState state, CallbackInfoReturnable<Direction.Axis> cir){
        if (state.getBlock() instanceof CrimschistPumpBlock)
            cir.setReturnValue(state.getValue(CrimschistPumpBlock.FACING).getAxis());
    }
}
