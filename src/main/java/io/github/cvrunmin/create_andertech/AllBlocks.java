package io.github.cvrunmin.create_andertech;

import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.AllTags;
import com.simibubi.create.Create;
import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.content.contraptions.bearing.StabilizedBearingMovementBehaviour;
import com.simibubi.create.content.decoration.encasing.EncasingRegistry;
import com.simibubi.create.content.decoration.girder.ConnectedGirderModel;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockModel;
import com.simibubi.create.content.kinetics.simpleRelays.CogwheelBlockItem;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogCTBehaviour;
import com.simibubi.create.content.processing.AssemblyOperatorBlockItem;
import com.simibubi.create.foundation.data.*;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import io.github.cvrunmin.create_andertech.content.contraptions.bearing.ChronenditeMechanicalBearingBlock;
import io.github.cvrunmin.create_andertech.content.decoration.GirderEncasedCrimschistShaftBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.chainDrive.CrimschistChainDriveBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.drill.ChronenditeDrillBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.drill.ChronenditeDrillMovementBehaviour;
import io.github.cvrunmin.create_andertech.content.kinetics.mixer.ChronenditeMechanicalMixerBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.motor.CreativeOCMotorBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.press.ChronenditeMechanicalPressBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.saw.ChronenditeSawBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.saw.ChronenditeSawMovementBehaviour;
import io.github.cvrunmin.create_andertech.content.kinetics.simpleRelays.CrimschistCogWheelBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.simpleRelays.CrimschistShaftBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.simpleRelays.encased.EncasedCrimschistCogwheelBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.simpleRelays.encased.EncasedCrimschistShaftBlock;
import io.github.cvrunmin.create_andertech.foundation.BlockStateGenerators;
import io.github.cvrunmin.create_andertech.foundation.VariousBuilders;
import net.createmod.catnip.data.Couple;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.client.model.generators.ModelFile;

import static com.simibubi.create.api.behaviour.movement.MovementBehaviour.movementBehaviour;
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
            .blockstate(tempAxisBlockProvider("block/shaft"))
            .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 0))
            .onRegister(CreateRegistrate.blockModel(() -> BracketedKineticBlockModel::new))
            .item()
            .transform(block ->
                            block.model((c, p) ->
                                    p.getBuilder(p.name(() -> c.getEntry().getBlock())).parent(new ModelFile.UncheckedModelFile(new ResourceLocation("create", "block/shaft")))
                            ).build()
            )
            .register();


    public static final BlockEntry<CrimschistCogWheelBlock> CRIMSCHIST_COGWHEEL = REGISTRATE.block("crimschist_cogwheel", CrimschistCogWheelBlock::small)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.sound(SoundType.WOOD)
                    .mapColor(MapColor.DIRT))
//            .transform(CStress.setNoImpact())
            .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 0))
            .transform(axeOrPickaxe())
            .blockstate(tempAxisBlockProvider("block/cogwheel"))
            .onRegister(CreateRegistrate.blockModel(() -> BracketedKineticBlockModel::new))
            .item(CogwheelBlockItem::new)
            .transform(block ->
                    block.model((c, p) ->
                                    p.getBuilder(p.name(() -> c.getEntry().getBlock())).parent(new ModelFile.UncheckedModelFile(new ResourceLocation("create", "block/cogwheel")))
                            )
            .build())
            .register();

    public static final BlockEntry<CrimschistCogWheelBlock> CRIMSCHIST_LARGE_COGWHEEL =
            REGISTRATE.block("crimschist_large_cogwheel", CrimschistCogWheelBlock::large)
                    .initialProperties(SharedProperties::stone)
                    .properties(p -> p.sound(SoundType.WOOD)
                            .mapColor(MapColor.DIRT))
                    .transform(axeOrPickaxe())
