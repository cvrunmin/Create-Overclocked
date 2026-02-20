package io.github.cvrunmin.create_andertech;

import io.github.cvrunmin.create_andertech.content.fluids.infusion_vat.InfusionVatBlockEntity;
import io.github.cvrunmin.create_andertech.content.fluids.tank.CrimschistFluidTankBlockEntity;
import io.github.cvrunmin.create_andertech.content.kinetics.belt.OCBeltBlockEntity;
import io.github.cvrunmin.create_andertech.content.kinetics.crafter.ChronenditeMechanicalCrafterBlockEntity;
import io.github.cvrunmin.create_andertech.content.kinetics.crusher.ChronenditeCrushWheelControllerBlockEntity;
import io.github.cvrunmin.create_andertech.content.kinetics.deployer.ChronenditeDeployerBlockEntity;
import io.github.cvrunmin.create_andertech.content.kinetics.motor.CreativeOCMotorBlockEntity;
import io.github.cvrunmin.create_andertech.content.kinetics.saw.ChronenditeSawBlockEntity;
import io.github.cvrunmin.create_andertech.content.kinetics.speedController.ChronenditeSpeedControllerBlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber
public class CommonEvents {
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event){
        OCBeltBlockEntity.registerCapabilities(event);
        ChronenditeMechanicalCrafterBlockEntity.registerCapabilities(event);
        ChronenditeDeployerBlockEntity.registerCapabilities(event);
        ChronenditeCrushWheelControllerBlockEntity.registerCapabilities(event);
        ChronenditeSawBlockEntity.registerCapabilities(event);
        ChronenditeSpeedControllerBlockEntity.registerCapabilities(event);
        CreativeOCMotorBlockEntity.registerCapabilities(event);
        CrimschistFluidTankBlockEntity.registerCapabilities(event);
        InfusionVatBlockEntity.registerCapabilities(event);
    }
}
