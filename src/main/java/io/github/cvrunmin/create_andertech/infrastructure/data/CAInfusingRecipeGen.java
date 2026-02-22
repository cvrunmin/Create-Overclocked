package io.github.cvrunmin.create_andertech.infrastructure.data;

import com.simibubi.create.api.data.recipe.StandardProcessingRecipeGen;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import io.github.cvrunmin.create_andertech.CreateAndertech;
import io.github.cvrunmin.create_andertech.content.fluids.infusion_vat.InfusionVatRecipe;
import io.github.cvrunmin.create_andertech.index.AllBlocks;
import io.github.cvrunmin.create_andertech.index.AllFluids;
import io.github.cvrunmin.create_andertech.index.CAAllRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class CAInfusingRecipeGen extends StandardProcessingRecipeGen<InfusionVatRecipe> {

    GeneratedRecipe CHRONENDITE_CASING = create("chronendite_casing", b ->
            b.require(AllBlocks.CRIMSCHIST_CASING)
                    .require(AllFluids.CHRONENDITE_SOLUTION.get(), 90)
                    .output(AllBlocks.CHRONENDITE_CASING)
    );

    public CAInfusingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateAndertech.MODID);
    }

    @Override
    protected IRecipeTypeInfo getRecipeType() {
        return CAAllRecipeTypes.INFUSING;
    }
}
