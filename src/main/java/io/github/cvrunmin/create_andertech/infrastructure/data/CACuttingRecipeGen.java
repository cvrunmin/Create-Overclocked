package io.github.cvrunmin.create_andertech.infrastructure.data;

import com.simibubi.create.api.data.recipe.CuttingRecipeGen;
import io.github.cvrunmin.create_andertech.CreateAndertech;
import io.github.cvrunmin.create_andertech.index.AllBlocks;
import io.github.cvrunmin.create_andertech.index.AllItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class CACuttingRecipeGen extends CuttingRecipeGen {

    GeneratedRecipe CRIMSCHIST_SHAFT_BY_CUTTING = create("crimschist_shaft_by_cutting", b ->
            b.require(AllItems.CRIMSCHIST_ALLOY).output(AllBlocks.CRIMSCHIST_SHAFT, 6));

    public CACuttingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateAndertech.MODID);
    }
}
