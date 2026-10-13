package io.github.cvrunmin.create_andertech.foundation;

import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.simibubi.create.content.kinetics.belt.BeltPart;
import com.simibubi.create.content.kinetics.belt.BeltSlope;
import com.simibubi.create.content.kinetics.saw.SawBlock;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import io.github.cvrunmin.create_andertech.CreateAndertech;
import io.github.cvrunmin.create_andertech.content.decoration.GirderEncasedCrimschistShaftBlock;
import io.github.cvrunmin.create_andertech.content.fluids.infusion_vat.InfusionVatBlock;
import io.github.cvrunmin.create_andertech.content.fluids.tank.CrimschistFluidTankBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.belt.OCBeltBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.chainDrive.CrimschistChainDriveBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.motor.CreativeOCMotorBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.saw.ChronenditeSawBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.speedController.ChronenditeSpeedControllerBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.transmission.CrimschistBlackboxDifferentialBlock;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.simibubi.create.foundation.data.BlockStateGen.axisBlock;
import static com.simibubi.create.foundation.data.BlockStateGen.directionalAxisBlock;

public class BlockStateGenerators {

    public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> textureOverrideAxisBlockProvider(
            boolean customItem, String modid, String parentName, Map<String, ResourceLocation> overriddenTextures) {
        return (c, p) -> axisBlock(c, p, getBlockModel(customItem, modid, parentName, overriddenTextures, c, p));
    }


    public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> textureOverrideHorizontalBlockProvider(
            boolean customItem, String modid, String parentName, Map<String, ResourceLocation> overriddenTextures) {
        return (c, p) -> p.horizontalBlock(c.get(), getBlockModel(customItem, modid, parentName, overriddenTextures, c, p));
    }

    public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> textureOverrideDirectionalBlockProvider(
            boolean customItem, String modid, String parentName, Map<String, ResourceLocation> overriddenTextures) {
        return (c, p) -> p.directionalBlock(c.get(), getBlockModel(customItem, modid, parentName, overriddenTextures, c, p));
    }

    public static <T extends DirectionalAxisKineticBlock> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> textureOverrideDirectionalAxisBlockProvider(
            String modid, String parentName, Map<String, ResourceLocation> overriddenTextures) {
        return (c, p) -> directionalAxisBlock(c, p, ($, vertical) -> {
                String format = "block/%s/" + (vertical ? "vertical" : "horizontal");
                return AssetLookupWithParent.specifiedModel(c, p, String.format(format, c.getName()), modid, String.format(format, parentName), overriddenTextures);
        });
    }

    private static <T extends Block> Function<BlockState, ModelFile> getBlockModel(boolean customItem, String modid, String parentName, Map<String, ResourceLocation> overriddenTextures,
                                                                                   DataGenContext<Block, T> c, RegistrateBlockstateProvider p) {
        return $ -> customItem ? AssetLookupWithParent.partialModel(c, p, modid, parentName, overriddenTextures) : AssetLookupWithParent.standardModel(c, p, modid, parentName, overriddenTextures);
    }

