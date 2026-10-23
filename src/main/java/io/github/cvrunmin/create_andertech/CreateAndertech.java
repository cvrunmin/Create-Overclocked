package io.github.cvrunmin.create_andertech;

import com.mojang.logging.LogUtils;
import com.simibubi.create.api.boiler.BoilerHeater;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.api.contraption.BlockMovementChecks;
import com.simibubi.create.content.fluids.tank.BoilerHeaters;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.data.CreateRegistrate;
import io.github.cvrunmin.create_andertech.content.contraptions.bearing.ChronenditeMechanicalBearingBlock;
import io.github.cvrunmin.create_andertech.content.contraptions.bearing.ChronenditeMechanicalBearingBlockEntity;
import io.github.cvrunmin.create_andertech.content.fluids.infusion_vat.InfusionVatConnectivityHandler;
import io.github.cvrunmin.create_andertech.content.processing.burner.ChronendicBlazeBurnerBlock;
import io.github.cvrunmin.create_andertech.index.*;
import io.github.cvrunmin.create_andertech.infrastructure.data.CADataGen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
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
        CAAllArmInteractionPointTypes.register(modEventBus);
        CAAllSoundEvents.register(modEventBus);
        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ExampleMod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
//        NeoForge.EVENT_BUS.register(this);
        modEventBus.addListener(CreateAndertech::commonSetup);
        modEventBus.addListener(EventPriority.HIGHEST, CADataGen::gatherDataHighPriority);
        modEventBus.addListener(CADataGen::gatherData);
    }

    public static void commonSetup(final FMLCommonSetupEvent event){
        BoilerHeater.REGISTRY.register(AllBlocks.CHRONENDIC_BLAZE_BURNER.get(), ((level, pos, state) -> {
            BlazeBurnerBlock.HeatLevel value = state.getValue(ChronendicBlazeBurnerBlock.HEAT_LEVEL);
            if (value == BlazeBurnerBlock.HeatLevel.SEETHING) {
                return 2;
            }
            if (value.isAtLeast(BlazeBurnerBlock.HeatLevel.FADING)) {
                return 1;
            }
            return BoilerHeater.PASSIVE_HEAT;
        }));
        BlockMovementChecks.registerMovementAllowedCheck((state, world, pos) -> {
            if(state.getBlock() instanceof ChronenditeMechanicalBearingBlock){
                var be = world.getBlockEntity(pos);
                if(be instanceof ChronenditeMechanicalBearingBlockEntity bearing){
                    return BlockMovementChecks.CheckResult.of(!bearing.isRunning());
                }
            }
            return BlockMovementChecks.CheckResult.PASS;
        });
        BlockMovementChecks.registerAttachedCheck((state, world, pos, direction) -> {
            if(AllBlocks.CRIMSCHIST_HARVESTER.has(state)){
                return BlockMovementChecks.CheckResult.of(direction == Direction.UP);
            }
            if (AllBlocks.CRIMSCHIST_FLUID_TANK.has(state)) {
                return BlockMovementChecks.CheckResult.of(ConnectivityHandler.isConnected(world, pos, pos.relative(direction)));
            } else if (AllBlocks.INFUSION_VAT.has(state)) {
                return BlockMovementChecks.CheckResult.of(InfusionVatConnectivityHandler.isConnected(world, pos, pos.relative(direction)));
            }
            return BlockMovementChecks.CheckResult.PASS;
        });
        BlockMovementChecks.registerNotSupportiveCheck((state, direction) -> {
            if(AllBlocks.CHRONENDITE_MECHANICAL_DRILL.has(state)){
                return BlockMovementChecks.CheckResult.of(state.getValue(BlockStateProperties.FACING) == direction);
            }
            if(AllBlocks.CHRONENDITE_MECHANICAL_BEARING.has(state)){
                return BlockMovementChecks.CheckResult.of(state.getValue(BlockStateProperties.FACING) == direction);
            }
            if(AllBlocks.CHRONENDITE_MECHANICAL_SAW.has(state)){
                return BlockMovementChecks.CheckResult.of(state.getValue(BlockStateProperties.FACING) == direction);
            }
            if(AllBlocks.CRIMSCHIST_HARVESTER.has(state)){
                return BlockMovementChecks.CheckResult.of(direction == Direction.DOWN);
            }
            return BlockMovementChecks.CheckResult.PASS;
        });
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
