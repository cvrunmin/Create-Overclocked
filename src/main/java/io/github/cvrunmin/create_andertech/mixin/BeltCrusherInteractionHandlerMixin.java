package io.github.cvrunmin.create_andertech.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.kinetics.belt.transport.BeltCrusherInteractionHandler;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.crusher.ChronenditeCrushWheelControllerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BeltCrusherInteractionHandler.class)
public class BeltCrusherInteractionHandlerMixin {
    @Definition(id = "crusherState", local = @Local(type = BlockState.class))
    @Definition(id = "getBlock", method = "Lnet/minecraft/world/level/block/state/BlockState;getBlock()Lnet/minecraft/world/level/block/Block;")
    @Definition(id = "CrushingWheelControllerBlock", type = CrushingWheelControllerBlock.class)
    @Expression("crusherState.getBlock() instanceof CrushingWheelControllerBlock")
    @ModifyExpressionValue(method = "checkForCrushers", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static boolean createandertech$alsoCheckForChronenditeCrusher(boolean original, @Local(name = "crusherState") BlockState crusherState){
        return original || crusherState.getBlock() instanceof ChronenditeCrushWheelControllerBlock;
    }
}
