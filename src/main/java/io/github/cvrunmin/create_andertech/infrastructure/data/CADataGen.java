package io.github.cvrunmin.create_andertech.infrastructure.data;

import com.tterrag.registrate.providers.ProviderType;
import io.github.cvrunmin.create_andertech.CreateAndertech;
import io.github.cvrunmin.create_andertech.index.AllCreativeTabs;
import io.github.cvrunmin.create_andertech.index.AllExtraModels;
import io.github.cvrunmin.create_andertech.index.AllTags;
import io.github.cvrunmin.create_andertech.index.CAAllParticleTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.ParticleDescriptionProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

import static io.github.cvrunmin.create_andertech.CreateAndertech.REGISTRATE;

public class CADataGen {

    public static void gatherDataHighPriority(GatherDataEvent event){
        if (!event.getMods().contains(CreateAndertech.MODID))
            return;
        REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, provider -> {
            provider.addTag(AllTags.EXPLOSIVE_TO_TRANSMUTER).add(Blocks.TNT);
        });
        REGISTRATE.addDataGenerator(ProviderType.LANG, provider -> {
            provider.add(AllCreativeTabs.MAIN_TAB.get(), "Create Andertech");
            provider.add(CreateAndertech.MODID + ".gui.goggles.vat_contents", "Vat Contents:");
            provider.add(CreateAndertech.MODID + ".infusion_vat.content_interaction_state.label", "Content Interaction");
            provider.add(CreateAndertech.MODID + ".infusion_vat.content_interaction_state.ingredient", "Ingredient Only");
            provider.add(CreateAndertech.MODID + ".infusion_vat.content_interaction_state.all", "All");
            provider.add(CreateAndertech.MODID + ".infusion_vat.content_interaction_state.product", "Product Only");
            provider.add(CreateAndertech.MODID + ".tooltip.noTank", "No Fluid Container");
            provider.add(CreateAndertech.MODID + ".tooltip.noTank.hint", "It appears that there is _no_ fluid container _above_ this %1$s.");

            provider.add(CreateAndertech.MODID + ".differential.header", "Blackbox Differential");
            provider.add(CreateAndertech.MODID + ".differential.port.neg", "Negative Side");
            provider.add(CreateAndertech.MODID + ".differential.port.pos", "Positive Side");
            provider.add(CreateAndertech.MODID + ".differential.port.cog", "Large Cog");
            provider.add(CreateAndertech.MODID + ".differential.mode.none", "Covered");
            provider.add(CreateAndertech.MODID + ".differential.mode.in", "Input");
            provider.add(CreateAndertech.MODID + ".differential.mode.out", "Output");
            provider.add(CreateAndertech.MODID + ".tooltip.differential.misconfigured", "Misconfigured Differential");
            provider.add(CreateAndertech.MODID + ".tooltip.differential.misconfigured.hint", "It appears that this %1$s has _too many_ outputs, or _no_ inputs.");

            provider.add(CreateAndertech.MODID + ".recipe.infusing", "Infusing");
            provider.add(CreateAndertech.MODID + ".recipe.assembly.stirring", "Stir");
        });
        REGISTRATE.addDataGenerator(ProviderType.BLOCKSTATE, AllExtraModels::genBlockStates);
    }

    public static void gatherData(GatherDataEvent event){
        if (!event.getMods().contains(CreateAndertech.MODID))
            return;
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        var entriesProvider = new AdditionalEntriesProvider(output, lookupProvider);
        lookupProvider = entriesProvider.getRegistryProvider();
        generator.addProvider(event.includeServer(), entriesProvider);

        generator.addProvider(event.includeServer(), new StandardRecipeGen(output, lookupProvider));
        generator.addProvider(event.includeServer(), new CADeployingRecipeGen(output, lookupProvider));
        generator.addProvider(event.includeServer(), new CACuttingRecipeGen(output, lookupProvider));
        generator.addProvider(event.includeServer(), new CAPressingRecipeGen(output, lookupProvider));
        generator.addProvider(event.includeServer(), new CAMixingRecipeGen(output, lookupProvider));
        generator.addProvider(event.includeServer(), new CAInfusingRecipeGen(output, lookupProvider));
        generator.addProvider(event.includeServer(), new CASequencedAssemblyRecipeGen(output, lookupProvider));

        generator.addProvider(event.includeClient(), new ParticleDescriptionProvider(output, event.getExistingFileHelper()) {
            @Override
            protected void addDescriptions() {
                spriteSet(CAAllParticleTypes.ADVANCED_AIR_FLOW.get(),
                        ResourceLocation.withDefaultNamespace("generic_7"),
                        ResourceLocation.withDefaultNamespace("generic_6"),
                        ResourceLocation.withDefaultNamespace("generic_5"),
                        ResourceLocation.withDefaultNamespace("generic_4"),
                        ResourceLocation.withDefaultNamespace("generic_3"),
                        ResourceLocation.withDefaultNamespace("generic_2"),
                        ResourceLocation.withDefaultNamespace("generic_1"),
                        ResourceLocation.withDefaultNamespace("generic_0")
                );
            }
        });
    }
}
