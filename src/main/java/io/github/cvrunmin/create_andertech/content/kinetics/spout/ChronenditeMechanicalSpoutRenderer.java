package io.github.cvrunmin.create_andertech.content.kinetics.spout;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.platform.NeoForgeCatnipServices;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.FluidStack;

public class ChronenditeMechanicalSpoutRenderer extends KineticBlockEntityRenderer<ChronenditeMechanicalSpoutBlockEntity> {
    public ChronenditeMechanicalSpoutRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }
    static final PartialModel[] BITS =
            { AllPartialModels.SPOUT_TOP, AllPartialModels.SPOUT_MIDDLE, AllPartialModels.SPOUT_BOTTOM };

    @Override
    protected void renderSafe(ChronenditeMechanicalSpoutBlockEntity blockEntity, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(blockEntity, partialTicks, ms, buffer, light, overlay);

        FluidStack fluidStack = blockEntity.getCurrentFluidInTank();
        int processingTicks = blockEntity.processingTime;
        float processingPT = processingTicks - partialTicks * blockEntity.getProcessingSpeed();
        float processingProgress = 1 - (processingPT - 50) / 100;
        processingProgress = Mth.clamp(processingProgress, 0, 1);
        float radius = 0;

        if (!fluidStack.isEmpty() && processingTicks != -1) {
            radius = (float) (Math.pow(((2 * processingProgress) - 1), 2) - 1);
            AABB bb = new AABB(0.5, 0.0, 0.5, 0.5, -1.2, 0.5).inflate(radius / 32f);
            NeoForgeCatnipServices.FLUID_RENDERER.renderFluidBox(fluidStack, (float) bb.minX, (float) bb.minY, (float) bb.minZ,
                    (float) bb.maxX, (float) bb.maxY, (float) bb.maxZ, buffer, ms, light, true, true);
        }

        float squeeze = radius;
        if (processingPT < 0)
            squeeze = 0;
        else if (processingPT < 20)
            squeeze = Mth.lerp(processingPT / 2f, 0, -1);
        else if (processingPT < 100)
            squeeze = -1;

        ms.pushPose();
        for (PartialModel bit : BITS) {
            CachedBuffers.partial(bit, blockEntity.getBlockState())
                    .light(light)
                    .renderInto(ms, buffer.getBuffer(RenderType.solid()));
            ms.translate(0, -3 * squeeze / 32f, 0);
        }
        ms.popPose();
    }

    @Override
    protected BlockState getRenderedBlockState(ChronenditeMechanicalSpoutBlockEntity be) {
        return shaft(getRotationAxisOf(be));
    }
}
