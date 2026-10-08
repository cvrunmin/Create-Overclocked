package io.github.cvrunmin.create_andertech.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity;
import io.github.cvrunmin.create_andertech.index.CAAllRecipeTypes;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MechanicalMixerBlockEntity.class)
public class MechanicalMixerBlockEntityMixin {
    @ModifyReturnValue(method = "matchStaticFilters", at = @At("RETURN"))
    private boolean alsoMatchStirringRecipe(boolean original, @Local(name = "r") Recipe<?> r){
        return original || r.getType() == CAAllRecipeTypes.STIRRING.getType();
    }
}
