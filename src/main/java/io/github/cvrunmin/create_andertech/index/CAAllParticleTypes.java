package io.github.cvrunmin.create_andertech.index;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.kinetics.fan.AirFlowParticleData;
import io.github.cvrunmin.create_andertech.CreateAndertech;
import io.github.cvrunmin.create_andertech.content.kinetics.fan.AdvancedAirFlowParticle;
import io.github.cvrunmin.create_andertech.content.kinetics.fan.AdvancedAirFlowParticleData;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CAAllParticleTypes {
    private static final DeferredRegister<ParticleType<?>> PARTICLE_TYPE = DeferredRegister.create(Registries.PARTICLE_TYPE, CreateAndertech.MODID);

    public static final DeferredHolder<ParticleType<?>, ParticleType<AdvancedAirFlowParticleData>> ADVANCED_AIR_FLOW = PARTICLE_TYPE.register("advanced_air_flow", () -> new ParticleType<>(true) {
        @Override
        public MapCodec<AdvancedAirFlowParticleData> codec() {
            return AdvancedAirFlowParticleData.CODEC;
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, AdvancedAirFlowParticleData> streamCodec() {
            return AdvancedAirFlowParticleData.STREAM_CODEC;
        }
    });

    public static void register(IEventBus modBus){
        PARTICLE_TYPE.register(modBus);
        modBus.addListener(CAAllParticleTypes::registerParticleProviders);
    }

    private static void registerParticleProviders(RegisterParticleProvidersEvent event){
        event.registerSpriteSet(ADVANCED_AIR_FLOW.get(), AdvancedAirFlowParticle.Factory::new);
    }
}
