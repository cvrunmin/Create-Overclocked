package io.github.cvrunmin.create_andertech;

import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class AllCreativeTabs {
    private static final DeferredRegister<CreativeModeTab> REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateAndertech.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = REGISTER.register("main", () ->
        CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.create_andertech.main"))
                .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                .icon(AllBlocks.CREATIVE_OC_MOTOR::asStack)
                .displayItems(new MyDisplayitemGen(AllCreativeTabs.MAIN_TAB))
                .build()
    );

    public static void register(IEventBus modEventBus){
        REGISTER.register(modEventBus);
    }

    private static class MyDisplayitemGen implements CreativeModeTab.DisplayItemsGenerator{

        private final DeferredHolder<CreativeModeTab,CreativeModeTab> tabFilter;

        public MyDisplayitemGen(DeferredHolder<CreativeModeTab,CreativeModeTab> tab) {
            this.tabFilter = tab;
        }

        private static Predicate<Item> makeExclusion(){
            Set<Item> exclusions = new ReferenceOpenHashSet<>();
            var simpleBlacklist = List.of(
                    AllBlocks.ANDESITE_ENCASED_CRIMSCHIST_SHAFT,
                    AllBlocks.BRASS_ENCASED_CRIMSCHIST_SHAFT,
                    AllBlocks.ANDESITE_ENCASED_CRIMSCHIST_COGWHEEL,
                    AllBlocks.BRASS_ENCASED_CRIMSCHIST_COGWHEEL,
                    AllBlocks.ANDESITE_ENCASED_CRIMSCHIST_LARGE_COGWHEEL,
                    AllBlocks.BRASS_ENCASED_CRIMSCHIST_LARGE_COGWHEEL,
                    AllBlocks.METAL_GIRDER_ENCASED_CRIMSCHIST_SHAFT
            );
            for (ItemProviderEntry<? extends FeatureElement, ?> itemProviderEntry : simpleBlacklist) {
                exclusions.add(itemProviderEntry.asItem());
            }
            return exclusions::contains;
        }

        @Override
        public void accept(CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output) {
            var exclusionPredicate = makeExclusion();
                for (RegistryEntry<Block, Block> entry : CreateAndertech.REGISTRATE.getAll(Registries.BLOCK)) {
                if(entry.get().asItem() == Items.AIR) continue;
                if(exclusionPredicate.test(entry.get().asItem())) continue;
                if(CreateRegistrate.isInCreativeTab(entry, tabFilter)){
                    output.accept(entry.get(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                }
            }
            for (RegistryEntry<Item, Item> entry : CreateAndertech.REGISTRATE.getAll(Registries.ITEM)) {
                if(entry.get() instanceof BlockItem) continue;
                if(exclusionPredicate.test(entry.get())) continue;
                if(CreateRegistrate.isInCreativeTab(entry, tabFilter)){
                    output.accept(entry.get(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                }
            }
        }
    }
}
