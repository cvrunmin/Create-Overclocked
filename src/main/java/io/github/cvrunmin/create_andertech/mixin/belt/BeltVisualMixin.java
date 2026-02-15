package io.github.cvrunmin.create_andertech.mixin.belt;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.kinetics.belt.BeltVisual;
import dev.engine_room.flywheel.api.model.Model;
import io.github.cvrunmin.create_andertech.content.kinetics.belt.BeltVisualCustomPulley;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(BeltVisual.class)
public class BeltVisualMixin implements BeltVisualCustomPulley {

    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/kinetics/belt/BeltVisual;getPulleyModel()Ldev/engine_room/flywheel/api/model/Model;"))
    private Model createandertech$redirectToWarpper(BeltVisual self, Operation<Model> original){
        var maybeModel = getCustomPulleyModel();
        return maybeModel.orElseGet(() -> original.call(self));
    }

    @Override
    public Optional<Model> getCustomPulleyModel() {
        return Optional.empty();
    }
}
