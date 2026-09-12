package io.github.cvrunmin.create_andertech.content.fluids.pump;

import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

public class CrimschistPumpRenderer extends KineticBlockEntityRenderer<CrimschistPumpBlockEntity> {
    public CrimschistPumpRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected BlockState getRenderedBlockState(CrimschistPumpBlockEntity be) {
        return shaft(getRotationAxisOf(be));
    }
}
