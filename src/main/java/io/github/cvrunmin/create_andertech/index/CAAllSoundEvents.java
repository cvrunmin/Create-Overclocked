package io.github.cvrunmin.create_andertech.index;

import io.github.cvrunmin.create_andertech.CreateAndertech;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CAAllSoundEvents {
    private static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, CreateAndertech.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> CHRONENDIC_BLAZE_AMBIENT = SOUND_EVENTS.register("entity.chronendic_blaze.ambient",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "entity.chronendic_blaze.ambient")));

    public static final DeferredHolder<SoundEvent, SoundEvent> CHRONENDIC_BLAZE_HURT = SOUND_EVENTS.register("entity.chronendic_blaze.hurt",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "entity.chronendic_blaze.hurt")));

    public static final DeferredHolder<SoundEvent, SoundEvent> CHRONENDIC_BLAZE_DEATH = SOUND_EVENTS.register("entity.chronendic_blaze.death",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "entity.chronendic_blaze.death")));

    public static void register(IEventBus modBus){
        SOUND_EVENTS.register(modBus);
    }
}
