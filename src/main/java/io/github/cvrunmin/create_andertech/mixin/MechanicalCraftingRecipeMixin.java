package io.github.cvrunmin.create_andertech.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.simibubi.create.content.kinetics.crafter.MechanicalCraftingInventory;
import com.simibubi.create.content.kinetics.crafter.MechanicalCraftingRecipe;
import io.github.cvrunmin.create_andertech.content.kinetics.crafter.OCMechanicalCraftingInventory;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MechanicalCraftingRecipe.class)
public class MechanicalCraftingRecipeMixin {
    @Definition(id = "MechanicalCraftingInventory", type = MechanicalCraftingInventory.class)
    @Expression("? instanceof MechanicalCraftingInventory")
    @ModifyExpressionValue(method = "matches(Lnet/minecraft/world/inventory/CraftingContainer;Lnet/minecraft/world/level/Level;)Z", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static boolean createandertech$alsoCheckOCInventory(boolean original, CraftingContainer inv, Level worldIn){
        return original || inv instanceof OCMechanicalCraftingInventory;
    }
}
