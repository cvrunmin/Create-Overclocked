package io.github.cvrunmin.create_andertech.mixin.belt;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.transport.BeltMovementHandler;
import io.github.cvrunmin.create_andertech.index.AllBlocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BeltMovementHandler.class)
public class BeltMovementHandlerMixin {
//    @Definition(id = "SLOPE", field = "Lcom/simibubi/create/content/kinetics/belt/BeltBlock;SLOPE:Lnet/minecraft/world/level/block/state/properties/Property;")
//    @Expression("SLOPE")
//    @ModifyExpressionValue(method = "transportEntity", at = @At("MIXINEXTRAS:EXPRESSION"))
//    private static Property replaceWithOCBeltProperty(Property original, BeltBlockEntity beltBE){
////        if(beltBE instanceof OCBeltBlockEntity){
////            return OCBeltBlock.SLOPE;
////        }
//        return original;
//    }
//
//    @Definition(id = "PART", field = "Lcom/simibubi/create/content/kinetics/belt/BeltBlock;PART:Lnet/minecraft/world/level/block/state/properties/Property;")
//    @Expression("PART")
//    @ModifyExpressionValue(method = "transportEntity", at = @At("MIXINEXTRAS:EXPRESSION"))
//    private static Property replaceWithOCBeltProperty$2(Property original, BeltBlockEntity beltBE){
////        if(beltBE instanceof OCBeltBlockEntity){
////            return OCBeltBlock.PART;
////        }
//        return original;
//    }

    @Definition(id = "BELT", field = "Lcom/simibubi/create/AllBlocks;BELT:Lcom/tterrag/registrate/util/entry/BlockEntry;")
    @Definition(id = "has", method = "Lcom/tterrag/registrate/util/entry/BlockEntry;has(Lnet/minecraft/world/level/block/state/BlockState;)Z")
    @Definition(id = "getBlockState", method = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;")
    @Definition(id = "blockPosition", method = "Lnet/minecraft/world/entity/Entity;blockPosition()Lnet/minecraft/core/BlockPos;")
    @Expression("BELT.has(?.getBlockState(?.blockPosition()))")
    @ModifyExpressionValue(method = "transportEntity", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static boolean createandertech$alsoAllowOCBelt(boolean original, BeltBlockEntity beltBE, Entity entityIn, @Local(name = "world")Level world){
        return original || AllBlocks.OC_BELT.has(world.getBlockState(entityIn.blockPosition()));
    }

    @Definition(id = "BELT", field = "Lcom/simibubi/create/AllBlocks;BELT:Lcom/tterrag/registrate/util/entry/BlockEntry;")
    @Definition(id = "has", method = "Lcom/tterrag/registrate/util/entry/BlockEntry;has(Lnet/minecraft/world/level/block/state/BlockState;)Z")
    @Definition(id = "getBlockState", method = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;")
    @Definition(id = "blockPosition", method = "Lnet/minecraft/world/entity/Entity;blockPosition()Lnet/minecraft/core/BlockPos;")
    @Definition(id = "below", method = "Lnet/minecraft/core/BlockPos;below()Lnet/minecraft/core/BlockPos;")
    @Expression("BELT.has(?.getBlockState(?.blockPosition().below()))")
    @ModifyExpressionValue(method = "transportEntity", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static boolean createandertech$alsoAllowOCBeltBelow(boolean original, BeltBlockEntity beltBE, Entity entityIn, @Local(name = "world")Level world){
        return original || AllBlocks.OC_BELT.has(world.getBlockState(entityIn.blockPosition().below()));
    }
}
