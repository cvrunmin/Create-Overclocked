package io.github.cvrunmin.create_andertech.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import io.github.cvrunmin.create_andertech.index.AllBlockEntityTypes;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(StockTickerBlockEntity.class)
public class StockTickerBlockEntityMixin {
    @Definition(id = "HEATER", field = "Lcom/simibubi/create/AllBlockEntityTypes;HEATER:Lcom/tterrag/registrate/util/entry/BlockEntityEntry;")
    @Definition(id = "is", method = "Lcom/tterrag/registrate/util/entry/BlockEntityEntry;is(Lnet/minecraft/world/level/block/entity/BlockEntity;)Z")
    @Expression("HEATER.is(?)")
    @ModifyExpressionValue(method = "isKeeperPresent", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean isKeeperPresentAlsoCheckChronendicHeater(boolean original, @Local(name = "seatPos") BlockPos seatPos){
        if (!original) {
            AllBlockEntityTypes.CHRONENDIC_BLAZE_BURNER.is(((StockTickerBlockEntity) (Object) this).getLevel().getBlockEntity(seatPos));
        }
        return true;
    }
}
