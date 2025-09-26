package io.github.cvrunmin.create_andertech;

import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class AllCreativeTabs {
    private static final DeferredRegister<CreativeModeTab> REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateAndertech.MODID);

    public static final RegistryObject<CreativeModeTab> MAIN_TAB = REGISTER.register("main", () ->
        CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.create_andertech.main"))
                .withTabsBefore(AllCreativeModeTabs.PALETTES_CREATIVE_TAB.getId())
                .icon(AllBlocks.CREATIVE_OC_MOTOR::asStack)
                .displayItems(new MyDisplayitemGen(AllCreativeTabs.MAIN_TAB))
                .build()
    );

    public static void register(IEventBus modEventBus){
        REGISTER.register(modEventBus);
    }

    private static class MyDisplayitemGen implements CreativeModeTab.DisplayItemsGenerator{

        private final RegistryObject<CreativeModeTab> tabFilter;

        public MyDisplayitemGen(RegistryObject<CreativeModeTab> tab) {
            this.tabFilter = tab;
        }

        @Override
        public void accept(CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output) {
            for (RegistryEntry<Block> entry : CreateAndertech.REGISTRATE.getAll(Registries.BLOCK)) {
                if(CreateRegistrate.isInCreativeTab(entry, tabFilter)){
                    output.accept(entry.get());
                }
            }
            for (RegistryEntry<Item> entry : CreateAndertech.REGISTRATE.getAll(Registries.ITEM)) {
                if(entry.get() instanceof BlockItem) continue;
                if(CreateRegistrate.isInCreativeTab(entry, tabFilter)){
                    output.accept(entry.get());
                }
            }
        }
    }
}
