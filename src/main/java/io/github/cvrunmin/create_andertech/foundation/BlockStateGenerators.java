package io.github.cvrunmin.create_andertech.foundation;

import com.simibubi.create.content.decoration.girder.GirderEncasedShaftBlock;
import com.simibubi.create.content.kinetics.chainDrive.ChainDriveBlock;
import com.simibubi.create.content.kinetics.motor.CreativeMotorBlock;
import com.simibubi.create.content.kinetics.saw.SawBlock;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import io.github.cvrunmin.create_andertech.content.decoration.GirderEncasedCrimschistShaftBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.chainDrive.CrimschistChainDriveBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.motor.CreativeOCMotorBlock;
import io.github.cvrunmin.create_andertech.content.kinetics.saw.ChronenditeSawBlock;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;

import java.util.function.BiFunction;

public class BlockStateGenerators {

    public static void griderBlockStateWithShaft(DataGenContext<Block, GirderEncasedCrimschistShaftBlock> c,
                                           RegistrateBlockstateProvider p) {
        MultiPartBlockStateBuilder builder = p.getMultipartBuilder(c.get());

        builder.part()
                .modelFile(new ModelFile.UncheckedModelFile(new ResourceLocation("create", "block/metal_girder_encased_shaft/block")))
//                .modelFile(AssetLookup.partialBaseModel(c, p))
                .rotationY(0)
                .addModel()
                .condition(GirderEncasedCrimschistShaftBlock.HORIZONTAL_AXIS, Direction.Axis.Z)
                .end();

        builder.part()
                .modelFile(new ModelFile.UncheckedModelFile(new ResourceLocation("create", "block/metal_girder_encased_shaft/block")))
//                .modelFile(AssetLookup.partialBaseModel(c, p))
                .rotationY(90)
                .addModel()
                .condition(GirderEncasedCrimschistShaftBlock.HORIZONTAL_AXIS, Direction.Axis.X)
                .end();

        builder.part()
                .modelFile(new ModelFile.UncheckedModelFile(new ResourceLocation("create", "block/metal_girder_encased_shaft/block_top")))
//                .modelFile(AssetLookup.partialBaseModel(c, p, "top"))
                .addModel()
                .condition(GirderEncasedCrimschistShaftBlock.TOP, true)
                .end();

        builder.part()
                .modelFile(new ModelFile.UncheckedModelFile(new ResourceLocation("create", "block/metal_girder_encased_shaft/block_bottom")))
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
            String orientation = state.getValue(SawBlock.FACING)
                    .getAxis()
                    .isVertical() ? "vertical" : "horizontal";
            return new ModelFile.UncheckedModelFile(new ResourceLocation("create", "block/mechanical_saw/" + orientation));
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
            return state.getValue(CreativeMotorBlock.FACING)
                    .getAxis()
                    .isVertical() ? new ModelFile.UncheckedModelFile(new ResourceLocation("create", "block/creative_motor/block_vertical"))
                    : new ModelFile.UncheckedModelFile(new ResourceLocation("create", "block/creative_motor/block"));
        }
    }
}
