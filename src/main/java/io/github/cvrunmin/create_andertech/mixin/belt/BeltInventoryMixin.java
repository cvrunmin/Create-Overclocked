package io.github.cvrunmin.create_andertech.mixin.belt;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.transport.BeltInventory;
import io.github.cvrunmin.create_andertech.content.kinetics.belt.OCBeltBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.belt.OCBeltBlockEntity;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(BeltInventory.class)
public class BeltInventoryMixin {

    @Final
    @Shadow
    BeltBlockEntity belt;

    @Definition(id = "SLOPE", field = "Lcom/simibubi/create/content/kinetics/belt/BeltBlock;SLOPE:Lnet/minecraft/world/level/block/state/properties/Property;")
    @Expression("SLOPE")
    @ModifyExpressionValue(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
    private Property replaceWithOCBeltProperty(Property original){
//        if(belt instanceof OCBeltBlockEntity) return OCBeltBlock.SLOPE;
        return original;
    }
}
