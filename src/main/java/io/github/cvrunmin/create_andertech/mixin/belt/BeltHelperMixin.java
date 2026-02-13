package io.github.cvrunmin.create_andertech.mixin.belt;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BeltHelper.class)
public class BeltHelperMixin {
    @Definition(id = "SLOPE", field = "Lcom/simibubi/create/content/kinetics/belt/BeltBlock;SLOPE:Lnet/minecraft/world/level/block/state/properties/Property;")
    @Expression("SLOPE")
    @ModifyExpressionValue(method = {"getPositionForOffset", "getVectorForOffset"}, at = @At("MIXINEXTRAS:EXPRESSION"))
    private static Property createandertech$replaceWithOCBeltProperty(Property original, BeltBlockEntity controller){
//        if(controller instanceof OCBeltBlockEntity){
//            return OCBeltBlock.SLOPE;
//        }
        return original;
    }

    @Definition(id = "SLOPE", field = "Lcom/simibubi/create/content/kinetics/belt/BeltBlock;SLOPE:Lnet/minecraft/world/level/block/state/properties/Property;")
    @Expression("SLOPE")
    @ModifyExpressionValue(method = "getBeltVector", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static Property createandertech$replaceWithOCBeltProperty(Property original, BlockState state){
//        if(AllBlocks.OC_BELT.has(state)){
//            return OCBeltBlock.SLOPE;
//        }
        return original;
    }
}
