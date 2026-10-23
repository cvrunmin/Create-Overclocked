package io.github.cvrunmin.create_andertech.infrastructure.data;

import io.github.cvrunmin.create_andertech.CreateAndertech;
import io.github.cvrunmin.create_andertech.index.CAAllSoundEvents;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

public class CASoundDefProvider extends SoundDefinitionsProvider {

    protected CASoundDefProvider(PackOutput output, ExistingFileHelper helper) {
        super(output, CreateAndertech.MODID, helper);
    }

    @Override
    public void registerSounds() {
        add(CAAllSoundEvents.CHRONENDIC_BLAZE_AMBIENT.get(), SoundDefinition.definition()
                .subtitle(CreateAndertech.MODID + ".subtitles.entity.chronendic_blaze.ambient")
                .with(sound(ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "mob/chronendic_blaze/breathe1")))
                .with(sound(ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "mob/chronendic_blaze/breathe2")))
                .with(sound(ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "mob/chronendic_blaze/breathe3")))
        );
        add(CAAllSoundEvents.CHRONENDIC_BLAZE_HURT.get(), SoundDefinition.definition()
                .subtitle(CreateAndertech.MODID + ".subtitles.entity.chronendic_blaze.hurt")
                .with(sound(ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "mob/chronendic_blaze/hit1")))
                .with(sound(ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "mob/chronendic_blaze/hit2")))
                .with(sound(ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "mob/chronendic_blaze/hit3")))
        );
        add(CAAllSoundEvents.CHRONENDIC_BLAZE_DEATH.get(), SoundDefinition.definition()
                .subtitle(CreateAndertech.MODID + ".subtitles.entity.chronendic_blaze.death")
                .with(sound(ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "mob/chronendic_blaze/death")))
        );
    }
}
