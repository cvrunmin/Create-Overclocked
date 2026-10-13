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
    
    GeneratedRecipe CHRONENDITE_CRUSH_WHEEL = create("chronendite_crushing_wheel", b ->
            b.require(com.simibubi.create.AllBlocks.CRUSHING_WHEEL)
                    .require(AllFluids.CHRONENDITE_SOLUTION.get(), 120)
                    .output(AllBlocks.CHRONENDITE_CRUSHING_WHEEL)
    );
    
    GeneratedRecipe CHRONENDITE_SPEED_CONTROLLER = create("chronendite_speed_controller", b ->
            b.require(com.simibubi.create.AllBlocks.ROTATION_SPEED_CONTROLLER)
                    .require(AllFluids.CHRONENDITE_SOLUTION.get(), 90)
                    .output(AllBlocks.CHRONENDITE_ROTATION_SPEED_CONTROLLER)
    );
    
    GeneratedRecipe CHRONENDITE_MECHANICAL_ARM = create("chronendite_mechanical_arm", b ->
            b.require(com.simibubi.create.AllBlocks.MECHANICAL_ARM)
                    .require(AllFluids.CHRONENDITE_SOLUTION.get(), 90)
                    .output(AllBlocks.CHRONENDITE_MECHANICAL_ARM)
    );

    public CAInfusingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateAndertech.MODID);
    }

    @Override
    protected IRecipeTypeInfo getRecipeType() {
        return CAAllRecipeTypes.INFUSING;
    }
}
