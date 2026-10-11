package io.github.cvrunmin.create_andertech.content.contraptions.actors.harvester;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import io.github.cvrunmin.create_andertech.index.CAAllPartialModels;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class CrimschistHarvesterRenderer extends SafeBlockEntityRenderer<CrimschistHarvesterBlockEntity> {
    public CrimschistHarvesterRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    protected void renderSafe(CrimschistHarvesterBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        BlockState blockState = be.getBlockState();
        SuperByteBuffer superBuffer = CachedBuffers.partial(CAAllPartialModels.CRIMSCHIST_HARVESTER_BLADE, blockState);
        transform(be.getLevel(), superBuffer, be.getAnimatedSpeed());
        superBuffer.light(light)
                .renderInto(ms, bufferSource.getBuffer(RenderType.cutoutMipped()));
    }

    public static void transform(Level world, SuperByteBuffer superBuffer, float speed) {
        float time = AnimationTickHolder.getRenderTime(world) / 20;
        float angle = (time * speed) % 360;

        superBuffer.rotate(AngleHelper.rad(angle), Direction.UP);
    }

    public static void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld,
                                           ContraptionMatrices matrices, MultiBufferSource buffers) {
        BlockState blockState = context.state;
        Direction facing = blockState.getValue(HORIZONTAL_FACING);
        SuperByteBuffer superBuffer = CachedBuffers.partial(CAAllPartialModels.CRIMSCHIST_HARVESTER_BLADE, blockState);
        float speed = !VecHelper.isVecPointingTowards(context.relativeMotion, Direction.UP)
                ? context.getAnimationSpeed()
                : 0;
        if (context.contraption.stalled)
            speed = 0;

        superBuffer.transform(matrices.getModel());
        transform(context.world, superBuffer, speed);

        superBuffer.light(LevelRenderer.getLightColor(renderWorld, context.localPos))
                .useLevelLight(context.world, matrices.getWorld())
                .renderInto(matrices.getViewProjection(), buffers.getBuffer(RenderType.cutoutMipped()));
    }
}
