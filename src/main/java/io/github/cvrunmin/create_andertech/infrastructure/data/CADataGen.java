package io.github.cvrunmin.create_andertech.infrastructure.data;

import com.tterrag.registrate.providers.ProviderType;
import io.github.cvrunmin.create_andertech.CreateAndertech;
import io.github.cvrunmin.create_andertech.index.AllExtraModels;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

import static io.github.cvrunmin.create_andertech.CreateAndertech.REGISTRATE;

public class CADataGen {

    public static void gatherDataHighPriority(GatherDataEvent event){
        if (!event.getMods().contains(CreateAndertech.MODID))
            return;
        REGISTRATE.addDataGenerator(ProviderType.LANG, provider -> {
            provider.add("itemGroup.create_andertech.main", "Create Andertech");
        });
        REGISTRATE.addDataGenerator(ProviderType.BLOCKSTATE, AllExtraModels::genBlockStates);
    }

    public static void gatherData(GatherDataEvent event){
        if (!event.getMods().contains(CreateAndertech.MODID))
            return;
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        generator.addProvider(event.includeServer(), new StandardRecipeGen(output, lookupProvider));
        generator.addProvider(event.includeServer(), new CAMixingRecipeGen(output, lookupProvider));
    }
}
