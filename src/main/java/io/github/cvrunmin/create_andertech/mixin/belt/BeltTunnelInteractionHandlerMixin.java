package io.github.cvrunmin.create_andertech.mixin.belt;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.transport.BeltTunnelInteractionHandler;
import io.github.cvrunmin.create_andertech.content.kinetics.belt.OCBeltBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.belt.OCBeltBlockEntity;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BeltTunnelInteractionHandler.class)
public class BeltTunnelInteractionHandlerMixin {
    @Definition(id = "SLOPE", field = "Lcom/simibubi/create/content/kinetics/belt/BeltBlock;SLOPE:Lnet/minecraft/world/level/block/state/properties/Property;")
    @Expression("SLOPE")
    @ModifyExpressionValue(method = "getTunnelOnSegment", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static Property replaceWithOCBeltProperty(Property original, @Local(name = "belt") BeltBlockEntity belt){
//        if(belt instanceof OCBeltBlockEntity) return OCBeltBlock.SLOPE;
        return original;
    }
}
