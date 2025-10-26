package io.github.cvrunmin.create_andertech;

import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.AllTags;
import com.simibubi.create.Create;
import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.content.contraptions.bearing.StabilizedBearingMovementBehaviour;
import com.simibubi.create.content.decoration.encasing.EncasedCTBehaviour;
import com.simibubi.create.content.decoration.encasing.EncasingRegistry;
import com.simibubi.create.content.decoration.girder.ConnectedGirderModel;
import com.simibubi.create.content.kinetics.belt.BeltModel;
import com.simibubi.create.content.kinetics.crafter.CrafterCTBehaviour;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlock;
import com.simibubi.create.content.kinetics.deployer.DeployerBlock;
import com.simibubi.create.content.kinetics.deployer.DeployerMovementBehaviour;
import com.simibubi.create.content.kinetics.deployer.DeployerMovingInteraction;
import com.simibubi.create.content.kinetics.gearbox.GearboxBlock;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmItem;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockModel;
import com.simibubi.create.content.kinetics.simpleRelays.CogwheelBlockItem;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogCTBehaviour;
import com.simibubi.create.content.processing.AssemblyOperatorBlockItem;
import com.simibubi.create.foundation.data.*;
import com.simibubi.create.infrastructure.config.CStress;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import io.github.cvrunmin.create_andertech.content.contraptions.bearing.ChronenditeMechanicalBearingBlock;
import io.github.cvrunmin.create_andertech.content.decoration.GirderEncasedCrimschistShaftBlock;
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
import io.github.cvrunmin.create_andertech.content.kinetics.transmission.CrimschistClutchBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.transmission.CrimschistGearshiftBlock;
import io.github.cvrunmin.create_andertech.foundation.AssetLookupWithParent;
import io.github.cvrunmin.create_andertech.foundation.BlockStateGenerators;
import io.github.cvrunmin.create_andertech.foundation.VariousBuilders;
import net.createmod.catnip.data.Couple;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;

import java.util.Map;

import static com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour.interactionBehaviour;
import static com.simibubi.create.api.behaviour.movement.MovementBehaviour.movementBehaviour;
import static com.simibubi.create.foundation.data.BlockStateGen.axisBlock;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;
import static io.github.cvrunmin.create_andertech.CreateAndertech.REGISTRATE;

