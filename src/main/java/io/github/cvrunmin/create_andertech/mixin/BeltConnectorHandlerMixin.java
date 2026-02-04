package io.github.cvrunmin.create_andertech.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.kinetics.belt.item.BeltConnectorHandler;
import io.github.cvrunmin.create_andertech.AllDataComponents;
import io.github.cvrunmin.create_andertech.content.kinetics.belt.OCBeltConnectorHelper;
import io.github.cvrunmin.create_andertech.content.kinetics.simpleRelays.CrimschistShaftBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BeltConnectorHandler.class)
public class BeltConnectorHandlerMixin {
    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/kinetics/simpleRelays/ShaftBlock;isShaft(Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private static boolean createandertech$alsoCheckForCrimschistShaft(boolean original, @Local(name = "level") Level level, @Local(name = "selected") BlockPos selected){
        return original || CrimschistShaftBlock.isShaft(level.getBlockState(selected));
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/kinetics/belt/item/BeltConnectorItem;canConnect(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;)Z"))
    private static boolean createandertech$checkSpeedingConnectionIfNeeded(Level world, BlockPos first, BlockPos selected, Operation<Boolean> original, @Local(name = "heldItem") ItemStack heldItem){
        if(heldItem.getOrDefault(AllDataComponents.BELT_IS_SPEEDING, false)) return OCBeltConnectorHelper.canConnectAsSpeedingBelt(world, first, selected);
        return OCBeltConnectorHelper.isSameTypeOfShaft(world, first, selected) && original.call(world, first, selected);
    }
}
