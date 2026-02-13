package io.github.cvrunmin.create_andertech.content.kinetics.simpleRelays;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import io.github.cvrunmin.create_andertech.index.AllBlocks;
import io.github.cvrunmin.create_andertech.index.CAAllPartialModels;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class BracketedOCKineticBlockEntityRenderer extends KineticBlockEntityRenderer<BracketedOCKineticBlockEntity> {
    public BracketedOCKineticBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(BracketedOCKineticBlockEntity be, float partialTicks, PoseStack ms,
                              MultiBufferSource buffer, int light, int overlay) {

        if (VisualizationManager.supportsVisualization(be.getLevel()))
            return;

        if (!AllBlocks.CRIMSCHIST_LARGE_COGWHEEL.has(be.getBlockState())) {
            super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
            return;
        }

        // Large cogs sometimes have to offset their teeth by 11.25 degrees in order to
        // mesh properly

        VertexConsumer vc = buffer.getBuffer(RenderType.solid());
        Direction.Axis axis = getRotationAxisOf(be);
        Direction facing = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);
        renderRotatingBuffer(be,
                CachedBuffers.partialFacingVertical(CAAllPartialModels.SHAFTLESS_LARGE_CRIMSCHIST_COGWHEEL, be.getBlockState(), facing),
                ms, vc, light);

        float angle = getAngleForLargeCogShaft(be, axis);
        SuperByteBuffer shaft =
                CachedBuffers.partialFacingVertical(CAAllPartialModels.CRIMSCHIST_COGWHEEL_SHAFT, be.getBlockState(), facing);
        kineticRotationTransform(shaft, be, axis, angle, light);
        shaft.renderInto(ms, vc);

    }

    public static float getAngleForLargeCogShaft(SimpleOCKineticBlockEntity be, Direction.Axis axis) {
        BlockPos pos = be.getBlockPos();
        float offset = getShaftAngleOffset(axis, pos);
        float time = AnimationTickHolder.getRenderTime(be.getLevel());
        float angle = ((time * be.getSpeed() * 3f / 10 + offset) % 360) / 180 * (float) Math.PI;
        return angle;
    }

    public static float getShaftAngleOffset(Direction.Axis axis, BlockPos pos) {
        if (KineticBlockEntityVisual.shouldOffset(axis, pos)) {
            return 22.5f;
        } else {
            return 0;
        }
    }
}
