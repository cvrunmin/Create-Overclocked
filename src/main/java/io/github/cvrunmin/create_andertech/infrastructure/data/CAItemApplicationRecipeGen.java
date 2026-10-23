package io.github.cvrunmin.create_andertech.infrastructure.data;

import com.simibubi.create.api.data.recipe.ItemApplicationRecipeGen;
import io.github.cvrunmin.create_andertech.CreateAndertech;
import io.github.cvrunmin.create_andertech.index.AllBlocks;
import io.github.cvrunmin.create_andertech.index.AllItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class CAItemApplicationRecipeGen extends ItemApplicationRecipeGen {

    GeneratedRecipe CRIMSCHIST = woodCasing("crimschist", () -> AllItems.CRIMSCHIST_ALLOY, () -> AllBlocks.CRIMSCHIST_CASING);

    public CAItemApplicationRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateAndertech.MODID);
    }
}
