package io.github.cvrunmin.create_andertech.infrastructure.data;

import com.simibubi.create.api.data.recipe.PressingRecipeGen;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import io.github.cvrunmin.create_andertech.CreateAndertech;
import io.github.cvrunmin.create_andertech.index.AllItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class CAPressingRecipeGen extends PressingRecipeGen {


    GeneratedRecipe CRIMSCHIST_SHEET = create("crimschist_sheet", b ->
            b.require(AllItems.CRIMSCHIST_ALLOY)
                    .output(AllItems.CRIMSCHIST_SHEET, 2));

    public CAPressingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateAndertech.MODID);
    }

}