public class AllBlocks {
    static {
        REGISTRATE.setCreativeTab(AllCreativeTabs.MAIN_TAB);
    }
    public static final BlockEntry<CrimschistShaftBlock> CRIMSCHIST_SHAFT = REGISTRATE.block("crimschist_shaft", CrimschistShaftBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.METAL).forceSolidOff())
            .transform(TagGen.pickaxeOnly())
            .blockstate(BlockStateGenerators.textureOverrideAxisBlockProvider(false, "create", "shaft", Map.ofEntries(
                    Map.entry("particle", new ResourceLocation(CreateAndertech.MODID, "block/crimschist_axis")),
                    Map.entry("0", new ResourceLocation(CreateAndertech.MODID, "block/crimschist_axis")),
                    Map.entry("1", new ResourceLocation(CreateAndertech.MODID, "block/crimschist_axis_top")))
                    )
            )
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
            .blockstate(BlockStateGenerators.textureOverrideAxisBlockProvider(false, "create", "cogwheel", Map.ofEntries(
                    Map.entry("0", new ResourceLocation(CreateAndertech.MODID, "block/crimschist_cogwheel_axis")),
                    Map.entry("3", new ResourceLocation(CreateAndertech.MODID, "block/crimschist_axis_top")),
                    Map.entry("1_2", new ResourceLocation(CreateAndertech.MODID, "block/warped_cogwheel")),
                    Map.entry("particle", new ResourceLocation("block/stripped_warped_stem_top")))
                    )
            )
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
                        Map.entry("0", new ResourceLocation(CreateAndertech.MODID, "block/crimschist_cogwheel_axis")),
                        Map.entry("3", new ResourceLocation(CreateAndertech.MODID, "block/crimschist_axis_top")),
                        Map.entry("4", new ResourceLocation(CreateAndertech.MODID, "block/warped_large_cogwheel")),
                        Map.entry("particle", new ResourceLocation("block/stripped_warped_stem")))
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
                    .mapColor(MapColor.PODZOL))
            .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 0))
            .transform(axeOrPickaxe())
            .onRegister(CreateRegistrate.connectedTextures(() -> new EncasedCTBehaviour(AllSpriteShifts.ANDESITE_CASING)))
            .onRegister(CreateRegistrate.casingConnectivity((block, cc) -> cc.make(block, AllSpriteShifts.ANDESITE_CASING,
                    (s, f) -> f.getAxis() == s.getValue(GearboxBlock.AXIS))))
            .blockstate((c, p) -> axisBlock(c, p, $ -> AssetLookupWithParent.partialModel(c, p, "create", "gearbox", Map.of()), true))
            .item()
            .transform(b -> b.model((c, p) -> {
                p.getBuilder("block/" + p.name(() -> c.getEntry().getBlock()) + "/item").parent(new ModelFile.UncheckedModelFile(new ResourceLocation("create", "block/gearbox/item")))
                        .texture("1_0", new ResourceLocation(CreateAndertech.MODID, "block/crimschist_axis"))
                        .texture("1_1", new ResourceLocation(CreateAndertech.MODID, "block/crimschist_axis_top"));
                p.getBuilder("block/" + p.name(() -> c.getEntry().getBlock()) + "/item_vertical").parent(new ModelFile.UncheckedModelFile(new ResourceLocation("create", "block/gearbox/item_vertical")))
                        .texture("0", new ResourceLocation(CreateAndertech.MODID, "block/crimschist_axis"))
                        .texture("1", new ResourceLocation(CreateAndertech.MODID, "block/crimschist_axis_top"));
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
            .blockstate((c, p) -> BlockStateGen.axisBlock(c, p, AssetLookupWithParent.forPowered(c, p, "create", "clutch", Map.of())))
            .item()
            .transform(tempCustomItemModel(new ResourceLocation("create", "block/clutch/item"), Map.ofEntries(
                    Map.entry("particle", new ResourceLocation(CreateAndertech.MODID, "block/crimschist_axis")),
                    Map.entry("1_0", new ResourceLocation(CreateAndertech.MODID, "block/crimschist_axis")),
                    Map.entry("1_1", new ResourceLocation(CreateAndertech.MODID, "block/crimschist_axis_top"))
            )))
            .register();

    public static final BlockEntry<CrimschistGearshiftBlock> CRIMSCHIST_GEARSHIFT = REGISTRATE.block("crimschist_gearshift", CrimschistGearshiftBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.noOcclusion()
                    .mapColor(MapColor.PODZOL))
            .addLayer(() -> RenderType::cutoutMipped)
            .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 0))
            .transform(axeOrPickaxe())
            .blockstate((c, p) -> BlockStateGen.axisBlock(c, p, AssetLookupWithParent.forPowered(c, p, "create", "gearshift", Map.of())))
            .item()
            .transform(tempCustomItemModel(new ResourceLocation("create", "block/gearshift/item"), Map.ofEntries(
                    Map.entry("particle", new ResourceLocation(CreateAndertech.MODID, "block/crimschist_axis")),
                    Map.entry("1_0", new ResourceLocation(CreateAndertech.MODID, "block/crimschist_axis")),
                    Map.entry("1_1", new ResourceLocation(CreateAndertech.MODID, "block/crimschist_axis_top"))
            )))
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
                                return p.models().getBuilder("block/" + c.getName() + "/" + suffix).parent(new ModelFile.UncheckedModelFile(new ResourceLocation("create", "block/encased_chain_drive/" + suffix)));
                            }).generate(c, p))
                    .item()
                    .transform(tempCustomItemModel(new ResourceLocation("create", "block/encased_chain_drive/item"), Map.ofEntries(
                            Map.entry("1_0", new ResourceLocation(CreateAndertech.MODID, "block/crimschist_axis")),
                            Map.entry("1_1", new ResourceLocation(CreateAndertech.MODID, "block/crimschist_axis_top"))
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
//            .transform(displaySource(AllDisplaySources.ITEM_NAMES))
            .onRegister(CreateRegistrate.blockModel(() -> BeltModel::new))
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
                    .transform(tempCustomItemModel(new ResourceLocation("create", "block/creative_motor/item"), Map.ofEntries(
                            Map.entry("1_0", new ResourceLocation(CreateAndertech.MODID, "block/crimschist_axis")),
                            Map.entry("1_1", new ResourceLocation(CreateAndertech.MODID, "block/crimschist_axis_top"))
                    )))
                    .register();

    public static final BlockEntry<ChronenditeEncasedFanBlock> CHRONENDITE_ENCASED_FAN = REGISTRATE.block("chronendite_encased_fan", prop -> new ChronenditeEncasedFanBlock(prop, false))
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.PODZOL))
            .blockstate(BlockStateGenerators.textureOverrideDirectionalBlockProvider(true, "create", "encased_fan", Map.ofEntries()))
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(axeOrPickaxe())
            .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 2.0))
            .item()
            .transform(tempCustomItemModel(new ResourceLocation("create", "block/encased_fan/item")))
            .register();

    public static final BlockEntry<ChronenditeEncasedFanBlock> CHRONENDITE_HV_ENCASED_FAN = REGISTRATE.block("chronendite_hv_encased_fan", prop -> new ChronenditeEncasedFanBlock(prop, true))
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.PODZOL))
            .lang("Chronendite High Volume Encased Fan")
            .blockstate(BlockStateGenerators.textureOverrideDirectionalBlockProvider(true, "create", "encased_fan", Map.ofEntries()))
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(axeOrPickaxe())
            .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 2.0))
            .item()
            .transform(tempCustomItemModel(new ResourceLocation("create", "block/encased_fan/item")))
            .register();

    public static final BlockEntry<ChronenditeCrushWheelBlock> CHRONENDITE_CRUSHING_WHEEL =
            REGISTRATE.block("chronendite_crushing_wheel", ChronenditeCrushWheelBlock::new)
                    .properties(p -> p.mapColor(MapColor.METAL))
                    .initialProperties(SharedProperties::stone)
                    .properties(BlockBehaviour.Properties::noOcclusion)
                    .transform(pickaxeOnly())
                    .blockstate(BlockStateGenerators.textureOverrideAxisBlockProvider(true, "create", "crushing_wheel", Map.ofEntries()))
                    .addLayer(() -> RenderType::cutoutMipped)
                    .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 8.0))
                    .item()
                    .transform(tempCustomItemModel(new ResourceLocation("create", "block/crushing_wheel/item")))
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
                            .mapColor(MapColor.PODZOL))
                    .transform(axeOrPickaxe())
                    .blockstate(BlockStateGenerators.textureOverrideHorizontalBlockProvider(true, "create", "mechanical_press", Map.ofEntries(

                    )))
                    .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 8))
                    .item(AssemblyOperatorBlockItem::new)
                    .transform(tempCustomItemModel(new ResourceLocation("create", "block/mechanical_press/item"), Map.ofEntries(
                            Map.entry("0", new ResourceLocation(CreateAndertech.MODID, "block/crimschist_axis")),
                            Map.entry("1", new ResourceLocation(CreateAndertech.MODID, "block/crimschist_axis_top"))
                    )))
                    .register();

    @SuppressWarnings("removal")
    public static final BlockEntry<ChronenditeMechanicalMixerBlock> CHRONENDITE_MECHANICAL_MIXER =
            REGISTRATE.block("chronendite_mechanical_mixer", ChronenditeMechanicalMixerBlock::new)
                    .initialProperties(SharedProperties::stone)
                    .properties(p -> p.noOcclusion()
                            .mapColor(MapColor.STONE))
                    .transform(axeOrPickaxe())
                    .blockstate((c, p) -> p.simpleBlock(c.getEntry(), p.models()
                            .getBuilder("block/" + c.getName() + "/block")
                            .parent(new ModelFile.UncheckedModelFile(new ResourceLocation(Create.ID, "block/mechanical_mixer/block")))
                    ))
                    .addLayer(() -> RenderType::cutoutMipped)
                    .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 4))
                    .item(AssemblyOperatorBlockItem::new)
                    .transform(tempCustomItemModel(new ResourceLocation("create", "block/mechanical_mixer/item"), Map.ofEntries(
                            Map.entry("1_2", new ResourceLocation(CreateAndertech.MODID, "block/warped_cogwheel"))
                    )))
                    .register();


    public static final BlockEntry<ChronenditeDrillBlock> CHRONENDITE_MECHANICAL_DRILL = REGISTRATE.block("chronendite_mechanical_drill", ChronenditeDrillBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.PODZOL))
            .transform(axeOrPickaxe())
            .blockstate(BlockStateGenerators.textureOverrideDirectionalBlockProvider(true, "create", "mechanical_drill", Map.ofEntries(

            )))
            .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 4))
            .onRegister(movementBehaviour(new ChronenditeDrillMovementBehaviour()))
            .item()
            .tag(AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag)
            .transform(tempCustomItemModel(new ResourceLocation("create", "block/mechanical_drill/item"), Map.ofEntries(
                    Map.entry("0", new ResourceLocation(CreateAndertech.MODID, "block/crimschist_axis_top")),
                    Map.entry("1", new ResourceLocation(CreateAndertech.MODID, "block/crimschist_axis"))
            )))
            .register();

    @SuppressWarnings("removal")
    public static final BlockEntry<ChronenditeSawBlock> CHRONENDITE_MECHANICAL_SAW = REGISTRATE.block("chronendite_mechanical_saw", ChronenditeSawBlock::new)
            .initialProperties(SharedProperties::stone)
            .addLayer(() -> RenderType::cutoutMipped)
            .properties(p -> p.mapColor(MapColor.PODZOL))
            .transform(axeOrPickaxe())
            .blockstate(new BlockStateGenerators.Saw()::generate)
            .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 4))
            .onRegister(movementBehaviour(new ChronenditeSawMovementBehaviour()))
            .addLayer(() -> RenderType::cutoutMipped)
            .item()
            .tag(AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag)
            .transform(tempCustomItemModel(new ResourceLocation("create", "block/mechanical_saw/item")))
            .register();


    public static final BlockEntry<ChronenditeDeployerBlock> CHRONENDITE_DEPLOYER = REGISTRATE.block("chronendite_deployer", ChronenditeDeployerBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.PODZOL))
            .transform(axeOrPickaxe())
            .blockstate(BlockStateGenerators.textureOverrideDirectionalAxisBlockProvider("create", "deployer", Map.ofEntries()))
            .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 4.0))
            .onRegister(movementBehaviour(new DeployerMovementBehaviour()))
            .onRegister(interactionBehaviour(new DeployerMovingInteraction()))
            .item(AssemblyOperatorBlockItem::new)
            .tag(AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag)
            .transform(tempCustomItemModel(new ResourceLocation("create", "block/deployer/item")))
            .register();

    public static final BlockEntry<ChronenditeMechanicalBearingBlock> CHRONENDITE_MECHANICAL_BEARING =
            REGISTRATE.block("chronendite_mechanical_bearing", ChronenditeMechanicalBearingBlock::new)
                    .properties(p -> p.mapColor(MapColor.PODZOL))
                    .transform(axeOrPickaxe())
                    .transform(VariousBuilders.chronenditeBearing("mechanical", "gearbox"))
                    .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 4))
                    .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
                    .onRegister(movementBehaviour(new StabilizedBearingMovementBehaviour()))
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
                .transform(tempCustomItemModel(new ResourceLocation("create", "block/rotation_speed_controller/item")))
                .register();

    public static final BlockEntry<ChronenditeMechanicalCrafterBlock> CHRONENDITE_MECHANICAL_CRAFTER =
            REGISTRATE.block("chronendite_mechanical_crafter", ChronenditeMechanicalCrafterBlock::new)
                    .initialProperties(SharedProperties::softMetal)
                    .properties(p -> p.noOcclusion()
                            .mapColor(MapColor.TERRACOTTA_YELLOW))
                    .transform(axeOrPickaxe())
                    .blockstate(BlockStateGenerators.textureOverrideHorizontalBlockProvider(true, "create", "mechanical_crafter", Map.ofEntries()))
                    .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 2.0))
                    .onRegister(CreateRegistrate.connectedTextures(OCCrafterCTBehaviour::new))
                    .addLayer(() -> RenderType::cutoutMipped)
                    .item()
                    .transform(tempCustomItemModel(new ResourceLocation("create", "block/mechanical_crafter/item")))
                    .register();


    public static final BlockEntry<ChronenditeArmBlock> CHRONENDITE_MECHANICAL_ARM = REGISTRATE.block("chronendite_mechanical_arm", ChronenditeArmBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.TERRACOTTA_YELLOW))
            .transform(axeOrPickaxe())
            .blockstate((c, p) -> p.getVariantBuilder(c.get())
                    .forAllStates(s -> ConfiguredModel.builder()
                            .modelFile(AssetLookupWithParent.partialModel(c, p, "create", "mechanical_arm", Map.ofEntries()))
                            .rotationX(s.getValue(ChronenditeArmBlock.CEILING) ? 180 : 0)
                            .build()))
            .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 2.0))
            .item(ArmItem::new)
            .transform(tempCustomItemModel(new ResourceLocation("create", "block/mechanical_arm/item")))
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
