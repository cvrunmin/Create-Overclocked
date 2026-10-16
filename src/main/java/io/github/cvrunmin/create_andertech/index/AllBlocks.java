package io.github.cvrunmin.create_andertech.index;

import com.simibubi.create.*;
import com.simibubi.create.AllTags;
import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.content.contraptions.bearing.StabilizedBearingMovementBehaviour;
import com.simibubi.create.content.decoration.encasing.CasingBlock;
import com.simibubi.create.content.decoration.encasing.EncasedCTBehaviour;
import com.simibubi.create.content.decoration.encasing.EncasingRegistry;
import com.simibubi.create.content.decoration.girder.ConnectedGirderModel;
import com.simibubi.create.content.fluids.PipeAttachmentModel;
import com.simibubi.create.content.fluids.tank.*;
import com.simibubi.create.content.kinetics.belt.BeltModel;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlock;
import com.simibubi.create.content.kinetics.deployer.DeployerMovementBehaviour;
import com.simibubi.create.content.kinetics.deployer.DeployerMovingInteraction;
import com.simibubi.create.content.kinetics.gearbox.GearboxBlock;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmItem;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockModel;
import com.simibubi.create.content.kinetics.simpleRelays.CogwheelBlockItem;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogCTBehaviour;
import com.simibubi.create.content.processing.AssemblyOperatorBlockItem;
import com.simibubi.create.foundation.data.*;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import io.github.cvrunmin.create_andertech.Constants;
import io.github.cvrunmin.create_andertech.CreateAndertech;
import io.github.cvrunmin.create_andertech.content.contraptions.actors.harvester.CrimschistHarvesterBlock;
import io.github.cvrunmin.create_andertech.content.contraptions.actors.harvester.CrimschistHarvesterMovementBehaviour;
import io.github.cvrunmin.create_andertech.content.contraptions.bearing.ChronenditeMechanicalBearingBlock;
import io.github.cvrunmin.create_andertech.content.decoration.CasingSlabBlock;
import io.github.cvrunmin.create_andertech.content.decoration.GirderEncasedCrimschistShaftBlock;
import io.github.cvrunmin.create_andertech.content.decoration.PoorNyliumBlock;
import io.github.cvrunmin.create_andertech.content.decoration.RichNyliumBlock;
import io.github.cvrunmin.create_andertech.content.fluids.infusion_vat.InfusionVatBlock;
import io.github.cvrunmin.create_andertech.content.fluids.infusion_vat.InfusionVatCTBehaviour;
import io.github.cvrunmin.create_andertech.content.fluids.infusion_vat.InfusionVatItem;
import io.github.cvrunmin.create_andertech.content.fluids.pump.CrimschistPumpBlock;
import io.github.cvrunmin.create_andertech.content.fluids.tank.CrimschistFluidTankBlock;
import io.github.cvrunmin.create_andertech.content.fluids.tank.CrimschistFluidTankItem;
import io.github.cvrunmin.create_andertech.content.kinetics.belt.OCBeltBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.chainDrive.CrimschistChainDriveBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.crafter.ChronenditeMechanicalCrafterBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.crafter.OCCrafterCTBehaviour;
import io.github.cvrunmin.create_andertech.content.kinetics.crusher.ChronenditeCrushWheelBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.crusher.ChronenditeCrushWheelControllerBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.deployer.ChronenditeDeployerBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.drill.ChronenditeDrillBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.drill.ChronenditeDrillMovementBehaviour;
import io.github.cvrunmin.create_andertech.content.kinetics.fan.ChronenditeEncasedFanBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.gearbox.CrimschistGearboxBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.mechanicalArm.ChronenditeArmBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.mixer.ChronenditeMechanicalMixerBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.motor.CreativeOCMotorBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.press.ChronenditeMechanicalPressBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.saw.ChronenditeSawBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.saw.ChronenditeSawMovementBehaviour;
import io.github.cvrunmin.create_andertech.content.kinetics.simpleRelays.CrimschistCogWheelBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.simpleRelays.CrimschistShaftBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.simpleRelays.encased.EncasedCrimschistCogwheelBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.simpleRelays.encased.EncasedCrimschistShaftBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.speedController.ChronenditeSpeedControllerBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.spout.ChronenditeMechanicalSpoutBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.transmission.CrimschistBlackboxDifferentialBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.transmission.CrimschistClutchBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.transmission.CrimschistGearshiftBlock;
import io.github.cvrunmin.create_andertech.content.misc.NyliumTransmuterBlock;
import io.github.cvrunmin.create_andertech.content.processing.burner.ChronendicBlazeBurnerMovementBehaviour;
import io.github.cvrunmin.create_andertech.content.processing.burner.ChronendicBlazeBurnerBlock;
import io.github.cvrunmin.create_andertech.foundation.AssetLookupWithParent;
import io.github.cvrunmin.create_andertech.foundation.BlockStateGenerators;
import io.github.cvrunmin.create_andertech.foundation.CullableCTModel;
import io.github.cvrunmin.create_andertech.foundation.VariousBuilders;
import net.createmod.catnip.data.Couple;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.Tags;

import java.util.Map;

import static com.simibubi.create.api.behaviour.display.DisplaySource.displaySource;
import static com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour.interactionBehaviour;
import static com.simibubi.create.api.behaviour.movement.MovementBehaviour.movementBehaviour;
import static com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageType.mountedFluidStorage;
import static com.simibubi.create.foundation.data.BlockStateGen.*;
import static com.simibubi.create.foundation.data.CreateRegistrate.casingConnectivity;
import static com.simibubi.create.foundation.data.CreateRegistrate.connectedTextures;
import static com.simibubi.create.foundation.data.TagGen.*;
import static io.github.cvrunmin.create_andertech.CreateAndertech.REGISTRATE;

public class AllBlocks {
    static {
        REGISTRATE.setCreativeTab(AllCreativeTabs.MAIN_TAB);
    }
    public static final BlockEntry<CrimschistShaftBlock> CRIMSCHIST_SHAFT = REGISTRATE.block("crimschist_shaft", CrimschistShaftBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.METAL).forceSolidOff())
            .transform(TagGen.pickaxeOnly())
            .blockstate(BlockStateGen.axisBlockProvider(false))
            .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 0))
            .onRegister(CreateRegistrate.blockModel(() -> BracketedKineticBlockModel::new))
            .simpleItem()
            .register();


    public static final BlockEntry<CrimschistCogWheelBlock> CRIMSCHIST_COGWHEEL = REGISTRATE.block("crimschist_cogwheel", CrimschistCogWheelBlock::small)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.sound(SoundType.WOOD)
                    .mapColor(MapColor.DIRT))
//            .transform(CStress.setNoImpact())
            .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 0))
            .transform(axeOrPickaxe())
            .blockstate(BlockStateGen.axisBlockProvider(false))
            .onRegister(CreateRegistrate.blockModel(() -> BracketedKineticBlockModel::new))
            .item(CogwheelBlockItem::new)
            .build()
            .register();

    public static final BlockEntry<CrimschistCogWheelBlock> CRIMSCHIST_LARGE_COGWHEEL =
            REGISTRATE.block("crimschist_large_cogwheel", CrimschistCogWheelBlock::large)
                    .initialProperties(SharedProperties::stone)
                    .properties(p -> p.sound(SoundType.WOOD)
                            .mapColor(MapColor.DIRT))
                    .transform(axeOrPickaxe())
