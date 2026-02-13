package io.github.cvrunmin.create_andertech.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.logistics.chute.ChuteBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import io.github.cvrunmin.create_andertech.index.AllBlocks;
import io.github.cvrunmin.create_andertech.content.kinetics.fan.ChronenditeEncasedFanBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.fan.ChronenditeEncasedFanBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChuteBlockEntity.class)
public abstract class ChuteBlockEntityMixin extends SmartBlockEntity {

    public ChuteBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "calculatePull", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;", ordinal = 0), cancellable = true)
    private void createandertech$pullAlsoCheckForOCFan(CallbackInfoReturnable<Float> cir, @Local(ordinal = 0) BlockState blockStateAbove){
        if (AllBlocks.CHRONENDITE_ENCASED_FAN.has(blockStateAbove)
                && blockStateAbove.getValue(ChronenditeEncasedFanBlock.FACING) == Direction.DOWN) {
            BlockEntity be = level.getBlockEntity(worldPosition.above());
            if (be instanceof ChronenditeEncasedFanBlockEntity fan && !be.isRemoved()) {
                cir.setReturnValue(fan.getSpeed());
            }
        }
    }
    @Inject(method = "calculatePush", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;", ordinal = 0), cancellable = true)
    private void createandertech$pushAlsoCheckForOCFan(int branchCount, CallbackInfoReturnable<Float> cir, @Local(ordinal = 0) BlockState blockStateBelow){
        if (AllBlocks.CHRONENDITE_ENCASED_FAN.has(blockStateBelow)
                && blockStateBelow.getValue(ChronenditeEncasedFanBlock.FACING) == Direction.UP) {
            BlockEntity be = level.getBlockEntity(worldPosition.below());
            if (be instanceof ChronenditeEncasedFanBlockEntity fan && !be.isRemoved()) {
                cir.setReturnValue(fan.getSpeed());
            }
        }
    }
}
