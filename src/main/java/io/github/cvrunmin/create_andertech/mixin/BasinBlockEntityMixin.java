package io.github.cvrunmin.create_andertech.mixin;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import io.github.cvrunmin.create_andertech.content.processing.burner.ChronendicBlazeBurnerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BasinBlockEntity.class)
public class BasinBlockEntityMixin {
    @Inject(method = "getHeatLevelOf", at = @At("HEAD"), cancellable = true)
    private static void injectGetHeatLevelOf(BlockState state, CallbackInfoReturnable<BlazeBurnerBlock.HeatLevel> cir){
        if(state.hasProperty(ChronendicBlazeBurnerBlock.HEAT_LEVEL)){
            cir.setReturnValue(state.getValue(ChronendicBlazeBurnerBlock.HEAT_LEVEL));
        }
    }
}
