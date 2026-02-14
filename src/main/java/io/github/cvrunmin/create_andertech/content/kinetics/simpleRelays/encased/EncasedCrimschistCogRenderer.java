package io.github.cvrunmin.create_andertech.content.kinetics.simpleRelays.encased;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import io.github.cvrunmin.create_andertech.content.kinetics.simpleRelays.BracketedOCKineticBlockEntityRenderer;
import io.github.cvrunmin.create_andertech.content.kinetics.simpleRelays.SimpleOCKineticBlockEntity;
import io.github.cvrunmin.create_andertech.index.CAAllPartialModels;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class EncasedCrimschistCogRenderer extends KineticBlockEntityRenderer<SimpleOCKineticBlockEntity> {
    private final boolean large;

    public static EncasedCrimschistCogRenderer small(BlockEntityRendererProvider.Context context){
        return new EncasedCrimschistCogRenderer(context, false);
    }

    public static EncasedCrimschistCogRenderer large(BlockEntityRendererProvider.Context context){
        return new EncasedCrimschistCogRenderer(context, true);
    }

    public EncasedCrimschistCogRenderer(BlockEntityRendererProvider.Context context, boolean large) {
        super(context);
        this.large = large;
    }

    @Override
    protected void renderSafe(SimpleOCKineticBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
        if (VisualizationManager.supportsVisualization(be.getLevel()))
            return;

        BlockState blockState = be.getBlockState();
        Block block = blockState.getBlock();
        if (!(block instanceof IRotate def))
            return;

        Direction.Axis axis = getRotationAxisOf(be);
        BlockPos pos = be.getBlockPos();
        float angle = large ? BracketedOCKineticBlockEntityRenderer.getAngleForLargeCogShaft(be, axis)
                : getAngleForBe(be, pos, axis);

        for (Direction d : Iterate.directionsInAxis(getRotationAxisOf(be))) {
            if (!def.hasShaftTowards(be.getLevel(), be.getBlockPos(), blockState, d))
                continue;
            SuperByteBuffer shaft = CachedBuffers.partialFacing(CAAllPartialModels.CRIMSCHIST_SHAFT_HALF, be.getBlockState(), d);
            kineticRotationTransform(shaft, be, axis, angle, light);
            shaft.renderInto(ms, buffer.getBuffer(RenderType.solid()));
        }
    }

    @Override
    protected SuperByteBuffer getRotatedModel(SimpleOCKineticBlockEntity be, BlockState state) {
        return CachedBuffers.partialFacingVertical(
                large ? CAAllPartialModels.SHAFTLESS_LARGE_CRIMSCHIST_COGWHEEL : CAAllPartialModels.SHAFTLESS_CRIMSCHIST_COGWHEEL, state,
                Direction.fromAxisAndDirection(state.getValue(EncasedCrimschistCogwheelBlock.AXIS), Direction.AxisDirection.POSITIVE));
    }
}
