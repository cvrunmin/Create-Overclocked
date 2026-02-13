package io.github.cvrunmin.create_andertech.foundation;

import com.simibubi.create.Create;
import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.content.decoration.encasing.EncasedCTBehaviour;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogCTBehaviour;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedShaftBlock;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import io.github.cvrunmin.create_andertech.index.AllBlocks;
import io.github.cvrunmin.create_andertech.CreateAndertech;
import io.github.cvrunmin.create_andertech.content.kinetics.simpleRelays.encased.EncasedCrimschistCogwheelBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.simpleRelays.encased.EncasedCrimschistShaftBlock;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.client.model.generators.ModelFile;

import java.util.function.Supplier;

import static com.simibubi.create.foundation.data.BlockStateGen.axisBlock;

public class VariousBuilders {
    public static <B extends EncasedCrimschistShaftBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> encasedCrimschistShaft(String casing,
                                                                                                                             Supplier<CTSpriteShiftEntry> casingShift) {
        return builder -> encasedBase(builder, AllBlocks.CRIMSCHIST_SHAFT::get)
                .onRegister(CreateRegistrate.connectedTextures(() -> new EncasedCTBehaviour(casingShift.get())))
                .onRegister(CreateRegistrate.casingConnectivity((block, cc) -> cc.make(block, casingShift.get(),
                        (s, f) -> f.getAxis() != s.getValue(EncasedShaftBlock.AXIS))))
                .blockstate((c, p) -> axisBlock(c, p, blockState ->
                        new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath("create", "block/encased_shaft/block_" + casing))
//                        p.models().getExistingFile(p.modLoc("block/encased_shaft/block_" + casing))
                        , true))
                .item()
                .model((c, p) -> {
                    p.getBuilder("block/encased_crimschist_shaft/item_" + casing)
                            .parent(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath("create", "block/encased_shaft/item_" + casing)))
                            .texture("1_0", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_axis"))
                            .texture("1_1", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_axis_top"));
                    p.withExistingParent(c.getName(), p.modLoc("block/encased_crimschist_shaft/item_" + casing));
                })
                .build();
    }
    public static <B extends EncasedCrimschistCogwheelBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> encasedCogwheel(
            String casing, Supplier<CTSpriteShiftEntry> casingShift) {
        return b -> encasedCogwheelBase(b, casing, casingShift, AllBlocks.CRIMSCHIST_COGWHEEL::get, false);
    }

    public static <B extends EncasedCrimschistCogwheelBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> encasedLargeCogwheel(
            String casing, Supplier<CTSpriteShiftEntry> casingShift) {
        return b -> encasedCogwheelBase(b, casing, casingShift, AllBlocks.CRIMSCHIST_LARGE_COGWHEEL::get, true)
                .onRegister(CreateRegistrate.connectedTextures(() -> new EncasedCogCTBehaviour(casingShift.get())));
    }

