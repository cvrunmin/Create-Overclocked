package io.github.cvrunmin.create_andertech.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import io.github.cvrunmin.create_andertech.content.decoration.RichNyliumBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.HugeFungusConfiguration;
import net.minecraft.world.level.levelgen.feature.HugeFungusFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HugeFungusFeature.class)
public class HugeFungusFeatureMixin {
    @Definition(id = "getBlockState", method = "Lnet/minecraft/world/level/WorldGenLevel;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;")
    @Definition(id = "below", method = "Lnet/minecraft/core/BlockPos;below()Lnet/minecraft/core/BlockPos;")
    @Expression("? = ?.getBlockState(?.below())")
    @Inject(method = "place", at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.AFTER))
    private void alsoAcceptRichNylium(FeaturePlaceContext<HugeFungusConfiguration> context, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 0) BlockState blockState, @Local(ordinal = 1) LocalRef<BlockPos> blockpos1){
        if(blockState.getBlock() instanceof RichNyliumBlock){
            blockpos1.set(context.origin().below());
        }
    }
}
