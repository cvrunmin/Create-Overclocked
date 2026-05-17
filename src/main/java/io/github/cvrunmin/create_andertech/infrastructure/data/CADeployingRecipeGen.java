package io.github.cvrunmin.create_andertech.infrastructure.data;

import com.simibubi.create.api.data.recipe.DeployingRecipeGen;
import io.github.cvrunmin.create_andertech.CreateAndertech;
import io.github.cvrunmin.create_andertech.index.AllBlocks;
import io.github.cvrunmin.create_andertech.index.AllItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class CADeployingRecipeGen extends DeployingRecipeGen {

    GeneratedRecipe MECHANICAL_PRESS_UPGRADE = create("upgrade_mechanical_press", b ->
            b.require(com.simibubi.create.AllBlocks.MECHANICAL_PRESS)
                    .require(AllBlocks.CHRONENDITE_CASING)
                    .output(AllBlocks.CHRONENDITE_MECHANICAL_PRESS).output(com.simibubi.create.AllBlocks.ANDESITE_CASING));
    GeneratedRecipe MECHANICAL_MIXER_UPGRADE = create("upgrade_mechanical_mixer", b ->
            b.require(com.simibubi.create.AllBlocks.MECHANICAL_MIXER)
                    .require(AllBlocks.CHRONENDITE_CASING)
                    .output(AllBlocks.CHRONENDITE_MECHANICAL_MIXER).output(com.simibubi.create.AllBlocks.ANDESITE_CASING));
    GeneratedRecipe DEPLOYER_UPGRADE = create("upgrade_deployer", b ->
            b.require(com.simibubi.create.AllBlocks.DEPLOYER)
                    .require(AllBlocks.CHRONENDITE_CASING)
                    .output(AllBlocks.CHRONENDITE_DEPLOYER).output(com.simibubi.create.AllBlocks.ANDESITE_CASING));
    GeneratedRecipe ENCASED_FAN_UPGRADE = create("upgrade_encased_fan", b ->
            b.require(com.simibubi.create.AllBlocks.ENCASED_FAN)
                    .require(AllBlocks.CHRONENDITE_CASING)
                    .output(AllBlocks.CHRONENDITE_ENCASED_FAN).output(com.simibubi.create.AllBlocks.ANDESITE_CASING));
    GeneratedRecipe MECHANICAL_DRILL_UPGRADE = create("upgrade_mechanical_drill", b ->
            b.require(com.simibubi.create.AllBlocks.MECHANICAL_DRILL)
                    .require(AllBlocks.CHRONENDITE_CASING)
                    .output(AllBlocks.CHRONENDITE_MECHANICAL_DRILL).output(com.simibubi.create.AllBlocks.ANDESITE_CASING));
    GeneratedRecipe MECHANICAL_SAW_UPGRADE = create("upgrade_mechanical_saw", b ->
            b.require(com.simibubi.create.AllBlocks.MECHANICAL_SAW)
                    .require(AllBlocks.CHRONENDITE_CASING)
                    .output(AllBlocks.CHRONENDITE_MECHANICAL_SAW).output(com.simibubi.create.AllBlocks.ANDESITE_CASING));
    GeneratedRecipe ROTATION_CONTROLLER_UPGRADE = create("upgrade_rotation_speed_controller", b ->
            b.require(com.simibubi.create.AllBlocks.ROTATION_SPEED_CONTROLLER)
                    .require(AllBlocks.CHRONENDITE_CASING)
                    .output(AllBlocks.CHRONENDITE_ROTATION_SPEED_CONTROLLER).output(com.simibubi.create.AllBlocks.BRASS_CASING));
    GeneratedRecipe MECHANICAL_CRAFTER_UPGRADE = create("upgrade_mechanical_crafter", b ->
            b.require(com.simibubi.create.AllBlocks.MECHANICAL_CRAFTER)
                    .require(AllBlocks.CHRONENDITE_CASING)
                    .output(AllBlocks.CHRONENDITE_MECHANICAL_CRAFTER).output(com.simibubi.create.AllBlocks.BRASS_CASING));

    GeneratedRecipe INFUSING_VAT_BY_DEPLOYER = create("infusing_vat_by_deployer", b ->
            b.require(AllBlocks.CRIMSCHIST_FLUID_TANK)
                    .require(com.simibubi.create.AllItems.WRENCH)
                    .toolNotConsumed()
                    .output(AllBlocks.INFUSION_VAT)
                    .output(AllItems.CRIMSCHIST_SHEET));

    GeneratedRecipe CRIMSHICT_FLUID_TANK_BY_DEPLOYER_ADD_TOP = create("crimshict_fluid_tank_by_deployer_add_top", b ->
            b.require(AllBlocks.INFUSION_VAT)
                    .require(AllItems.CRIMSCHIST_SHEET)
                    .output(AllBlocks.CRIMSCHIST_FLUID_TANK));


    public CADeployingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateAndertech.MODID);
    }
}