//                    .transform(CStress.setNoImpact())
                    .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 0))
                    .blockstate(BlockStateGenerators.textureOverrideAxisBlockProvider(false, "create", "large_cogwheel", Map.ofEntries(
                        Map.entry("0", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_cogwheel_axis")),
                        Map.entry("3", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_axis_top")),
                        Map.entry("4", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/warped_large_cogwheel")),
                        Map.entry("particle", ResourceLocation.withDefaultNamespace("block/stripped_warped_stem")))
                        )
                    )
                    .onRegister(CreateRegistrate.blockModel(() -> BracketedKineticBlockModel::new))
                    .item(CogwheelBlockItem::new)
                    .build()
                    .register();

    public static final BlockEntry<EncasedCrimschistShaftBlock> ANDESITE_ENCASED_CRIMSCHIST_SHAFT =
            REGISTRATE.block("andesite_encased_crimschist_shaft", p -> new EncasedCrimschistShaftBlock(p, com.simibubi.create.AllBlocks.ANDESITE_CASING::get))
                    .properties(p -> p.mapColor(MapColor.PODZOL))
                    .transform(VariousBuilders.encasedCrimschistShaft("andesite", () -> AllSpriteShifts.ANDESITE_CASING))
                    .transform(EncasingRegistry.addVariantTo(AllBlocks.CRIMSCHIST_SHAFT))
                    .transform(axeOrPickaxe())
                    .register();

    public static final BlockEntry<EncasedCrimschistShaftBlock> BRASS_ENCASED_CRIMSCHIST_SHAFT =
            REGISTRATE.block("brass_encased_crimschist_shaft", p -> new EncasedCrimschistShaftBlock(p, com.simibubi.create.AllBlocks.BRASS_CASING::get))
                    .properties(p -> p.mapColor(MapColor.TERRACOTTA_BROWN))
                    .transform(VariousBuilders.encasedCrimschistShaft("brass", () -> AllSpriteShifts.BRASS_CASING))
                    .transform(EncasingRegistry.addVariantTo(AllBlocks.CRIMSCHIST_SHAFT))
                    .transform(axeOrPickaxe())
                    .register();

    public static final BlockEntry<EncasedCrimschistCogwheelBlock> ANDESITE_ENCASED_CRIMSCHIST_COGWHEEL = REGISTRATE
            .block("andesite_encased_crimschist_cogwheel", p -> new EncasedCrimschistCogwheelBlock(p, false, com.simibubi.create.AllBlocks.ANDESITE_CASING::get))
            .properties(p -> p.mapColor(MapColor.PODZOL))
            .transform(VariousBuilders.encasedCogwheel("andesite", () -> AllSpriteShifts.ANDESITE_CASING))
            .transform(EncasingRegistry.addVariantTo(AllBlocks.CRIMSCHIST_COGWHEEL))
            .onRegister(CreateRegistrate.connectedTextures(() -> new EncasedCogCTBehaviour(AllSpriteShifts.ANDESITE_CASING,
                    Couple.create(AllSpriteShifts.ANDESITE_ENCASED_COGWHEEL_SIDE,
                            AllSpriteShifts.ANDESITE_ENCASED_COGWHEEL_OTHERSIDE))))
            .transform(axeOrPickaxe())
            .register();

    public static final BlockEntry<EncasedCrimschistCogwheelBlock> BRASS_ENCASED_CRIMSCHIST_COGWHEEL =
            REGISTRATE.block("brass_encased_crimschist_cogwheel", p -> new EncasedCrimschistCogwheelBlock(p, false, com.simibubi.create.AllBlocks.BRASS_CASING::get))
                    .properties(p -> p.mapColor(MapColor.TERRACOTTA_BROWN))
                    .transform(VariousBuilders.encasedCogwheel("brass", () -> AllSpriteShifts.BRASS_CASING))
                    .transform(EncasingRegistry.addVariantTo(AllBlocks.CRIMSCHIST_COGWHEEL))
                    .onRegister(CreateRegistrate.connectedTextures(() -> new EncasedCogCTBehaviour(AllSpriteShifts.BRASS_CASING,
                            Couple.create(AllSpriteShifts.BRASS_ENCASED_COGWHEEL_SIDE,
                                    AllSpriteShifts.BRASS_ENCASED_COGWHEEL_OTHERSIDE))))
                    .transform(axeOrPickaxe())
                    .register();

    public static final BlockEntry<EncasedCrimschistCogwheelBlock> ANDESITE_ENCASED_CRIMSCHIST_LARGE_COGWHEEL = REGISTRATE
            .block("andesite_encased_crimschist_large_cogwheel",
                    p -> new EncasedCrimschistCogwheelBlock(p, true, com.simibubi.create.AllBlocks.ANDESITE_CASING::get))
            .properties(p -> p.mapColor(MapColor.PODZOL))
            .transform(VariousBuilders.encasedLargeCogwheel("andesite", () -> AllSpriteShifts.ANDESITE_CASING))
            .transform(EncasingRegistry.addVariantTo(AllBlocks.CRIMSCHIST_LARGE_COGWHEEL))
            .transform(axeOrPickaxe())
            .register();

    public static final BlockEntry<EncasedCrimschistCogwheelBlock> BRASS_ENCASED_CRIMSCHIST_LARGE_COGWHEEL = REGISTRATE
            .block("brass_encased_crimschist_large_cogwheel", p -> new EncasedCrimschistCogwheelBlock(p, true, com.simibubi.create.AllBlocks.BRASS_CASING::get))
            .properties(p -> p.mapColor(MapColor.TERRACOTTA_BROWN))
            .transform(VariousBuilders.encasedLargeCogwheel("brass", () -> AllSpriteShifts.BRASS_CASING))
            .transform(EncasingRegistry.addVariantTo(AllBlocks.CRIMSCHIST_LARGE_COGWHEEL))
            .transform(axeOrPickaxe())
            .register();

    public static final BlockEntry<CrimschistGearboxBlock> CRIMSCHIST_GEARBOX = REGISTRATE.block("crimschist_gearbox", CrimschistGearboxBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.noOcclusion()
                    .mapColor(MapColor.WARPED_NYLIUM))
            .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 0))
            .transform(axeOrPickaxe())
            .onRegister(CreateRegistrate.connectedTextures(() -> new EncasedCTBehaviour(CAAllSpriteShifts.CRIMSCHIST_CASING)))
            .onRegister(CreateRegistrate.casingConnectivity((block, cc) -> cc.make(block, CAAllSpriteShifts.CRIMSCHIST_CASING,
                    (s, f) -> f.getAxis() == s.getValue(GearboxBlock.AXIS))))
            .blockstate((c, p) -> axisBlock(c, p, $ -> AssetLookupWithParent.partialModel(c, p, "create", "gearbox", Map.ofEntries(
                    Map.entry("0", p.modLoc("block/crimschist_casing")),
                    Map.entry("1", p.modLoc("block/crimschist_gearbox")),
                    Map.entry("particle", p.modLoc("block/crimschist_casing"))
            )), true))
            .item()
            .transform(b -> b.model((c, p) -> {
                p.getBuilder("block/" + p.name(() -> c.getEntry().getBlock()) + "/item").parent(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath("create", "block/gearbox/item")))
                        .texture("0", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_casing"))
                        .texture("1", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_gearbox"))
                        .texture("particle", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_casing"))
                        .texture("1_0", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_axis"))
                        .texture("1_1", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_axis_top"));
                p.getBuilder("block/" + p.name(() -> c.getEntry().getBlock()) + "/item_vertical").parent(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath("create", "block/gearbox/item_vertical")))
                        .texture("gearbox_top", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_casing"))
                        .texture("gearbox", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_gearbox"))
                        .texture("particle", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_casing"))
                        .texture("0", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_axis"))
                        .texture("1", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_axis_top"));
                p.withExistingParent(c.getName(), p.modLoc("block/" + p.name(() -> c.getEntry().getBlock()) + "/item"));
            }))
            .build()
            .register();

    public static final BlockEntry<CrimschistClutchBlock> CRIMSCHIST_CLUTCH = REGISTRATE.block("crimschist_clutch", CrimschistClutchBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.noOcclusion()
                    .mapColor(MapColor.PODZOL))
            .addLayer(() -> RenderType::cutoutMipped)
            .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 0))
            .transform(axeOrPickaxe())
            .blockstate((c, p) -> BlockStateGen.axisBlock(c, p, AssetLookupWithParent.forPowered(c, p, "create", "clutch",
                    Map.ofEntries(
                            Map.entry("0", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_clutch_off")),
                            Map.entry("1", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_gearbox")),
                            Map.entry("2", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_funnel_frame")),
                            Map.entry("particle", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_clutch_off"))
                    ),
                    Map.ofEntries(
                            Map.entry("0", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_clutch_on")),
                            Map.entry("1", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_gearbox")),
                            Map.entry("2", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_funnel_frame")),
                            Map.entry("particle", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_clutch_off"))
                    )
            )))
            .item()
            .transform(tempCustomItemModel(ResourceLocation.fromNamespaceAndPath("create", "block/clutch/item"), Map.ofEntries(
                    Map.entry("0", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_clutch_off")),
                    Map.entry("particle", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_axis")),
                    Map.entry("1_0", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_axis")),
                    Map.entry("1_1", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_axis_top"))
            )))
            .register();

    public static final BlockEntry<CrimschistGearshiftBlock> CRIMSCHIST_GEARSHIFT = REGISTRATE.block("crimschist_gearshift", CrimschistGearshiftBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.noOcclusion()
                    .mapColor(MapColor.PODZOL))
            .addLayer(() -> RenderType::cutoutMipped)
            .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 0))
            .transform(axeOrPickaxe())
            .blockstate((c, p) -> BlockStateGen.axisBlock(c, p, AssetLookupWithParent.forPowered(c, p, "create", "gearshift",
                    Map.ofEntries(
                            Map.entry("0", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_gearshift_off")),
                            Map.entry("1", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_gearbox")),
                            Map.entry("2", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_funnel_frame")),
                            Map.entry("particle", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_gearshift_off"))
                    ),
                    Map.ofEntries(
                            Map.entry("0", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_gearshift_on")),
                            Map.entry("1", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_gearbox")),
                            Map.entry("2", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_funnel_frame")),
                            Map.entry("particle", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_gearshift_off"))
                    )
            )))
            .item()
            .transform(tempCustomItemModel(ResourceLocation.fromNamespaceAndPath("create", "block/gearshift/item"), Map.ofEntries(
                    Map.entry("0", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_gearshift_off")),
                    Map.entry("particle", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_axis")),
                    Map.entry("1_0", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_axis")),
                    Map.entry("1_1", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_axis_top"))
            )))
            .register();

    public static final BlockEntry<CrimschistBlackboxDifferentialBlock> CRIMSCHIST_BLACKBOX_DIFFERENTIAL =
            REGISTRATE.block("crimschist_blackbox_differential", CrimschistBlackboxDifferentialBlock::new)
                    .initialProperties(SharedProperties::stone)
                    .properties(p -> p.noOcclusion()
                            .mapColor(MapColor.PODZOL))
                    .addLayer(() -> RenderType::cutoutMipped)
                    .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 0))
                    .transform(axeOrPickaxe())
                    .onRegister(CreateRegistrate.connectedTextures(() -> new EncasedCTBehaviour(CAAllSpriteShifts.CRIMSCHIST_CASING)))
                    .onRegister(CreateRegistrate.casingConnectivity((block, cc) -> cc.make(block, CAAllSpriteShifts.CRIMSCHIST_CASING,
                            (s, f) -> f.getAxis() == s.getValue(CrimschistBlackboxDifferentialBlock.AXIS)
                                    && !CrimschistBlackboxDifferentialBlock.hasShaftOn(s, f))))
                    .blockstate(BlockStateGenerators.blackboxDifferential())
                    .item()
                    .model(AssetLookup::customItemModel)
                    .build()
                    .register();

    public static final BlockEntry<CrimschistChainDriveBlock> ENCASED_CRIMSCHIST_CHAIN_DRIVE =
            REGISTRATE.block("encased_crimschist_chain_drive", CrimschistChainDriveBlock::new)
                    .initialProperties(SharedProperties::stone)
                    .properties(p -> p.noOcclusion()
                            .mapColor(MapColor.PODZOL))
                    .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 0))
                    .transform(axeOrPickaxe())
                    .blockstate((c, p) -> new BlockStateGenerators.ChainDrive(
                            (state, suffix) -> {
                                var modelBuilder = p.models().getBuilder("block/" + c.getName() + "/" + suffix).parent(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath("create", "block/encased_chain_drive/" + suffix)))
                                        .texture("side", p.modLoc("block/encased_crimschist_chain_drive"))
                                        .texture("particle", p.modLoc("block/encased_crimschist_chain_drive"))
                                        ;
                                CrimschistChainDriveBlock.Part stateValue = state.getValue(CrimschistChainDriveBlock.PART);
                                if(stateValue == CrimschistChainDriveBlock.Part.NONE){
                                    modelBuilder.texture("1", p.modLoc("block/crimschist_gearbox"));
                                } else if (stateValue == CrimschistChainDriveBlock.Part.MIDDLE) {
                                    modelBuilder.texture("1", p.modLoc("block/crimschist_encased_chain_drive_middle"));
                                } else if (stateValue == CrimschistChainDriveBlock.Part.START || stateValue == CrimschistChainDriveBlock.Part.END) {
                                    modelBuilder.texture("2", p.modLoc("block/crimschist_encased_chain_drive_end"));
                                }
                                return modelBuilder;
                            }).generate(c, p))
                    .item()
                    .transform(tempCustomItemModel(ResourceLocation.fromNamespaceAndPath("create", "block/encased_chain_drive/item"), Map.ofEntries(
                            Map.entry("1_0", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_axis")),
                            Map.entry("1_1", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_axis_top")),
                            Map.entry("side", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/encased_crimschist_chain_drive")),
                            Map.entry("particle", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/encased_crimschist_chain_drive")),
                            Map.entry("1", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_gearbox"))
                    )))
                    .register();

    @SuppressWarnings("removal")
    public static final BlockEntry<OCBeltBlock> OC_BELT = REGISTRATE.block("speeding_belt", OCBeltBlock::new)
            .properties(p -> p.sound(SoundType.WOOL)
                    .strength(0.8f)
                    .mapColor(MapColor.COLOR_GRAY))
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(axeOrPickaxe())
            .blockstate(new BlockStateGenerators.Belt()::generate)
            .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 0))
            .transform(displaySource(AllDisplaySources.ITEM_NAMES))
            .onRegister(CreateRegistrate.blockModel(() -> BeltModel::new))
            .clientExtension(() -> OCBeltBlock.RenderProperties::new)
            .register();

    public static final BlockEntry<CreativeOCMotorBlock> CREATIVE_OC_MOTOR =
            REGISTRATE.block("creative_oc_motor", CreativeOCMotorBlock::new)
                    .initialProperties(SharedProperties::stone)
                    .lang("Creative Overclocking Motor")
                    .properties(p -> p.mapColor(MapColor.COLOR_PURPLE)
                            .forceSolidOn())
                    .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
                    .transform(pickaxeOnly())
                    .blockstate(new BlockStateGenerators.Motor()::generate)
                    .onRegister(BlockStressValues.setGeneratorSpeed(Constants.MAX_OVERCLOCKING_SPEED, true))
                    .onRegister(block -> BlockStressValues.CAPACITIES.register(block, () -> 131072.0))
                    .item()
                    .properties(p -> p.rarity(Rarity.EPIC))
                    .transform(tempCustomItemModel(ResourceLocation.fromNamespaceAndPath("create", "block/creative_motor/item"), Map.ofEntries(
                            Map.entry("1_0", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_axis")),
                            Map.entry("1_1", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_axis_top"))
                    )))
                    .register();

    public static final BlockEntry<ChronenditeEncasedFanBlock> CHRONENDITE_ENCASED_FAN = REGISTRATE.block("chronendite_encased_fan", prop -> new ChronenditeEncasedFanBlock(prop, false))
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.SAND))
            .blockstate(BlockStateGenerators.textureOverrideDirectionalBlockProvider(true, "create", "encased_fan", Map.ofEntries(
                    Map.entry("3", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_funnel_frame")),
                    Map.entry("back", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_gearbox")),
                    Map.entry("fan_side", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_fan_side")),
                    Map.entry("particle", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_fan_side"))
            )))
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(axeOrPickaxe())
            .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 2.0))
            .item()
            .transform(tempCustomItemModel(ResourceLocation.fromNamespaceAndPath("create", "block/encased_fan/item"), Map.ofEntries(
                    Map.entry("axis", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_axis")),
                    Map.entry("axis_top", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_axis_top")),
                    Map.entry("2", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_gearbox")),
                    Map.entry("fan_side", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_fan_side")),
                    Map.entry("particle", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_fan_side"))
            )))
            .register();

    public static final BlockEntry<ChronenditeEncasedFanBlock> CHRONENDITE_HV_ENCASED_FAN = REGISTRATE.block("chronendite_hv_encased_fan", prop -> new ChronenditeEncasedFanBlock(prop, true))
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.SAND))
            .lang("Chronendite High Volume Encased Fan")
            .blockstate(BlockStateGenerators.textureOverrideDirectionalBlockProvider(true, "create", "encased_fan", Map.ofEntries(
                    Map.entry("3", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_funnel_frame")),
                    Map.entry("back", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_gearbox")),
                    Map.entry("fan_side", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_fan_side_hv")),
                    Map.entry("particle", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_fan_side_hv"))
            )))
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(axeOrPickaxe())
            .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 2.0))
            .item()
            .transform(tempCustomItemModel(ResourceLocation.fromNamespaceAndPath("create", "block/encased_fan/item"), Map.ofEntries(
                    Map.entry("axis", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_axis")),
                    Map.entry("axis_top", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_axis_top")),
                    Map.entry("2", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_gearbox")),
                    Map.entry("fan_side", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_fan_side_hv")),
                    Map.entry("particle", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_fan_side_hv"))
            )))
            .register();

    public static final BlockEntry<ChronenditeCrushWheelBlock> CHRONENDITE_CRUSHING_WHEEL =
            REGISTRATE.block("chronendite_crushing_wheel", ChronenditeCrushWheelBlock::new)
                    .properties(p -> p.mapColor(MapColor.METAL))
                    .initialProperties(SharedProperties::stone)
                    .properties(BlockBehaviour.Properties::noOcclusion)
                    .transform(pickaxeOnly())
                    .blockstate(BlockStateGenerators.textureOverrideAxisBlockProvider(true, "create", "crushing_wheel", Map.ofEntries(
                            Map.entry("axis", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_axis")),
                            Map.entry("axis_top", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_axis_top")),
                            Map.entry("insert", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_crushing_wheel_insert")),
                            Map.entry("plates", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_crushing_wheel_plates")),
                            Map.entry("spruce_log_top", ResourceLocation.withDefaultNamespace("block/birch_log_top"))
                    )))
                    .addLayer(() -> RenderType::cutoutMipped)
                    .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 8.0))
                    .item()
                    .transform(tempCustomItemModel(ResourceLocation.fromNamespaceAndPath("create", "block/crushing_wheel/item"), Map.ofEntries(
                            Map.entry("axis", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_axis")),
                            Map.entry("axis_top", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_axis_top")),
                            Map.entry("insert", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_crushing_wheel_insert")),
                            Map.entry("plates", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_crushing_wheel_plates")),
                            Map.entry("spruce_log_top", ResourceLocation.withDefaultNamespace("block/birch_log_top"))
                    )))
                    .register();

    public static final BlockEntry<ChronenditeCrushWheelControllerBlock> CHRONENDITE_CRUSHING_WHEEL_CONTROLLER =
            REGISTRATE.block("chronendite_crushing_wheel_controller", ChronenditeCrushWheelControllerBlock::new)
                    .properties(p -> p.mapColor(MapColor.STONE)
                            .noOcclusion()
                            .noLootTable()
                            .air()
                            .noCollission()
                            .pushReaction(PushReaction.BLOCK))
                    .blockstate((c, p) -> p.getVariantBuilder(c.get())
                            .forAllStatesExcept(BlockStateGen.mapToAir(p), CrushingWheelControllerBlock.FACING))
                    .register();

    public static final BlockEntry<ChronenditeMechanicalPressBlock> CHRONENDITE_MECHANICAL_PRESS =
            REGISTRATE.block("chronendite_mechanical_press", ChronenditeMechanicalPressBlock::new)
                    .initialProperties(SharedProperties::stone)
                    .properties(p -> p.noOcclusion()
                            .mapColor(MapColor.SAND))
                    .transform(axeOrPickaxe())
                    .blockstate(BlockStateGenerators.textureOverrideHorizontalBlockProvider(true, "create", "mechanical_press", Map.ofEntries(
                        Map.entry("4", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_mechanical_press_side")),
                        Map.entry("particle", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_mechanical_press_side")),
                        Map.entry("gearbox_top", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_gearbox_top")),
                        Map.entry("gearbox", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_gearbox")),
                        Map.entry("mechanical_press_top", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_mechanical_press_top")),
                        Map.entry("mechanical_press_bottom", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_mechanical_press_bottom"))
                    )))
                    .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 8))
                    .item(AssemblyOperatorBlockItem::new)
                    .transform(tempCustomItemModel(ResourceLocation.fromNamespaceAndPath("create", "block/mechanical_press/item"), Map.ofEntries(
                            Map.entry("8", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_mechanical_press_side")),
                            Map.entry("particle", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_mechanical_press_side")),
                            Map.entry("gearbox_top", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_gearbox_top")),
                            Map.entry("gearbox", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_gearbox")),
                            Map.entry("mechanical_press_top", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_mechanical_press_top")),
                            Map.entry("mechanical_press_bottom", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_mechanical_press_bottom")),
                            Map.entry("0", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_axis")),
                            Map.entry("1", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_axis_top"))
                    )))
                    .register();

    @SuppressWarnings("removal")
    public static final BlockEntry<ChronenditeMechanicalMixerBlock> CHRONENDITE_MECHANICAL_MIXER =
            REGISTRATE.block("chronendite_mechanical_mixer", ChronenditeMechanicalMixerBlock::new)
                    .initialProperties(SharedProperties::stone)
                    .properties(p -> p.noOcclusion()
                            .mapColor(MapColor.SAND))
                    .transform(axeOrPickaxe())
                    .blockstate((c, p) -> p.simpleBlock(c.getEntry(), p.models()
                            .getBuilder("block/" + c.getName() + "/block")
                            .parent(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(Create.ID, "block/mechanical_mixer/block")))
                            .texture("2", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/smooth_birch_log_top"))
                            .texture("4", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_mixer_base_side"))
                            .texture("11", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_mechanical_press_top"))
                            .texture("particle", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_mixer_base_side"))
                    ))
                    .addLayer(() -> RenderType::cutoutMipped)
                    .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 4))
                    .item(AssemblyOperatorBlockItem::new)
                    .transform(tempCustomItemModel(ResourceLocation.fromNamespaceAndPath("create", "block/mechanical_mixer/item"), Map.ofEntries(
                            Map.entry("1_2", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/warped_cogwheel")),
                            Map.entry("2", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/smooth_birch_log_top")),
                            Map.entry("4", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_mixer_base_side")),
                            Map.entry("11", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_mechanical_press_top")),
                            Map.entry("particle", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_mixer_base_side"))
                    )))
                    .register();


    public static final BlockEntry<ChronenditeDrillBlock> CHRONENDITE_MECHANICAL_DRILL = REGISTRATE.block("chronendite_mechanical_drill", ChronenditeDrillBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.SAND))
            .transform(axeOrPickaxe())
            .blockstate(BlockStateGenerators.textureOverrideDirectionalBlockProvider(true, "create", "mechanical_drill", Map.ofEntries(
                Map.entry("10", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_casing_very_short")),
                Map.entry("gearbox_top", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_gearbox_top")),
                Map.entry("particle", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_gearbox")),
                Map.entry("gearbox", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_gearbox"))
            )))
            .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 4))
            .onRegister(movementBehaviour(new ChronenditeDrillMovementBehaviour()))
            .item()
            .tag(AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag)
            .transform(tempCustomItemModel(ResourceLocation.fromNamespaceAndPath("create", "block/mechanical_drill/item"), Map.ofEntries(
                    Map.entry("0", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_axis_top")),
                    Map.entry("1", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_axis")),
                    Map.entry("10", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_casing_very_short")),
                    Map.entry("gearbox", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_gearbox"))
            )))
            .register();

    @SuppressWarnings("removal")
    public static final BlockEntry<ChronenditeSawBlock> CHRONENDITE_MECHANICAL_SAW = REGISTRATE.block("chronendite_mechanical_saw", ChronenditeSawBlock::new)
            .initialProperties(SharedProperties::stone)
            .addLayer(() -> RenderType::cutoutMipped)
            .properties(p -> p.mapColor(MapColor.SAND))
            .transform(axeOrPickaxe())
            .blockstate(new BlockStateGenerators.Saw()::generate)
            .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 4))
            .onRegister(movementBehaviour(new ChronenditeSawMovementBehaviour()))
            .addLayer(() -> RenderType::cutoutMipped)
            .item()
            .tag(AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag)
            .transform(tempCustomItemModel(ResourceLocation.fromNamespaceAndPath("create", "block/mechanical_saw/item"), Map.ofEntries(
                    Map.entry("slit", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_mechanical_saw_top_no_slot")),
                    Map.entry("particle", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_gearbox_top")),
                    Map.entry("gearbox_top", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_gearbox_top")),
                    Map.entry("encased_belt", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_encased_chain_drive")),
                    Map.entry("gearbox", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_gearbox")),
                    Map.entry("andesite_casing_short", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_casing_short"))
            )))
            .register();


    public static final BlockEntry<ChronenditeDeployerBlock> CHRONENDITE_DEPLOYER = REGISTRATE.block("chronendite_deployer", ChronenditeDeployerBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.SAND))
            .transform(axeOrPickaxe())
            .blockstate(BlockStateGenerators.textureOverrideDirectionalAxisBlockProvider("create", "deployer", Map.ofEntries(
                    Map.entry("5", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_casing_piston")),
                    Map.entry("6", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_piston_bottom")),
                    Map.entry("7", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_piston_inner")),
                    Map.entry("particle", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_gearbox_top")),
                    Map.entry("gearbox_top", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_gearbox_top")),
                    Map.entry("gearbox", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_gearbox")),
                    Map.entry("andesite_casing_short", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_casing_short"))
            )))
            .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 4.0))
            .onRegister(movementBehaviour(new DeployerMovementBehaviour()))
            .onRegister(interactionBehaviour(new DeployerMovingInteraction()))
            .item(AssemblyOperatorBlockItem::new)
            .tag(AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag)
            .transform(tempCustomItemModel(ResourceLocation.fromNamespaceAndPath("create", "block/deployer/item"), Map.ofEntries(
                    Map.entry("0", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_axis_top")),
                    Map.entry("1", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_axis")),
                    Map.entry("6", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_piston_bottom")),
                    Map.entry("7", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_piston_inner")),
                    Map.entry("10", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_casing_piston")),
                    Map.entry("particle", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_axis")),
                    Map.entry("gearbox_top", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_gearbox_top")),
                    Map.entry("gearbox", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_gearbox")),
                    Map.entry("andesite_casing_short", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_casing_short"))

            )))
            .register();

    public static final BlockEntry<CrimschistFluidTankBlock> CRIMSCHIST_FLUID_TANK = REGISTRATE.block("crimschist_fluid_tank", CrimschistFluidTankBlock::new)
            .properties(p -> p.mapColor(MapColor.WARPED_NYLIUM).requiresCorrectToolForDrops().strength(3.0F, 6.0F).noOcclusion()
                    .isRedstoneConductor((p1, p2, p3) -> true))
            .transform(pickaxeOnly())
            .blockstate(new BlockStateGenerators.FluidTank()::generate)
            .onRegister(CreateRegistrate.blockModel(() -> ((bakedModel) -> new CullableCTModel(bakedModel, new FluidTankCTBehaviour(CAAllSpriteShifts.CRIMSCHIST_FLUID_TANK, CAAllSpriteShifts.CRIMSCHIST_FLUID_TANK_TOP, CAAllSpriteShifts.CRIMSCHIST_FLUID_TANK_INNER)))))
            .transform(displaySource(AllDisplaySources.BOILER))
            .transform(mountedFluidStorage(AllMountedStorageTypes.FLUID_TANK))
            .onRegister(movementBehaviour(new FluidTankMovementBehavior()))
            .addLayer(() -> RenderType::cutoutMipped)
            .item(CrimschistFluidTankItem::new)
            .model(AssetLookup.customBlockItemModel("_", "block_single_window"))
            .build()
            .register();

    public static final BlockEntry<ChronenditeMechanicalBearingBlock> CHRONENDITE_MECHANICAL_BEARING =
            REGISTRATE.block("chronendite_mechanical_bearing", ChronenditeMechanicalBearingBlock::new)
                    .properties(p -> p.mapColor(MapColor.SAND))
                    .transform(axeOrPickaxe())
                    .transform(VariousBuilders.chronenditeBearing("chronendite_mechanical", "chronendite_gearbox"))
                    .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 4))
                    .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
                    .onRegister(movementBehaviour(new StabilizedBearingMovementBehaviour()))
                    .register();

    public static final BlockEntry<ChronenditeMechanicalSpoutBlock> CHRONENDITE_MECHANICAL_SPOUT =
            REGISTRATE.block("chronendite_mechanical_spout", ChronenditeMechanicalSpoutBlock::new)
                    .initialProperties(SharedProperties::stone)
                    .properties(p -> p.mapColor(MapColor.SAND))
                    .transform(axeOrPickaxe())
                    .blockstate(horizontalBlockProvider(true))
                    .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 4))
                    .item(AssemblyOperatorBlockItem::new)
                    .transform(ModelGen.customItemModel())
                    .register();

    public static final BlockEntry<ChronenditeSpeedControllerBlock> CHRONENDITE_ROTATION_SPEED_CONTROLLER =
        REGISTRATE.block("chronendite_rotation_speed_controller", ChronenditeSpeedControllerBlock::new)
                .initialProperties(SharedProperties::softMetal)
                .properties(p -> p.mapColor(MapColor.TERRACOTTA_PURPLE).noOcclusion())
                .transform(axeOrPickaxe())
                .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
                .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 0))
                .blockstate(new BlockStateGenerators.SpeedController()::generate)
                .item()
                .transform(tempCustomItemModel(ResourceLocation.fromNamespaceAndPath("create", "block/rotation_speed_controller/item"),Map.ofEntries(
                        Map.entry("0", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_axis")),
                        Map.entry("3", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_axis_top")),
                        Map.entry("4", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_gearbox")),
                        Map.entry("7", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_funnel_neutral")),
                        Map.entry("9", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_block")),
                        Map.entry("particle", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_gearbox")),
                        Map.entry("1_0", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_rotation_speed_controller")),
                        Map.entry("1_5", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_rotation_speed_controller_top"))
                )))
                .register();

    public static final BlockEntry<ChronenditeMechanicalCrafterBlock> CHRONENDITE_MECHANICAL_CRAFTER =
            REGISTRATE.block("chronendite_mechanical_crafter", ChronenditeMechanicalCrafterBlock::new)
                    .initialProperties(SharedProperties::softMetal)
                    .properties(p -> p.noOcclusion()
                            .mapColor(MapColor.SAND))
                    .transform(axeOrPickaxe())
                    .blockstate(BlockStateGenerators.textureOverrideHorizontalBlockProvider(true, "create", "mechanical_crafter", Map.ofEntries(
                            Map.entry("4", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_crafter_side")),
                            Map.entry("5", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_casing")),
                            Map.entry("7", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_block")),
                            Map.entry("8", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/smooth_birch_log_top")),
                            Map.entry("particle", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_casing"))
                    )))
                    .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 2.0))
                    .onRegister(CreateRegistrate.connectedTextures(OCCrafterCTBehaviour::new))
                    .addLayer(() -> RenderType::cutoutMipped)
                    .item()
                    .transform(tempCustomItemModel(ResourceLocation.fromNamespaceAndPath("create", "block/mechanical_crafter/item"), Map.ofEntries(
                            Map.entry("4", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_crafter_side")),
                            Map.entry("5", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_casing")),
                            Map.entry("7", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_block")),
                            Map.entry("8", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/smooth_birch_log_top")),
                            Map.entry("9", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/warped_cogwheel")),
                            Map.entry("particle", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_casing"))
                    )))
                    .register();


    public static final BlockEntry<ChronenditeArmBlock> CHRONENDITE_MECHANICAL_ARM = REGISTRATE.block("chronendite_mechanical_arm", ChronenditeArmBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.TERRACOTTA_PURPLE))
            .transform(axeOrPickaxe())
            .blockstate((c, p) -> p.getVariantBuilder(c.get())
                    .forAllStates(s -> ConfiguredModel.builder()
                            .modelFile(AssetLookupWithParent.partialModel(c, p, "create", "mechanical_arm", Map.ofEntries(
                                    Map.entry("3", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_mechanical_arm")),
                                    Map.entry("7", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_block")),
                                    Map.entry("particle", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_casing"))
                            )))
                            .rotationX(s.getValue(ChronenditeArmBlock.CEILING) ? 180 : 0)
                            .build()))
            .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 2.0))
            .item(ArmItem::new)
            .transform(tempCustomItemModel(ResourceLocation.fromNamespaceAndPath("create", "block/mechanical_arm/item"), Map.ofEntries(
                    Map.entry("4", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_flywheel")),
                    Map.entry("5", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_mechanical_arm")),
                    Map.entry("7", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_block")),
                    Map.entry("8", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_casing")),
                    Map.entry("particle", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_casing"))
            )))
            .register();

    public static final BlockEntry<CrimschistPumpBlock> CRIMSCHIST_MECHANICAL_PUMP = REGISTRATE.block("crimschist_mechanical_pump", CrimschistPumpBlock::new)
            .properties(p -> p.mapColor(MapColor.WARPED_NYLIUM).requiresCorrectToolForDrops().strength(3.0F, 6.0F).sound(SoundType.METAL))
            .transform(pickaxeOnly())
            .blockstate(BlockStateGen.directionalAxisBlockProvider())
            .onRegister(CreateRegistrate.blockModel(() -> PipeAttachmentModel::withAO))
            .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 4.0))
            .item()
            .transform(ModelGen.customItemModel())
            .register();

    public static final BlockEntry<InfusionVatBlock> INFUSION_VAT = REGISTRATE.block("crimschist_infusion_vat", InfusionVatBlock::new)
            .properties(p -> p.mapColor(MapColor.WARPED_NYLIUM).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.METAL).strength(3.f, 6.f))
            .transform(pickaxeOnly())
            .blockstate(new BlockStateGenerators.InfusionVat()::generate)
            .onRegister(CreateRegistrate.blockModel(() -> ((bakedModel) -> new CullableCTModel(bakedModel, new InfusionVatCTBehaviour(CAAllSpriteShifts.CRIMSCHIST_FLUID_TANK, CAAllSpriteShifts.CRIMSCHIST_FLUID_TANK_TOP, CAAllSpriteShifts.CRIMSCHIST_FLUID_TANK_INNER)))))
            .addLayer(() -> RenderType::cutoutMipped)
            .item(InfusionVatItem::new)
            .model(AssetLookup.customBlockItemModel("_", "block_single"))
            .build()
            .register();

    public static final BlockEntry<ChronendicBlazeBurnerBlock> CHRONENDIC_BLAZE_BURNER = REGISTRATE.block("chronendic_blaze_burner", ChronendicBlazeBurnerBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .lightLevel(ChronendicBlazeBurnerBlock::getLight))
            .transform(pickaxeOnly())
            .addLayer(() -> RenderType::cutoutMipped)
            .tag(AllTags.AllBlockTags.FAN_PROCESSING_CATALYSTS_BLASTING.tag,
                    AllTags.AllBlockTags.FAN_TRANSPARENT.tag, AllTags.AllBlockTags.PASSIVE_BOILER_HEATERS.tag)
            .blockstate((c, p) -> p.simpleBlock(c.getEntry(), AssetLookup.partialBaseModel(c, p)))
            .onRegister(movementBehaviour(new ChronendicBlazeBurnerMovementBehaviour()))
            .item()
            .model(AssetLookup.customBlockItemModel("chronendic_blaze_burner", "block_with_blaze"))
            .build().register();

    public static final BlockEntry<CrimschistHarvesterBlock> CRIMSCHIST_HARVESTER = REGISTRATE.block("crimschist_mechanical_harvester", CrimschistHarvesterBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.WARPED_NYLIUM)
                    .forceSolidOn())
            .transform(axeOrPickaxe())
            .onRegister(movementBehaviour(new CrimschistHarvesterMovementBehaviour()))
            .blockstate((c, p) ->
                    p.getVariantBuilder(c.getEntry()).partialState()
                            .with(CrimschistHarvesterBlock.ATTACHED_FACE, Direction.UP)
                                    .setModels(new ConfiguredModel(AssetLookup.partialBaseModel(c, p)))
                            .partialState()
                            .with(CrimschistHarvesterBlock.ATTACHED_FACE, Direction.DOWN)
                            .setModels(new ConfiguredModel(AssetLookup.partialBaseModel(c, p, "up")))
            )
            .addLayer(() -> RenderType::cutoutMipped)
            .item()
            .tag(AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag)
            .transform(ModelGen.customItemModel())
            .register();

    public static final BlockEntry<CasingBlock> CRIMSCHIST_CASING = REGISTRATE.block("crimschist_casing", CasingBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.WARPED_NYLIUM).sound(SoundType.WOOD))
            .transform(axeOrPickaxe())
            .blockstate((c, p) -> p.simpleBlock(c.get()))
            .onRegister(connectedTextures(() -> new EncasedCTBehaviour(CAAllSpriteShifts.CRIMSCHIST_CASING)))
            .onRegister(casingConnectivity((block, cc) -> cc.makeCasing(block, CAAllSpriteShifts.CRIMSCHIST_CASING)))
            .tag(AllTags.AllBlockTags.CASING.tag)
            .item()
            .tag(AllTags.AllItemTags.CASING.tag)
            .build()
            .register();


    public static final BlockEntry<CasingBlock> CHRONENDITE_CASING = REGISTRATE.block("chronendite_casing", CasingBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.SAND).sound(SoundType.WOOD))
            .transform(axeOrPickaxe())
            .blockstate((c, p) -> p.simpleBlock(c.get()))
            .onRegister(connectedTextures(() -> new EncasedCTBehaviour(CAAllSpriteShifts.CHRONENDITE_CASING)))
            .onRegister(casingConnectivity((block, cc) -> cc.makeCasing(block, CAAllSpriteShifts.CHRONENDITE_CASING)))
            .tag(AllTags.AllBlockTags.CASING.tag)
            .item()
            .tag(AllTags.AllItemTags.CASING.tag)
            .build()
            .register();

    public static final BlockEntry<CasingSlabBlock> CRIMSCHIST_CASING_SLAB = REGISTRATE.block("crimschist_casing_slab", CasingSlabBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.WARPED_NYLIUM).sound(SoundType.WOOD))
            .transform(axeOrPickaxe())
            .loot(((lootTables, b) -> lootTables.add(b, lootTables.createSlabItemTable(b))))
            .blockstate((c, p) -> p.slabBlock(c.get(), p.modLoc("block/crimschist_casing"), p.modLoc("block/crimschist_casing")))
            .onRegister(connectedTextures(() -> new EncasedCTBehaviour(CAAllSpriteShifts.CRIMSCHIST_CASING)))
            .onRegister(casingConnectivity((block, cc) -> cc.makeCasing(block, CAAllSpriteShifts.CRIMSCHIST_CASING)))
            .tag(BlockTags.SLABS)
            .item()
            .tag(ItemTags.SLABS)
            .build()
            .register();

    public static final BlockEntry<GirderEncasedCrimschistShaftBlock> METAL_GIRDER_ENCASED_CRIMSCHIST_SHAFT =
            REGISTRATE.block("metal_girder_encased_crimschist_shaft", GirderEncasedCrimschistShaftBlock::new)
                    .initialProperties(SharedProperties::softMetal)
                    .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                            .sound(SoundType.NETHERITE_BLOCK))
                    .transform(pickaxeOnly())
                    .blockstate(BlockStateGenerators::griderBlockStateWithShaft)
                    .loot((p, b) -> p.add(b, p.createSingleItemTable(com.simibubi.create.AllBlocks.METAL_GIRDER.get())
                            .withPool(p.applyExplosionCondition(CRIMSCHIST_SHAFT.get(), LootPool.lootPool()
                                    .setRolls(ConstantValue.exactly(1.0F))
                                    .add(LootItem.lootTableItem(CRIMSCHIST_SHAFT.get()))))))
                    .onRegister(CreateRegistrate.blockModel(() -> ConnectedGirderModel::new))
                    .register();

    public static final BlockEntry<NyliumTransmuterBlock> WARPED_NYLIUM_TRANSMUTER =
            REGISTRATE.block("warped_nylium_transmuter", NyliumTransmuterBlock::new)
                    .properties(p -> p.mapColor(MapColor.WARPED_NYLIUM).instabreak().sound(SoundType.GRASS).ignitedByLava().isRedstoneConductor(((state, level, pos) -> false)))
                    .tag(io.github.cvrunmin.create_andertech.index.AllTags.EXPLOSIVE_TO_TRANSMUTER)
                    .blockstate((ctx, p) ->
                            p.simpleBlock(ctx.get(), p.models()
                                    .withExistingParent(ctx.getName(), p.modLoc("block/nylium_transmuter"))
                                    .texture("texture", p.modLoc("block/wrapped_transmuter"))
                            )
                    )
                    .simpleItem()
                    .register();

    public static final BlockEntry<NyliumTransmuterBlock> CRIMSON_NYLIUM_TRANSMUTER =
            REGISTRATE.block("crimson_nylium_transmuter", NyliumTransmuterBlock::new)
                    .properties(p -> p.mapColor(MapColor.WARPED_NYLIUM).instabreak().sound(SoundType.GRASS).ignitedByLava().isRedstoneConductor(((state, level, pos) -> false)))
                    .tag(io.github.cvrunmin.create_andertech.index.AllTags.EXPLOSIVE_TO_TRANSMUTER)
                    .blockstate((ctx, p) ->
                            p.simpleBlock(ctx.get(), p.models()
                                    .withExistingParent(ctx.getName(), p.modLoc("block/nylium_transmuter"))
                                    .texture("texture", p.modLoc("block/crimson_transmuter"))
                            )
                    )
                    .simpleItem()
                    .register();

    public static final BlockEntry<Block> CRIMSCHIST_ALLOY_BLOCK = REGISTRATE.block("crimschist_alloy_block", Block::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.WARPED_NYLIUM).sound(SoundType.METAL).requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(simpleCubeAll("crimschist_block"))
            .tag(Tags.Blocks.STORAGE_BLOCKS)
            .transform(tagBlockAndItem(io.github.cvrunmin.create_andertech.index.AllTags.CRIMSCHIST_ALLOY_STORAGE, io.github.cvrunmin.create_andertech.index.AllTags.CRIMSCHIST_ALLOY_STORAGE_ITEM))
            .tag(Tags.Items.STORAGE_BLOCKS)
            .build()
            .lang("Block of Crimschist Alloy")
            .register();

    public static final BlockEntry<Block> CHRONENDITE_BLOCK = REGISTRATE.block("chronendite_block", Block::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.SAND).sound(SoundType.METAL).requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(simpleCubeAll("chronendite_block"))
            .tag(Tags.Blocks.STORAGE_BLOCKS)
            .transform(tagBlockAndItem(io.github.cvrunmin.create_andertech.index.AllTags.CHRONENDITE_STORAGE, io.github.cvrunmin.create_andertech.index.AllTags.CHRONENDITE_STORAGE_ITEM))
            .tag(Tags.Items.STORAGE_BLOCKS)
            .build()
            .lang("Block of Chronendite")
            .register();

    public static final BlockEntry<RichNyliumBlock> RICH_WARPED_NYLIUM = REGISTRATE.block("rich_warped_nylium", RichNyliumBlock::new)
            .initialProperties(() -> Blocks.WARPED_NYLIUM)
            .tag(BlockTags.NYLIUM, BlockTags.MUSHROOM_GROW_BLOCK)
            .loot((tables, block) -> tables.add(block, tables.createSingleItemTableWithSilkTouch(block, Blocks.NETHERRACK)))
            .transform(pickaxeOnly())
            .blockstate((ctx, p) ->
                    p.simpleBlock(ctx.get(), p.models().cubeBottomTop(ctx.getName(),
                            p.modLoc("block/rich_warped_nylium_side"),
                            p.mcLoc("block/netherrack"),
                            p.modLoc("block/rich_warped_nylium")))
            )
            .simpleItem()
            .register();

    public static final BlockEntry<RichNyliumBlock> UNTOPPED_WARPED_NYLIUM = REGISTRATE.block("untopped_warped_nylium", RichNyliumBlock::new)
            .initialProperties(() -> Blocks.NETHERRACK)
            .properties(prop -> prop.sound(SoundType.NYLIUM))
            .tag(BlockTags.NYLIUM, BlockTags.MUSHROOM_GROW_BLOCK, io.github.cvrunmin.create_andertech.index.AllTags.UNTOPPED_NYLIUM)
            .loot((tables, block) -> tables.add(block, tables.createSingleItemTableWithSilkTouch(block, Blocks.NETHERRACK)))
            .transform(pickaxeOnly())
            .blockstate((ctx, p) ->
                    p.simpleBlock(ctx.get(), p.models().cubeBottomTop(ctx.getName(),
                            p.modLoc("block/untopped_warped_nylium_side"),
                            p.mcLoc("block/netherrack"),
                            p.modLoc("block/untopped_warped_nylium")))
            )
            .simpleItem()
            .register();

    public static final BlockEntry<PoorNyliumBlock> POOR_WARPED_NYLIUM = REGISTRATE.block("poor_warped_nylium", PoorNyliumBlock::new)
            .initialProperties(() -> Blocks.NETHERRACK)
            .properties(prop -> prop.sound(SoundType.NYLIUM).randomTicks())
            .tag(BlockTags.NYLIUM, BlockTags.MUSHROOM_GROW_BLOCK)
            .loot((tables, block) -> tables.add(block, tables.createSingleItemTableWithSilkTouch(block, Blocks.NETHERRACK)))
            .transform(pickaxeOnly())
            .blockstate((ctx, p) ->
                    p.simpleBlock(ctx.get(), p.models().cubeBottomTop(ctx.getName(),
                            p.modLoc("block/poor_warped_nylium_side"),
                            p.mcLoc("block/netherrack"),
                            p.modLoc("block/poor_warped_nylium")))
            )
            .simpleItem()
            .register();

    public static final BlockEntry<RichNyliumBlock> RICH_CRIMSON_NYLIUM = REGISTRATE.block("rich_crimson_nylium", RichNyliumBlock::new)
            .initialProperties(() -> Blocks.CRIMSON_NYLIUM)
            .tag(BlockTags.NYLIUM, BlockTags.MUSHROOM_GROW_BLOCK)
            .loot((tables, block) -> tables.add(block, tables.createSingleItemTableWithSilkTouch(block, Blocks.NETHERRACK)))
            .transform(pickaxeOnly())
            .blockstate((ctx, p) ->
                    p.simpleBlock(ctx.get(), p.models().cubeBottomTop(ctx.getName(),
                            p.modLoc("block/rich_crimson_nylium_side"),
                            p.mcLoc("block/netherrack"),
                            p.modLoc("block/rich_crimson_nylium")))
            )
            .simpleItem()
            .register();

    public static final BlockEntry<RichNyliumBlock> UNTOPPED_CRIMSON_NYLIUM = REGISTRATE.block("untopped_crimson_nylium", RichNyliumBlock::new)
            .initialProperties(() -> Blocks.NETHERRACK)
            .properties(prop -> prop.sound(SoundType.NYLIUM))
            .tag(BlockTags.NYLIUM, BlockTags.MUSHROOM_GROW_BLOCK, io.github.cvrunmin.create_andertech.index.AllTags.UNTOPPED_NYLIUM)
            .loot((tables, block) -> tables.add(block, tables.createSingleItemTableWithSilkTouch(block, Blocks.NETHERRACK)))
            .transform(pickaxeOnly())
            .blockstate((ctx, p) ->
                    p.simpleBlock(ctx.get(), p.models().cubeBottomTop(ctx.getName(),
                            p.modLoc("block/untopped_crimson_nylium_side"),
                            p.mcLoc("block/netherrack"),
                            p.modLoc("block/untopped_crimson_nylium")))
            )
            .simpleItem()
            .register();

    public static final BlockEntry<PoorNyliumBlock> POOR_CRIMSON_NYLIUM = REGISTRATE.block("poor_crimson_nylium", PoorNyliumBlock::new)
            .initialProperties(() -> Blocks.NETHERRACK)
            .properties(prop -> prop.sound(SoundType.NYLIUM).randomTicks())
            .tag(BlockTags.NYLIUM, BlockTags.MUSHROOM_GROW_BLOCK)
            .loot((tables, block) -> tables.add(block, tables.createSingleItemTableWithSilkTouch(block, Blocks.NETHERRACK)))
            .transform(pickaxeOnly())
            .blockstate((ctx, p) ->
                    p.simpleBlock(ctx.get(), p.models().cubeBottomTop(ctx.getName(),
                            p.modLoc("block/poor_crimson_nylium_side"),
                            p.mcLoc("block/netherrack"),
                            p.modLoc("block/poor_crimson_nylium")))
            )
            .simpleItem()
            .register();

    private static <I extends BlockItem, P>NonNullFunction<ItemBuilder<I, P>, P> tempCustomItemModel(ResourceLocation parent){
        return tempCustomItemModel(parent, Map.of());
    }

    private static <I extends BlockItem, P>NonNullFunction<ItemBuilder<I, P>, P> tempCustomItemModel(ResourceLocation parent, Map<String, ResourceLocation> overriddenTextures){
        return block ->
                block.model((c, p) -> {
                    ItemModelBuilder mb = p.getBuilder(p.name(() -> c.getEntry().getBlock())).parent(new ModelFile.UncheckedModelFile(parent));
                    for (Map.Entry<String, ResourceLocation> entry : overriddenTextures.entrySet()) {
                        mb = mb.texture(entry.getKey(), entry.getValue());
                    }
                }
                ).build();
    }

    public static void register(){ }
}
