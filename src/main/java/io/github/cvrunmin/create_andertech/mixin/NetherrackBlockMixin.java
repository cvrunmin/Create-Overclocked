package io.github.cvrunmin.create_andertech.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import io.github.cvrunmin.create_andertech.index.AllBlocks;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.NetherrackBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetherrackBlock.class)
public class NetherrackBlockMixin {
    @Definition(id = "getBlockState", method = "Lnet/minecraft/server/level/ServerLevel;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;")
    @Definition(id = "p_221811_", local = @Local(type = ServerLevel.class, ordinal = 0))
    @Expression(value="? = p_221811_.getBlockState(?)", id="actual_point")
    @Inject(method = "performBonemeal", at = @At(value = "MIXINEXTRAS:EXPRESSION",
            id = "actual_point",
            shift = At.Shift.AFTER),
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;betweenClosed(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;)Ljava/lang/Iterable;"),
                           to = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/Blocks;WARPED_NYLIUM:Lnet/minecraft/world/level/block/Block;")))
    private void alsoBonemealableFromRichNylium(CallbackInfo ci, @Local(ordinal = 1) BlockState blockState, @Local(ordinal = 0) LocalBooleanRef flag, @Local(ordinal = 1) LocalBooleanRef flag1){
        if(blockState.is(AllBlocks.RICH_CRIMSON_NYLIUM)){
            flag.set(true);
        }
        if(blockState.is(AllBlocks.RICH_WARPED_NYLIUM)){
            flag1.set(true);
        }
    }
}
