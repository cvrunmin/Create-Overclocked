package io.github.cvrunmin.create_andertech.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.logistics.stockTicker.StockKeeperRequestMenu;
import com.simibubi.create.content.logistics.stockTicker.StockKeeperRequestScreen;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import io.github.cvrunmin.create_andertech.content.processing.burner.ChronendicBlazeBurnerBlockEntity;
import io.github.cvrunmin.create_andertech.content.processing.burner.ChronendicBlazeBurnerRenderer;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.ref.WeakReference;

@Debug(export = true)
@Mixin(StockKeeperRequestScreen.class)
public abstract class StockKeeperRequestScreenMixin extends Screen {
    @Shadow
    private StockTickerBlockEntity blockEntity;
    @Shadow
    private int windowHeight;
    @Unique
    private WeakReference<ChronendicBlazeBurnerBlockEntity> chronendicBlaze = new WeakReference<>(null);

    protected StockKeeperRequestScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void ctorTrySearchChronendicBlaze(StockKeeperRequestMenu container, Inventory inv, Component title, CallbackInfo ci){
        for (int yOffset : Iterate.zeroAndOne) {
            for (Direction side : Iterate.horizontalDirections) {
                BlockPos seatPos = blockEntity.getBlockPos().below(yOffset).relative(side);
                if (yOffset == 0 && blockEntity.getLevel()
                        .getBlockEntity(seatPos) instanceof ChronendicBlazeBurnerBlockEntity bbbe) {
                    chronendicBlaze = new WeakReference<>(bbbe);
                    return;
                }
            }
        }
    }

    @WrapWithCondition(method = "containerTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;closeContainer()V"))
    private boolean containerTickAlsoCheckChronendicBlaze(Player instance){
        ChronendicBlazeBurnerBlockEntity blazeBurnerBlockEntity = chronendicBlaze.get();
        return blazeBurnerBlockEntity == null || blazeBurnerBlockEntity.isRemoved();
    }

    @Inject(method = "renderBg", at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/logistics/stockTicker/StockKeeperRequestScreen;encodeRequester:Z", opcode = Opcodes.GETFIELD, ordinal = 0, shift = At.Shift.BEFORE))
    private void injectRenderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY, CallbackInfo ci,
                                @Local(name = "ms")PoseStack ms, @Local(name = "x") int x, @Local(name = "y") int y){
        ChronendicBlazeBurnerBlockEntity keeperBE = chronendicBlaze.get();
        if(keeperBE != null && !keeperBE.isRemoved()){
            ms.pushPose();
            int entityX = x - 35;
            int entityY = y + windowHeight - 43;
            ms.translate(entityX, entityY, -0);
            ms.mulPose(Axis.XP.rotationDegrees(-22.5f));
            ms.mulPose(Axis.YP.rotationDegrees(-45));
            ms.scale(48, -48, 48);
            float animation = keeperBE.headAnimation.getValue(AnimationTickHolder.getPartialTicks()) * .175f;
            float horizontalAngle = AngleHelper.rad(270);
            BlazeBurnerBlock.HeatLevel heatLevel = keeperBE.getHeatLevelForRender();
            boolean canDrawFlame = heatLevel.isAtLeast(BlazeBurnerBlock.HeatLevel.FADING);
            boolean drawGoggles = keeperBE.goggles;
            PartialModel drawHat = AllPartialModels.LOGISTICS_HAT;
            int hashCode = keeperBE.hashCode();
            Lighting.setupForEntityInInventory();

            VertexConsumer cutout = graphics.bufferSource().getBuffer(RenderType.cutoutMipped());
            CachedBuffers.partial(AllPartialModels.BLAZE_CAGE, keeperBE.getBlockState())
                    .rotateCentered(horizontalAngle + Mth.PI, Direction.UP)
                    .light(LightTexture.FULL_BRIGHT)
                    .renderInto(ms, cutout);

            ChronendicBlazeBurnerRenderer.renderShared(ms, null, graphics.bufferSource(), minecraft.level,
                    keeperBE.getBlockState(), heatLevel, animation, horizontalAngle, canDrawFlame, drawGoggles, drawHat,
                    hashCode);
            Lighting.setupFor3DItems();
            ms.popPose();
        }
    }
}
