package io.github.cvrunmin.create_andertech;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import io.github.cvrunmin.create_andertech.index.*;
import io.github.cvrunmin.create_andertech.infrastructure.data.CADataGen;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.slf4j.Logger;

@Mod(CreateAndertech.MODID)
public class CreateAndertech {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "create_andertech";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID).defaultCreativeTab((ResourceKey<CreativeModeTab>) null);

    public CreateAndertech(IEventBus modEventBus, ModContainer modContainer){
        REGISTRATE.registerEventListeners(modEventBus);
        AllBlocks.register();
        AllItems.register();
        AllFluids.register();
        AllEntityTypes.register();
        AllBlockEntityTypes.register();
        CAAllRecipeTypes.register(modEventBus);
        AllDataComponents.register(modEventBus);
        AllCreativeTabs.register(modEventBus);
        CAAllParticleTypes.register(modEventBus);
        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ExampleMod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
//        NeoForge.EVENT_BUS.register(this);
        modEventBus.addListener(EventPriority.HIGHEST, CADataGen::gatherDataHighPriority);
        modEventBus.addListener(CADataGen::gatherData);
    }

    @Mod(value = CreateAndertech.MODID, dist = Dist.CLIENT)
    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class AndertechClient
    {
        public AndertechClient(ModContainer container){
            // Allows NeoForge to create a config screen for this mod's configs.
            // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
            // Do not forget to add translations for your config options to the en_us.json file.
            container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        }

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            CAAllPartialModels.init();
        }
    }
}
