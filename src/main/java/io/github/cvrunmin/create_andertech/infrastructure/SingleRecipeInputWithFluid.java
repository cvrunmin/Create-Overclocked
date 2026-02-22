package io.github.cvrunmin.create_andertech.infrastructure;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;

public class SingleRecipeInputWithFluid implements RecipeInput {

    private final ItemStack item;
    private final FluidStack fluid;

    public SingleRecipeInputWithFluid(ItemStack itemStack, FluidStack fluidStack){
        item = itemStack;
        fluid = fluidStack;
    }

    @Override
    public ItemStack getItem(int index) {
        if(index != 0) throw new IllegalArgumentException("Recipe does not contain slot " + index);
        return item;
    }

    public FluidStack getFluid(){
        return fluid;
    }

    @Override
    public int size() {
        return 1;
    }
}
