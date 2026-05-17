package io.github.cvrunmin.create_andertech.index;

import io.github.cvrunmin.create_andertech.CreateAndertech;
import io.github.cvrunmin.create_andertech.content.entity.ChronendicBlazeModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = CreateAndertech.MODID)
public class AllModelLayers {
    public static final ModelLayerLocation CHRONENDIC_BLAZE = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "chronendic_blaze"), "main");

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event){
        event.registerLayerDefinition(CHRONENDIC_BLAZE, ChronendicBlazeModel::createBodyLayer);
    }
}