    private static <B extends EncasedCrimschistCogwheelBlock, P> BlockBuilder<B, P> encasedCogwheelBase(BlockBuilder<B, P> b,
                                                                                              String casing, Supplier<CTSpriteShiftEntry> casingShift, Supplier<ItemLike> drop, boolean large) {
        String encasedSuffix = "_encased_cogwheel_side" + (large ? "_connected" : "");
        String blockFolder = large ? "encased_large_cogwheel" : "encased_cogwheel";
        String wood = casing.equals("brass") ? "dark_oak" : "spruce";
        String gearbox = casing.equals("brass") ? "brass_gearbox" : "gearbox";
        ResourceLocation casingTextureLocation = ResourceLocation.fromNamespaceAndPath("create", "block/" + casing + "_casing");
        ResourceLocation gearboxTextureLocation = ResourceLocation.fromNamespaceAndPath("create", "block/" + gearbox);
        ResourceLocation strippedWoodTextureLocation = ResourceLocation.withDefaultNamespace("block/stripped_" + wood + "_log_top");
        ResourceLocation sideTextureLocation = ResourceLocation.fromNamespaceAndPath("create", "block/" + casing + encasedSuffix);
        //noinspection removal
        return encasedBase(b, drop).addLayer(() -> RenderType::cutoutMipped)
                .onRegister(CreateRegistrate.casingConnectivity((block, cc) -> cc.make(block, casingShift.get(),
                        (s, f) -> f.getAxis() == s.getValue(EncasedCrimschistCogwheelBlock.AXIS)
                                && !s.getValue(f.getAxisDirection() == Direction.AxisDirection.POSITIVE ? EncasedCrimschistCogwheelBlock.TOP_SHAFT
                                : EncasedCrimschistCogwheelBlock.BOTTOM_SHAFT))))
                .blockstate((c, p) -> axisBlock(c, p, blockState -> {
                    String suffix = (blockState.getValue(EncasedCrimschistCogwheelBlock.TOP_SHAFT) ? "_top" : "")
                            + (blockState.getValue(EncasedCrimschistCogwheelBlock.BOTTOM_SHAFT) ? "_bottom" : "");
                    String modelName = c.getName() + suffix;
                    p.models().existingFileHelper.trackGenerated(sideTextureLocation, PackType.CLIENT_RESOURCES, ".png", "textures");
                    p.models().existingFileHelper.trackGenerated(casingTextureLocation, PackType.CLIENT_RESOURCES, ".png", "textures");
                    p.models().existingFileHelper.trackGenerated(gearboxTextureLocation, PackType.CLIENT_RESOURCES, ".png", "textures");
                    return p.models()
                            .getBuilder(modelName).parent(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath("create", "block/" + blockFolder + "/block" + suffix)))
                            .texture("casing", casingTextureLocation)
                            .texture("particle", casingTextureLocation)
                            .texture("4", gearboxTextureLocation)
                            .texture("1", strippedWoodTextureLocation)
                            .texture("side", sideTextureLocation);
                }, false))
                .item()
                .model((c, p) -> p.getBuilder(c.getName()).parent(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath("create", "block/" + blockFolder + "/item")))
                        .texture("casing", casingTextureLocation)
                        .texture("particle", casingTextureLocation)
                        .texture("1", strippedWoodTextureLocation)
                        .texture("side", sideTextureLocation)
                        .texture("1_2", large ? ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/warped_large_cogwheel") : ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/warped_cogwheel"))
                )
                .build();
    }

    private static <B extends RotatedPillarKineticBlock, P> BlockBuilder<B, P> encasedBase(BlockBuilder<B, P> b,
                                                                                           Supplier<ItemLike> drop) {
        return b.initialProperties(SharedProperties::stone)
                .properties(BlockBehaviour.Properties::noOcclusion)
                .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 0))
                .loot((p, lb) -> p.dropOther(lb, drop.get()));
    }

    public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> chronenditeBearing(String prefix, String backTexture){
        ResourceLocation baseBlockModelLocation = Create.asResource("block/bearing/block");
        ResourceLocation baseItemModelLocation = Create.asResource("block/bearing/item");
        ResourceLocation topTextureLocation = Create.asResource("block/bearing_top");
        ResourceLocation sideTextureLocation = Create.asResource("block/" + prefix + "_bearing_side");
        ResourceLocation backTextureLocation = Create.asResource("block/" + backTexture);
        return b ->
                b.initialProperties(SharedProperties::stone)
                        .properties(BlockBehaviour.Properties::noOcclusion)
                        .blockstate((c, p) -> {
                            // fake the existence checker that those textures exists
                            p.models().existingFileHelper.trackGenerated(sideTextureLocation, PackType.CLIENT_RESOURCES, ".png", "textures");
                            p.models().existingFileHelper.trackGenerated(backTextureLocation, PackType.CLIENT_RESOURCES, ".png", "textures");
                            p.directionalBlock(c.get(),
                                    p.models().getBuilder(c.getName()).parent(new ModelFile.UncheckedModelFile(baseBlockModelLocation))
                                            .texture("side", sideTextureLocation)
                                            .texture("back", backTextureLocation));
                        })
                        .item()
                        .model((c, p) -> {
                            p.existingFileHelper.trackGenerated(topTextureLocation, PackType.CLIENT_RESOURCES, ".png", "textures");
                            p.existingFileHelper.trackGenerated(sideTextureLocation, PackType.CLIENT_RESOURCES, ".png", "textures");
                            p.existingFileHelper.trackGenerated(backTextureLocation, PackType.CLIENT_RESOURCES, ".png", "textures");
                            p.getBuilder(c.getName()).parent(new ModelFile.UncheckedModelFile(baseItemModelLocation))
                                    .texture("top", topTextureLocation)
                                    .texture("side", sideTextureLocation)
                                    .texture("back", backTextureLocation);
                        })
                        .build();
    }

    private static <I extends BlockItem> NonNullBiConsumer<DataGenContext<Item, I>, RegistrateItemModelProvider> tempCustomItemModel(ResourceLocation parent){
        return (c, p) ->
                        p.getBuilder(c.getName()).parent(new ModelFile.UncheckedModelFile(parent))
                ;
    }
}