    /**
     * Encased large cogwheel shell in crimschist, with the shaft stubs of Create's "top"/"bottom" model variants
     * following the block's own end modes: a capped end is a closed casing, a shaft end shows the stub.
     */
    public static <T extends CrimschistBlackboxDifferentialBlock> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> blackboxDifferential() {
        return (c, p) -> axisBlock(c, p, state -> {
            CrimschistBlackboxDifferentialBlock.Mode positiveMode = CrimschistBlackboxDifferentialBlock.positiveMode(state);
            CrimschistBlackboxDifferentialBlock.Mode negativeMode = CrimschistBlackboxDifferentialBlock.negativeMode(state);
            CrimschistBlackboxDifferentialBlock.Mode cogMode = CrimschistBlackboxDifferentialBlock.cogMode(state);
            String suffix = (positiveMode == CrimschistBlackboxDifferentialBlock.Mode.NONE ? "" : "_top")
                    + (negativeMode == CrimschistBlackboxDifferentialBlock.Mode.NONE ? "" : "_bottom");

            String combination = "";
            switch (negativeMode) {
                case NONE -> combination += "n";
                case IN -> combination += "i";
                case OUT -> combination += "o";
            }
            switch (cogMode) {
                case NONE -> combination += "n";
                case IN -> combination += "i";
                case OUT -> combination += "o";
            }
            switch (positiveMode) {
                case NONE -> combination += "n";
                case IN -> combination += "i";
                case OUT -> combination += "o";
            }

            var sideIn = p.modLoc("block/crimschist_differential_side_in");
            var sideOut = p.modLoc("block/crimschist_differential_side_out");
            var gearIn = p.modLoc("block/crimschist_gearbox_in");
            var gearOut = p.modLoc("block/crimschist_gearbox_out");

            BlockModelBuilder builder = p.models()
                    .withExistingParent("block/" + c.getName() + "/block_" + combination, p.modLoc("block/" + c.getName() + "/block" + suffix));
            if(suffix.contains("top")){
                builder = builder.texture("gear_top", positiveMode == CrimschistBlackboxDifferentialBlock.Mode.IN ? gearIn : gearOut);
            }
            if(suffix.contains("bottom")){
                builder = builder.texture("gear_bottom", negativeMode == CrimschistBlackboxDifferentialBlock.Mode.IN ? gearIn : gearOut);
            }
            if(cogMode != CrimschistBlackboxDifferentialBlock.Mode.NONE){
                builder = builder.texture("side", cogMode == CrimschistBlackboxDifferentialBlock.Mode.IN ? sideIn : sideOut);
            }
            return builder;
        }, false);
    }

    public static void griderBlockStateWithShaft(DataGenContext<Block, GirderEncasedCrimschistShaftBlock> c,
                                           RegistrateBlockstateProvider p) {
        MultiPartBlockStateBuilder builder = p.getMultipartBuilder(c.get());

        builder.part()
                .modelFile(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath("create", "block/metal_girder_encased_shaft/block")))
//                .modelFile(AssetLookup.partialBaseModel(c, p))
                .rotationY(0)
                .addModel()
                .condition(GirderEncasedCrimschistShaftBlock.HORIZONTAL_AXIS, Direction.Axis.Z)
                .end();

        builder.part()
                .modelFile(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath("create", "block/metal_girder_encased_shaft/block")))
//                .modelFile(AssetLookup.partialBaseModel(c, p))
                .rotationY(90)
                .addModel()
                .condition(GirderEncasedCrimschistShaftBlock.HORIZONTAL_AXIS, Direction.Axis.X)
                .end();

        builder.part()
                .modelFile(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath("create", "block/metal_girder_encased_shaft/block_top")))
//                .modelFile(AssetLookup.partialBaseModel(c, p, "top"))
                .addModel()
                .condition(GirderEncasedCrimschistShaftBlock.TOP, true)
                .end();

        builder.part()
                .modelFile(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath("create", "block/metal_girder_encased_shaft/block_bottom")))
