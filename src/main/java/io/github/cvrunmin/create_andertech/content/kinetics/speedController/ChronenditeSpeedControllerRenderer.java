package io.github.cvrunmin.create_andertech.content.kinetics.speedController;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ChronenditeSpeedControllerRenderer extends SmartBlockEntityRenderer<ChronenditeSpeedControllerBlockEntity> {
    public ChronenditeSpeedControllerRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(ChronenditeSpeedControllerBlockEntity blockEntity, float partialTicks, PoseStack ms,
                              MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(blockEntity, partialTicks, ms, buffer, light, overlay);

        VertexConsumer builder = buffer.getBuffer(Sheets.solidBlockSheet());
        if (!VisualizationManager.supportsVisualization(blockEntity.getLevel())) {
            KineticBlockEntityRenderer.renderRotatingBuffer(blockEntity, getRotatedModel(blockEntity), ms, builder, light);
        }

        if (!blockEntity.hasBracket)
            return;

        BlockPos pos = blockEntity.getBlockPos();
        Level world = blockEntity.getLevel();
        BlockState blockState = blockEntity.getBlockState();
        Direction facing = ChronenditeSpeedControllerBlock.getDirectionFromState(blockState);
        Direction.Axis axis = blockState.getValue(ChronenditeSpeedControllerBlock.AXIS);
        SuperByteBuffer bracket = CachedBuffers.partial(AllPartialModels.SPEED_CONTROLLER_BRACKET, blockState);
        bracket.translate(facing.getNormal());
//        bracket.rotateTo(Direction.UP, facing);
        if(facing.getAxis() == Direction.Axis.Y){
            if(facing == Direction.DOWN){
                bracket.rotateCentered(((float) Math.PI), Direction.Axis.X);
            }
            bracket.rotateCentered((float) (axis == Direction.Axis.X ? Math.PI : Math.PI / 2), facing);
        } else if (facing.getAxis() == Direction.Axis.X) {
            bracket.rotateCentered((float) (Math.PI / 2 * -1 * facing.getAxisDirection().getStep()), Direction.Axis.Z);
            if(axis.isHorizontal()){
            bracket.rotateCentered((float) (Math.PI / 2), Direction.Axis.Y);
            }
        }else{
            bracket.rotateCentered((float) (Math.PI / 2 * facing.getAxisDirection().getStep()), Direction.Axis.X);
            if(axis.isVertical()){
            bracket.rotateCentered((float) (Math.PI / 2), Direction.Axis.Y);
            }
        }
        bracket.light(LevelRenderer.getLightColor(world, pos.relative(facing)));
        bracket.renderInto(ms, builder);
    }

    private SuperByteBuffer getRotatedModel(ChronenditeSpeedControllerBlockEntity blockEntity) {
        return CachedBuffers.block(KineticBlockEntityRenderer.KINETIC_BLOCK,
                KineticBlockEntityRenderer.shaft(KineticBlockEntityRenderer.getRotationAxisOf(blockEntity)));
    }
}
