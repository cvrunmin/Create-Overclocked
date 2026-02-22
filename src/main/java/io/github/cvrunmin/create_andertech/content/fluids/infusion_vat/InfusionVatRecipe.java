package io.github.cvrunmin.create_andertech.content.fluids.infusion_vat;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import io.github.cvrunmin.create_andertech.index.CAAllRecipeTypes;
import io.github.cvrunmin.create_andertech.infrastructure.SingleRecipeInputWithFluid;
import net.createmod.catnip.data.Iterate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.LinkedList;
import java.util.List;

public class InfusionVatRecipe extends StandardProcessingRecipe<SingleRecipeInputWithFluid> {

    protected InfusionVatRecipe(IRecipeTypeInfo typeInfo, ProcessingRecipeParams params) {
        super(typeInfo, params);
    }

    public InfusionVatRecipe(ProcessingRecipeParams params){
        this(CAAllRecipeTypes.INFUSING, params);
    }

    @Override
    protected int getMaxInputCount() {
        return 1;
    }

    @Override
    protected int getMaxOutputCount() {
        return 1;
    }

    @Override
    protected int getMaxFluidInputCount() {
        return 1;
    }

    @Override
    protected int getMaxFluidOutputCount() {
        return 1;
    }

    @Override
    protected boolean canSpecifyDuration() {
        return true;
    }

    @Override
    public boolean matches(SingleRecipeInputWithFluid input, Level level) {
        if(ingredients.size() != 1) return false; // recipes with more than one item ingredients never pass
        if(fluidIngredients.size() > 1) return false;
        var recipeIngredient = ingredients.getFirst();
        if(recipeIngredient.test(input.getItem(0))){
            if(fluidIngredients.isEmpty()) return true;
            var recipeFluidIngredients = fluidIngredients.getFirst();
            return recipeFluidIngredients.test(input.getFluid());
        }
        return false;
    }

    public boolean apply(InfusionVatBlockEntity be, boolean test){
        IItemHandler availableItems = be.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, be.getBlockPos(), null);
        IFluidHandler availableFluids = be.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, be.getBlockPos(), null);
        if(availableItems == null || availableFluids == null) return false;
        List<Ingredient> ingredients = new LinkedList<>(getIngredients());
        List<SizedFluidIngredient> fluidIngredients = getFluidIngredients();

        for (boolean simulate : Iterate.trueAndFalse) {
            if(!simulate && test) return true; // not early returned and recipe test only
            int[] extractedItemsFromSlot = new int[availableItems.getSlots()];
            int[] extractedFluidsFromTank = new int[availableFluids.getTanks()];

            label:
            for (Ingredient ingredient : ingredients) {
                for (int i = 0; i < availableItems.getSlots(); i++) {
                    if(simulate && availableItems.getStackInSlot(i).getCount() <= extractedItemsFromSlot[i])
                        continue;
                    ItemStack extracted = availableItems.extractItem(i, 1, true);
                    if(!ingredient.test(extracted))
                        continue;
                    if(!simulate)
                        availableItems.extractItem(i, 1, false);
                    extractedItemsFromSlot[i]++;
                    continue label;
                }
                return false;
            }
        }
        return false;
    }
}
