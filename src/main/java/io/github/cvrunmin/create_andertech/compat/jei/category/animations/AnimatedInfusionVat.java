package io.github.cvrunmin.create_andertech.compat.jei.category.animations;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import io.github.cvrunmin.create_andertech.index.AllBlocks;
import net.createmod.catnip.gui.UIRenderHelper;
import net.createmod.catnip.platform.NeoForgeCatnipServices;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.fluids.FluidStack;

public class AnimatedInfusionVat extends AnimatedKinetics {

    private FluidStack fluid;

    public AnimatedInfusionVat withFluid(FluidStack fluid) {
        this.fluid = fluid;
        return this;
    }

    @Override
    public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset) {
        PoseStack matrixStack = guiGraphics.pose();
        matrixStack.pushPose();
        matrixStack.translate(xOffset, yOffset, 100);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5f));
        matrixStack.mulPose(Axis.YP.rotationDegrees(22.5f));
        int scale = 24;
        AnimatedKinetics.defaultBlockElement(AllBlocks.INFUSION_VAT.getDefaultState())
                .scale(scale)
                .render(guiGraphics);

        UIRenderHelper.flipForGuiRender(matrixStack);
        matrixStack.scale(scale, scale, scale);
        float baseHeight = 3 / 16f;
        float tankHullWidth = 2 / 16f;
        float minPuddleHeight = 1 / 16f;
        float totalHeight = 1 - baseHeight - minPuddleHeight;
        float xMin = tankHullWidth;
        float xMax = xMin + 1 - 2 * tankHullWidth;
        float zMin = tankHullWidth;
        float zMax = zMin + 1 - 2 * tankHullWidth;
        float yMin = baseHeight;
        NeoForgeCatnipServices.FLUID_RENDERER.renderFluidBox(fluid, xMin, yMin, zMin, xMax, totalHeight, zMax, guiGraphics.bufferSource(), matrixStack, LightTexture.FULL_BRIGHT, false, true);
        guiGraphics.flush();

        matrixStack.popPose();
    }
}