//                    .transform(CStress.setNoImpact())
                    .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 0))
                    .blockstate(tempAxisBlockProvider("block/large_cogwheel"))
                    .onRegister(CreateRegistrate.blockModel(() -> BracketedKineticBlockModel::new))
                    .item(CogwheelBlockItem::new)
                    .transform(block ->
                            block.model((c, p) ->
                                            p.getBuilder(p.name(() -> c.getEntry().getBlock())).parent(new ModelFile.UncheckedModelFile(new ResourceLocation("create", "block/large_cogwheel")))
                                    )
                    .build())
                    .register();

    public static final BlockEntry<EncasedCrimschistShaftBlock> ANDESITE_ENCASED_CRIMSCHIST_SHAFT =
            REGISTRATE.block("andesite_encased_crimschist_shaft", p -> new EncasedCrimschistShaftBlock(p, com.simibubi.create.AllBlocks.ANDESITE_CASING::get))
                    .properties(p -> p.mapColor(MapColor.PODZOL))
                    .transform(VariousBuilders.encasedShaft("andesite", () -> AllSpriteShifts.ANDESITE_CASING))
                    .transform(EncasingRegistry.addVariantTo(AllBlocks.CRIMSCHIST_SHAFT))
                    .transform(axeOrPickaxe())
                    .register();

    public static final BlockEntry<EncasedCrimschistShaftBlock> BRASS_ENCASED_CRIMSCHIST_SHAFT =
            REGISTRATE.block("brass_encased_crimschist_shaft", p -> new EncasedCrimschistShaftBlock(p, com.simibubi.create.AllBlocks.BRASS_CASING::get))
                    .properties(p -> p.mapColor(MapColor.TERRACOTTA_BROWN))
                    .transform(VariousBuilders.encasedShaft("brass", () -> AllSpriteShifts.BRASS_CASING))
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

    public static final BlockEntry<CrimschistChainDriveBlock> ENCASED_CRIMSCHIST_CHAIN_DRIVE =
            REGISTRATE.block("encased_crimschist_chain_drive", CrimschistChainDriveBlock::new)
                    .initialProperties(SharedProperties::stone)
                    .properties(p -> p.noOcclusion()
                            .mapColor(MapColor.PODZOL))
                    .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 0))
                    .transform(axeOrPickaxe())
                    .blockstate((c, p) -> new BlockStateGenerators.ChainDrive(
                            (state, suffix) -> new ModelFile.UncheckedModelFile(new ResourceLocation("create", "block/encased_chain_drive/" + suffix))).generate(c, p))
                    .item()
                    .transform(tempCustomItemModel(new ResourceLocation("create", "block/encased_chain_drive/item")))
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
                    .transform(tempCustomItemModel(new ResourceLocation("create", "block/creative_motor_item")))
//                    .transform(customItemModel())
                    .register();


    public static final BlockEntry<ChronenditeMechanicalPressBlock> CHRONENDITE_MECHANICAL_PRESS =
            REGISTRATE.block("chronendite_mechanical_press", ChronenditeMechanicalPressBlock::new)
                    .initialProperties(SharedProperties::stone)
                    .properties(p -> p.noOcclusion()
                            .mapColor(MapColor.PODZOL))
                    .transform(axeOrPickaxe())
                    .blockstate(tempHorizontalBlockProvider("block/mechanical_press/block"))
                    .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 8))
                    .item(AssemblyOperatorBlockItem::new)
//                    .transform(customItemModel())
                    .transform(tempCustomItemModel(new ResourceLocation("create", "block/mechanical_press/item")))
                    .register();

    @SuppressWarnings("removal")
    public static final BlockEntry<ChronenditeMechanicalMixerBlock> CHRONENDITE_MECHANICAL_MIXER =
            REGISTRATE.block("chronendite_mechanical_mixer", ChronenditeMechanicalMixerBlock::new)
                    .initialProperties(SharedProperties::stone)
                    .properties(p -> p.noOcclusion()
                            .mapColor(MapColor.STONE))
                    .transform(axeOrPickaxe())
                    .blockstate((c, p) -> p.simpleBlock(c.getEntry(), new ModelFile.UncheckedModelFile(new ResourceLocation(Create.ID, "block/mechanical_mixer/block"))))
                    .addLayer(() -> RenderType::cutoutMipped)
                    .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 4))
                    .item(AssemblyOperatorBlockItem::new)
                    .transform(tempCustomItemModel(new ResourceLocation("create", "block/mechanical_mixer/item")))
                    .register();


    public static final BlockEntry<ChronenditeDrillBlock> CHRONENDITE_MECHANICAL_DRILL = REGISTRATE.block("chronendite_mechanical_drill", ChronenditeDrillBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.PODZOL))
            .transform(axeOrPickaxe())
            .blockstate(tempDirectionalBlockProvider("block/mechanical_drill/block"))
            .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 4))
            .onRegister(movementBehaviour(new ChronenditeDrillMovementBehaviour()))
            .item()
            .tag(AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag)
//            .transform(customItemModel())
            .transform(tempCustomItemModel(new ResourceLocation("create", "block/mechanical_drill/block")))
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
//            .transform(customItemModel())
            .transform(tempCustomItemModel(new ResourceLocation("create", "block/mechanical_saw/item")))
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

    private static <T extends Block>NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> tempAxisBlockProvider(String parent){
        return (c, p) -> BlockStateGen.axisBlock(c, p, $ -> new ModelFile.UncheckedModelFile(new ResourceLocation(Create.ID, parent)));
    }

    private static <T extends Block>NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> tempHorizontalBlockProvider(String parent){
        return (c, p) -> p.horizontalBlock(c.get(), $ -> new ModelFile.UncheckedModelFile(new ResourceLocation(Create.ID, parent)));
    }

    private static <T extends Block>NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> tempDirectionalBlockProvider(String parent){
        return (c, p) -> p.directionalBlock(c.get(), $ -> new ModelFile.UncheckedModelFile(new ResourceLocation(Create.ID, parent)));
    }

    private static <I extends BlockItem, P>NonNullFunction<ItemBuilder<I, P>, P> tempCustomItemModel(ResourceLocation parent){
        return block ->
                block.model((c, p) ->
                        p.getBuilder(p.name(() -> c.getEntry().getBlock())).parent(new ModelFile.UncheckedModelFile(parent))
                ).build();
    }

    public static void register(){ }
}
