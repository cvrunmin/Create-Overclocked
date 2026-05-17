package io.github.cvrunmin.create_andertech.content.entity;

import io.github.cvrunmin.create_andertech.CreateAndertech;
import io.github.cvrunmin.create_andertech.index.AllModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class ChronendicBlazeRenderer extends MobRenderer<ChronendicBlaze, ChronendicBlazeModel<ChronendicBlaze>> {
    private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "textures/entity/chronendic_blaze.png");

    public ChronendicBlazeRenderer(EntityRendererProvider.Context context) {
        super(context, new ChronendicBlazeModel<>(context.bakeLayer(AllModelLayers.CHRONENDIC_BLAZE)), 0.5f);
    }

    protected int getBlockLightLevel(ChronendicBlaze entity, BlockPos pos) {
        return 7;
    }


    @Override
    public ResourceLocation getTextureLocation(ChronendicBlaze entity) {
        return TEXTURE_LOCATION;
    }
}
