package io.github.cvrunmin.create_andertech.infrastructure.data;

import com.simibubi.create.api.data.recipe.SequencedAssemblyRecipeGen;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import io.github.cvrunmin.create_andertech.CreateAndertech;
import io.github.cvrunmin.create_andertech.content.kinetics.mixer.StirringRecipe;
import io.github.cvrunmin.create_andertech.index.AllBlocks;
import io.github.cvrunmin.create_andertech.index.AllItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class CASequencedAssemblyRecipeGen extends SequencedAssemblyRecipeGen {

    GeneratedRecipe BLACKBOX_DIFFERENTIAL_FROM_SCRATCH = create("blackbox_differential_from_scratch", b ->
            b.require(AllBlocks.CRIMSCHIST_CASING_SLAB)
                    .transitionTo(AllItems.INCOMPLETE_BLACKBOX_DIFFERENTIAL)
                    .addStep(DeployerApplicationRecipe::new, rb -> rb.require(AllBlocks.CRIMSCHIST_LARGE_COGWHEEL))
                    .addStep(DeployerApplicationRecipe::new, rb -> rb.require(AllBlocks.CRIMSCHIST_COGWHEEL))
                    .addStep(DeployerApplicationRecipe::new, rb -> rb.require(AllBlocks.CRIMSCHIST_COGWHEEL))
                    .addStep(DeployerApplicationRecipe::new, rb -> rb.require(AllBlocks.CRIMSCHIST_COGWHEEL))
                    .addStep(DeployerApplicationRecipe::new, rb -> rb.require(AllBlocks.CRIMSCHIST_COGWHEEL))
                    .addStep(DeployerApplicationRecipe::new, rb -> rb.require(AllBlocks.CRIMSCHIST_CASING_SLAB))
                    .addStep(StirringRecipe::new, rb -> rb)
                    .addOutput(AllBlocks.CRIMSCHIST_BLACKBOX_DIFFERENTIAL, 1)
                    .addOutput(AllItems.MISCONFIGURED_BLACKBOX_DIFFERENTIAL, 9)
                    .loops(1));

    GeneratedRecipe BLACKBOX_DIFFERENTIAL_FROM_MISCONFIGURED = create("blackbox_differential_from_misconfigured", b ->
            b.require(AllItems.MISCONFIGURED_BLACKBOX_DIFFERENTIAL)
                    .transitionTo(AllItems.INCOMPLETE_BLACKBOX_DIFFERENTIAL)
                    .addStep(StirringRecipe::new, rb -> rb)
                    .addOutput(AllBlocks.CRIMSCHIST_BLACKBOX_DIFFERENTIAL, 1)
                    .addOutput(AllItems.MISCONFIGURED_BLACKBOX_DIFFERENTIAL, 9)
                    .loops(1));


    public CASequencedAssemblyRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateAndertech.MODID);
    }

}
