package io.github.cvrunmin.create_andertech.content.kinetics.transmission;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import io.github.cvrunmin.create_andertech.content.kinetics.simpleRelays.BracketedOCKineticBlockEntityRenderer;
import io.github.cvrunmin.create_andertech.index.CAAllPartialModels;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class CrimschistBlackboxDifferentialRenderer extends KineticBlockEntityRenderer<CrimschistBlackboxDifferentialBlockEntity> {

    public CrimschistBlackboxDifferentialRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(CrimschistBlackboxDifferentialBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {
        if (VisualizationManager.supportsVisualization(be.getLevel()))
            return;

        BlockState blockState = be.getBlockState();
        if (!(blockState.getBlock() instanceof CrimschistBlackboxDifferentialBlock block))
            return;

        Direction.Axis axis = getRotationAxisOf(be);

        SuperByteBuffer ring = CachedBuffers.partialFacingVertical(CAAllPartialModels.SHAFTLESS_LARGE_CRIMSCHIST_COGWHEEL,
                blockState, Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE));
        kineticRotationTransform(ring, be, axis,
                angleFor(be, axis, be.portSpeed(CrimschistBlackboxDifferentialBlock.Port.COG), false), light);
        ring.renderInto(ms, buffer.getBuffer(RenderType.solid()));

        for (Direction direction : Iterate.directionsInAxis(axis)) {
            if (!block.hasShaftTowards(be.getLevel(), be.getBlockPos(), blockState, direction))
                continue;

            float speed = be.portSpeed(CrimschistBlackboxDifferentialBlock.portForFace(blockState, direction));
            SuperByteBuffer shaft = CachedBuffers.partialFacing(CAAllPartialModels.CRIMSCHIST_SHAFT_HALF, blockState, direction);
            kineticRotationTransform(shaft, be, axis, angleFor(be, axis, speed, true), light);
            shaft.renderInto(ms, buffer.getBuffer(RenderType.solid()));
        }
    }

    /**
     * The renderers' phase math, with an explicit speed instead of the block entity's own: the ring uses the
     * generic position offset, a large cogwheel's shaft halves use the bracketed one.
     */
    private static float angleFor(KineticBlockEntity be, Direction.Axis axis, float speed, boolean bracketedShaftPhase) {
        float offset = bracketedShaftPhase
                ? BracketedOCKineticBlockEntityRenderer.getShaftAngleOffset(axis, be.getBlockPos())
                : getRotationOffsetForPosition(be, be.getBlockPos(), axis);
        float time = AnimationTickHolder.getRenderTime(be.getLevel());
        return ((time * speed * 3f / 10 + offset) % 360) / 180f * (float) Math.PI;
    }
}
