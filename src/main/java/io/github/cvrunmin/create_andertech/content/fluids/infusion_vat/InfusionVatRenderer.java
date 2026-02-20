package io.github.cvrunmin.create_andertech.content.fluids.infusion_vat;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.platform.NeoForgeCatnipServices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;

public class InfusionVatRenderer extends SmartBlockEntityRenderer<InfusionVatBlockEntity> {
    public InfusionVatRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(InfusionVatBlockEntity blockEntity, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(blockEntity, partialTicks, ms, buffer, light, overlay);
        if (!blockEntity.isController())
            return;

        float fluidLevel = renderFluids(blockEntity, partialTicks, ms, buffer, light, overlay);
        float level = Mth.clamp(fluidLevel - .3f, .125f, .6f);
        BlockPos pos = blockEntity.getBlockPos();
        // TODO: utilize the whole vat for item rendering
        ms.pushPose();
        float vatXHalf = blockEntity.getXLength() / 2.f;
        float vatZHalf = blockEntity.getXLength() / 2.f;
        ms.translate(vatXHalf, .2f, vatZHalf);

        RandomSource r = RandomSource.create(pos.hashCode());
        Vec3 baseVector = new Vec3(.125, level, 0);
        IItemHandlerModifiable itemCap = blockEntity.itemCapability; // only controller BE will run this code. Direct access is good
        if(itemCap == null) itemCap = new ItemStackHandler();
        int itemCount = 0;
        for (int slot = 0; slot < itemCap.getSlots(); slot++)
            if (!itemCap.getStackInSlot(slot).isEmpty())
                itemCount++;

        if (itemCount == 1)
            baseVector = new Vec3(0, level, 0);

        float anglePartition = 360f / itemCount;
        for (int slot = 0; slot < itemCap.getSlots(); slot++) {
            ItemStack stack = itemCap.getStackInSlot(slot);
            if (stack.isEmpty())
                continue;

            ms.pushPose();

            if (fluidLevel > 0) {
                ms.translate(0,
                        (Mth.sin(
                                AnimationTickHolder.getRenderTime(blockEntity.getLevel()) / 12f + anglePartition * itemCount) + 1.5f)
                                * 1 / 32f,
                        0);
            }

            Vec3 itemPosition = VecHelper.rotate(baseVector, anglePartition * itemCount, Direction.Axis.Y);
            ms.translate(itemPosition.x, itemPosition.y, itemPosition.z);
            TransformStack.of(ms)
                    .rotateYDegrees(anglePartition * itemCount + 35)
                    .rotateXDegrees(65);

            for (int i = 0; i <= stack.getCount() / 8; i++) {
                ms.pushPose();

                Vec3 vec = VecHelper.offsetRandomly(Vec3.ZERO, r, 1 / 16f);

                ms.translate(vec.x, vec.y, vec.z);
                renderItem(ms, buffer, light, overlay, stack);
                ms.popPose();
            }
            ms.popPose();

            itemCount--;
        }
        ms.popPose();
    }

    protected void renderItem(PoseStack ms, MultiBufferSource buffer, int light, int overlay, ItemStack stack) {
        Minecraft mc = Minecraft.getInstance();
        mc.getItemRenderer()
                .renderStatic(stack, ItemDisplayContext.GROUND, light, overlay, ms, buffer, mc.level, 0);
    }

    private float renderFluids(InfusionVatBlockEntity blockEntity, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        InfusionVatFluidTankBehaviour inputTank = blockEntity.getBehaviour(InfusionVatFluidTankBehaviour.INPUT);
        InfusionVatFluidTankBehaviour outputTank = blockEntity.getBehaviour(InfusionVatFluidTankBehaviour.OUTPUT);

        float totalUnits = blockEntity.getTotalFluidUnits(partialTicks);
        if(totalUnits < 1) return 0;

        float baseHeight = 3 / 16f;
        float tankHullWidth = 2 / 16f;
        float minPuddleHeight = 1 / 16f;
        float totalHeight = blockEntity.height - baseHeight - minPuddleHeight;

        if(totalUnits < 1 / (512f * totalHeight)) return 0;

        float xMin = tankHullWidth;
        float xMax = xMin + blockEntity.xLen - 2 * tankHullWidth;
        float zMin = tankHullWidth;
        float zMax = zMin + blockEntity.zLen - 2 * tankHullWidth;
        float inputUnit = inputTank.tank.getTotalUnits(partialTicks);
        float outputUnit = outputTank.tank.getTotalUnits(partialTicks);
        float renderingLevel = Math.max(inputUnit, outputUnit) / blockEntity.getExpectedFluidCapacity(blockEntity.getBlockAmount());
        float clampedTotalLevel = Mth.clamp(renderingLevel * totalHeight, 0, totalHeight);
        float yMin = baseHeight;
        float yIMax = yMin + clampedTotalLevel * (inputUnit / (inputUnit + outputUnit));
        float yOMax = yMin + clampedTotalLevel;

        if(inputTank.tank.getRenderedFluid() != FluidStack.EMPTY){
            NeoForgeCatnipServices.FLUID_RENDERER.renderFluidBox(inputTank.tank.getRenderedFluid(), xMin, yMin, zMin, xMax, yIMax, zMax, buffer,
                    ms, light, false, false);
        }

        if(outputTank.tank.getRenderedFluid() != FluidStack.EMPTY){
            NeoForgeCatnipServices.FLUID_RENDERER.renderFluidBox(outputTank.tank.getRenderedFluid(), xMin, yIMax, zMin, xMax, yOMax, zMax, buffer,
                    ms, light, false, false);
        }
        return yOMax;
    }
}