//                .modelFile(AssetLookup.partialBaseModel(c, p, "bottom"))
                .addModel()
                .condition(GirderEncasedCrimschistShaftBlock.BOTTOM, true)
                .end();

    }
    public static class ChainDrive extends SpecialBlockStateGen{

        private BiFunction<BlockState, String, ModelFile> modelFunc;

        public ChainDrive(BiFunction<BlockState, String, ModelFile> modelFunc) {
            this.modelFunc = modelFunc;
        }

        @Override
        protected int getXRotation(BlockState state) {
            CrimschistChainDriveBlock.Part part = state.getValue(CrimschistChainDriveBlock.PART);
            boolean connectedAlongFirst = state.getValue(CrimschistChainDriveBlock.CONNECTED_ALONG_FIRST_COORDINATE);
            Direction.Axis axis = state.getValue(CrimschistChainDriveBlock.AXIS);

            if (part == CrimschistChainDriveBlock.Part.NONE)
                return axis == Direction.Axis.Y ? 90 : 0;
            if (axis == Direction.Axis.X)
                return (connectedAlongFirst ? 90 : 0) + (part == CrimschistChainDriveBlock.Part.START ? 180 : 0);
            if (axis == Direction.Axis.Z)
                return (connectedAlongFirst ? 0 : (part == CrimschistChainDriveBlock.Part.START ? 270 : 90));
            return 0;
        }

        @Override
        protected int getYRotation(BlockState state) {
            CrimschistChainDriveBlock.Part part = state.getValue(CrimschistChainDriveBlock.PART);
            boolean connectedAlongFirst = state.getValue(CrimschistChainDriveBlock.CONNECTED_ALONG_FIRST_COORDINATE);
            Direction.Axis axis = state.getValue(CrimschistChainDriveBlock.AXIS);

            if (part == CrimschistChainDriveBlock.Part.NONE)
                return axis == Direction.Axis.X ? 90 : 0;
            if (axis == Direction.Axis.Z)
                return (connectedAlongFirst && part == CrimschistChainDriveBlock.Part.END ? 270 : 90);
            boolean flip = part == CrimschistChainDriveBlock.Part.END && !connectedAlongFirst || part == CrimschistChainDriveBlock.Part.START && connectedAlongFirst;
            if (axis == Direction.Axis.Y)
                return (connectedAlongFirst ? 90 : 0) + (flip ? 180 : 0);
            return 0;
        }

        @Override
        public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov,
                                                    BlockState state) {
            return modelFunc.apply(state, getModelSuffix(state));
        }

        protected String getModelSuffix(BlockState state) {
            CrimschistChainDriveBlock.Part part = state.getValue(CrimschistChainDriveBlock.PART);
            Direction.Axis axis = state.getValue(CrimschistChainDriveBlock.AXIS);

            if (part == CrimschistChainDriveBlock.Part.NONE)
                return "single";

            String orientation = axis == Direction.Axis.Y ? "vertical" : "horizontal";
            String section = part == CrimschistChainDriveBlock.Part.MIDDLE ? "middle" : "end";
            return section + "_" + orientation;
        }
    }

    public static class Saw extends SpecialBlockStateGen{
        @Override
        protected int getXRotation(BlockState state) {
            return state.getValue(ChronenditeSawBlock.FACING) == Direction.DOWN ? 180 : 0;
        }

        @Override
        protected int getYRotation(BlockState state) {
            Direction facing = state.getValue(ChronenditeSawBlock.FACING);
            boolean axisAlongFirst = state.getValue(ChronenditeSawBlock.AXIS_ALONG_FIRST_COORDINATE);
            if (facing.getAxis()
                    .isVertical())
                return (axisAlongFirst ? 270 : 0) + (state.getValue(ChronenditeSawBlock.FLIPPED) ? 180 : 0);
            return horizontalAngle(facing);
        }

        @Override
        public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, BlockState state) {
            boolean vertical = state.getValue(SawBlock.FACING).getAxis().isVertical();
            String orientation = vertical ? "vertical" : "horizontal";

            BlockModelBuilder modelBuilder = prov.models().getBuilder("block/" + ctx.getName() + "/" + orientation)
                    .parent(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath("create", "block/mechanical_saw/" + orientation)))
                    .texture("gearbox_top", prov.modLoc("block/chronendite_gearbox_top"))
                    .texture("gearbox", prov.modLoc("block/chronendite_gearbox"))
                    .texture("andesite_casing_short", prov.modLoc("block/chronendite_casing_short"));
            if(vertical){
                modelBuilder = modelBuilder.texture("mechanical_saw_top", prov.modLoc("block/chronendite_mechanical_saw_top"))
                        .texture("particle", prov.modLoc("block/chronendite_mechanical_saw_top"));
            }else{
                modelBuilder = modelBuilder.texture("slit", prov.modLoc("block/chronendite_mechanical_saw_top_no_slot"))
                        .texture("particle", prov.modLoc("block/chronendite_gearbox_top"))
                        .texture("encased_belt", prov.modLoc("block/chronendite_encased_chain_drive"));
            }
            return modelBuilder;
        }
    }

    public static class Motor extends SpecialBlockStateGen{
        @Override
        protected int getXRotation(BlockState state) {
            return state.getValue(CreativeOCMotorBlock.FACING) == Direction.DOWN ? 180 : 0;
        }

        @Override
        protected int getYRotation(BlockState state) {
            return state.getValue(CreativeOCMotorBlock.FACING)
                    .getAxis()
                    .isVertical() ? 0 : horizontalAngle(state.getValue(CreativeOCMotorBlock.FACING));
        }

        @Override
        public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, BlockState state) {
            if(state.getValue(CreativeOCMotorBlock.FACING).getAxis().isVertical()){
                return prov.models().getBuilder("block/" + ctx.getName() + "/block_vertical")
                        .parent(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath("create", "block/creative_motor/block_vertical")))
                        .texture("1_0", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_axis"));
            }
            return prov.models().getBuilder("block/" + ctx.getName() + "/block")
                    .parent(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath("create", "block/creative_motor/block")))
                    .texture("1_0", ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/crimschist_axis"));
        }
    }

    public static class Belt extends SpecialBlockStateGen{

        @Override
        protected int getXRotation(BlockState state) {
            Direction direction = state.getValue(OCBeltBlock.HORIZONTAL_FACING);
            BeltSlope slope = state.getValue(OCBeltBlock.SLOPE);
            return slope == BeltSlope.VERTICAL ? 90
                    : slope == BeltSlope.SIDEWAYS && direction.getAxisDirection() == Direction.AxisDirection.NEGATIVE ? 180 : 0;
        }

        @Override
        protected int getYRotation(BlockState state) {
            Boolean casing = state.getValue(OCBeltBlock.CASING);
            BeltSlope slope = state.getValue(OCBeltBlock.SLOPE);

            boolean flip = slope == BeltSlope.UPWARD;
            boolean rotate = casing && slope == BeltSlope.VERTICAL;
            Direction direction = state.getValue(OCBeltBlock.HORIZONTAL_FACING);
            return horizontalAngle(direction) + (flip ? 180 : 0) + (rotate ? 90 : 0);
        }

        @Override
        public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov,
                                                    BlockState state) {
            Boolean casing = state.getValue(OCBeltBlock.CASING);

            if (!casing) {
                return prov.models().getBuilder("block/" + ctx.getName() + "/particle").parent(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath("create", "block/belt/particle")));
            }

            BeltPart part = state.getValue(OCBeltBlock.PART);
            Direction direction = state.getValue(OCBeltBlock.HORIZONTAL_FACING);
            BeltSlope slope = state.getValue(OCBeltBlock.SLOPE);
            boolean downward = slope == BeltSlope.DOWNWARD;
            boolean diagonal = slope == BeltSlope.UPWARD || downward;
            boolean vertical = slope == BeltSlope.VERTICAL;
            boolean pulley = part == BeltPart.PULLEY;
            boolean sideways = slope == BeltSlope.SIDEWAYS;
            boolean negative = direction.getAxisDirection() == Direction.AxisDirection.NEGATIVE;

            if (!casing && pulley)
                part = BeltPart.MIDDLE;

            if ((vertical && negative || downward || sideways && negative) && part != BeltPart.MIDDLE && !pulley)
                part = part == BeltPart.END ? BeltPart.START : BeltPart.END;

            if (!casing && vertical)
                slope = BeltSlope.HORIZONTAL;
            if (casing && vertical)
                slope = BeltSlope.SIDEWAYS;

            String path = "block/" + (casing ? "belt_casing/" : "belt/");
            String myPath = "block/" + (casing ? "speeding_belt_casing/" : "speeding_belt/");
            String slopeName = slope.getSerializedName();
            String partName = part.getSerializedName();

            if (diagonal)
                slopeName = "diagonal";

            return prov.models().getBuilder(myPath + slopeName + "_" + partName).parent(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath("create", path + slopeName + "_" + partName)));
        }
    }

    public static class SpeedController extends SpecialBlockStateGen{

        @Override
        protected int getYRotation(BlockState state) {
            var axis = state.getValue(ChronenditeSpeedControllerBlock.AXIS);
            var facing = ChronenditeSpeedControllerBlock.getDirectionFromState(state);
            if(axis.isHorizontal()){
                if(facing.getAxis().isHorizontal()){
                    return switch (facing){
                        case NORTH -> -90;
                        case SOUTH -> 90;
                        case WEST -> 180;
                        default -> 0;
                    };
                }
                return axis == Direction.Axis.Z ? 0 : 90;
            }
            return facing.getAxis() == Direction.Axis.X ? 90 : 0;
        }

        @Override
        protected int getXRotation(BlockState state) {
            var axis = state.getValue(ChronenditeSpeedControllerBlock.AXIS);
            var facing = ChronenditeSpeedControllerBlock.getDirectionFromState(state);
            if(axis.isHorizontal()){
                if(facing == Direction.UP) return 0;
                if(facing == Direction.DOWN) return 180;
                return 0;
            }else{
                return switch (facing){
                    case NORTH, EAST -> 90;
                    case SOUTH, WEST -> -90;
                    default -> 0;
                };
            }
        }

        @Override
        public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, BlockState state) {
            var axis = state.getValue(ChronenditeSpeedControllerBlock.AXIS);
            var facing = ChronenditeSpeedControllerBlock.getDirectionFromState(state);
            if(axis.isHorizontal() && facing.getAxis().isHorizontal()){
                return prov.models().getBuilder("block/" + ctx.getName() + "/block_horizontal")
                        .parent(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/rotation_speed_controller/block_horizontal")))
                        .texture("0", prov.modLoc("block/chronendite_rotation_speed_controller"))
                        .texture("5", prov.modLoc("block/chronendite_rotation_speed_controller_top"))
                        .texture("6", prov.modLoc("block/chronendite_block"))
                        .texture("7", prov.modLoc("block/chronendite_funnel_neutral"))
                        .texture("particle", prov.modLoc("block/chronendite_block"));
            }
            return prov.models().getBuilder("block/" + ctx.getName() + "/block")
                    .parent(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath("create", "block/rotation_speed_controller/block")))
                    .texture("0", prov.modLoc("block/chronendite_rotation_speed_controller"))
                    .texture("4", prov.modLoc("block/chronendite_gearbox"))
                    .texture("5", prov.modLoc("block/chronendite_rotation_speed_controller_top"))
                    .texture("6", prov.modLoc("block/chronendite_block"))
                    .texture("7", prov.modLoc("block/chronendite_funnel_neutral"))
                    .texture("particle", prov.modLoc("block/chronendite_block"));
        }
    }

    public static class FluidTank extends SpecialBlockStateGen{

        @Override
        protected int getXRotation(BlockState state) {
            return 0;
        }

        @Override
        protected int getYRotation(BlockState state) {
            Boolean top = state.getValue(CrimschistFluidTankBlock.TOP);
            Boolean bottom = state.getValue(CrimschistFluidTankBlock.BOTTOM);
            if(top || bottom) return 0;
            FluidTankBlock.Shape shape = state.getValue(CrimschistFluidTankBlock.SHAPE);
            boolean isLargeWindow = state.getValue(CrimschistFluidTankBlock.LARGE_WINDOW);
            Direction largeWindowDirection = state.getValue(CrimschistFluidTankBlock.LARGE_WINDOW_DIRECTION);
            if(shape != FluidTankBlock.Shape.WINDOW || !isLargeWindow) return 0;
            return (int) largeWindowDirection.toYRot() - 180;
        }

        @Override
        public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, BlockState state) {
            Boolean top = state.getValue(CrimschistFluidTankBlock.TOP);
            Boolean bottom = state.getValue(CrimschistFluidTankBlock.BOTTOM);
            FluidTankBlock.Shape shape = state.getValue(CrimschistFluidTankBlock.SHAPE);
            boolean isLargeWindow = state.getValue(CrimschistFluidTankBlock.LARGE_WINDOW);
            Direction largeWindowDirection = state.getValue(CrimschistFluidTankBlock.LARGE_WINDOW_DIRECTION);

            String shapeName = "middle";
            if (top && bottom)
                shapeName = "single";
            else if (top)
                shapeName = "top";
            else if (bottom)
                shapeName = "bottom";

            String modelName = shapeName + (shape == FluidTankBlock.Shape.PLAIN ? "" : "_" + shape.getSerializedName());

            if(shape == FluidTankBlock.Shape.WINDOW && isLargeWindow){
                String s;
                if(!top && !bottom){
                    s = "n";
                }else {
                    s = largeWindowDirection.getSerializedName().substring(0, 1);
                }
                return AssetLookup.partialBaseModel(ctx, prov, modelName, s);
            }

            return prov.models().getBuilder("block/crimschist_fluid_tank/block_" + modelName)
                    .parent(AssetLookupWithParent.anyParent(ResourceLocation.fromNamespaceAndPath("create", "block/fluid_tank/block_" + modelName)))
                    .texture("0", prov.modLoc("block/crimschist_fluid_tank_top"))
                    .texture("1", prov.modLoc("block/crimschist_fluid_tank"))
                    .texture("3", prov.modLoc("block/crimschist_fluid_tank_window"))
                    .texture("4", prov.modLoc("block/crimschist_fluid_tank_inner"))
                    .texture("5", prov.modLoc("block/crimschist_fluid_tank_window_single"))
                    .texture("particle", prov.modLoc("block/crimschist_fluid_tank"));
        }
    }

    public static class InfusionVat extends SpecialBlockStateGen{
        @Override
        protected int getXRotation(BlockState state) {
            return 0;
        }

        @Override
        protected int getYRotation(BlockState state) {
            Boolean top = state.getValue(InfusionVatBlock.TOP);
            Boolean bottom = state.getValue(InfusionVatBlock.BOTTOM);
            if(top || bottom) return 0;
            boolean east = state.getValue(InfusionVatBlock.EAST);
            boolean west = state.getValue(InfusionVatBlock.WEST);
            boolean south = state.getValue(InfusionVatBlock.SOUTH);
            boolean north = state.getValue(InfusionVatBlock.NORTH);
            if(north) return 0;
            if(east && !west && !south) return 90;
            if(south && !east && !west) return 180;
            if(west && !east && !south) return 270;
            return 0;
        }

        @Override
        public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, BlockState state) {
            Boolean top = state.getValue(InfusionVatBlock.TOP);
            Boolean bottom = state.getValue(InfusionVatBlock.BOTTOM);
            String shapeName = "middle";
            if (top && bottom)
                shapeName = "single";
            else if (top)
                shapeName = "top";
            else if (bottom)
                shapeName = "bottom";
            boolean east = state.getValue(InfusionVatBlock.EAST);
            boolean west = state.getValue(InfusionVatBlock.WEST);
            boolean south = state.getValue(InfusionVatBlock.SOUTH);
            boolean north = state.getValue(InfusionVatBlock.NORTH);

            String corner = "";
            boolean single = north && east && south && west;
            boolean center = !north && !east && !south && !west;
            if(center) {
                corner = "c";
            }
            else if (!single) {
                if (north) corner += "n";
                else if (south && (!east || !west)) corner += "s";
                if (east) corner += "e";
                if (south && (north || east && west)) corner += "s";
                if (west) corner += "w";
            }
            if(!top && !bottom && corner.length() == 1 && !corner.equals("c")){
                corner = "n"; // either 'n', 'e', 's', or 'w'. simplify to 'n'
            }
            String modelName = shapeName + (corner.isEmpty() ? "" : "_" + corner);
            if(!bottom && center) return AssetLookupWithParent.specifiedModel(ctx, prov, "block/" + ctx.getName() + "/block_" + modelName, "minecraft", "block/air", Map.ofEntries(
                    Map.entry("particle", ResourceLocation.parse("create_andertech:block/crimschist_fluid_tank"))
            ));
            return AssetLookup.partialBaseModel(ctx, prov, modelName);
        }
    }
}
