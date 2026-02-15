package io.github.cvrunmin.create_andertech.content.fluids;

import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

public abstract class ChronenditeSolutionFluid extends BaseFlowingFluid {

    protected ChronenditeSolutionFluid(Properties properties) {
        super(properties);
    }

    @Override
    public float getOwnHeight(FluidState state) {
        return (float)state.getAmount() / 7.0F;
    }

    public static class Source extends ChronenditeSolutionFluid{

        public Source(Properties properties) {
            super(properties);
        }

        @Override
        public boolean isSource(FluidState fluidState) {
            return true;
        }

        @Override
        public int getAmount(FluidState fluidState) {
            return 6;
        }
    }

    public static class Flowing extends ChronenditeSolutionFluid{

        public Flowing(Properties properties) {
            super(properties);
            this.registerDefaultState(this.getStateDefinition().any().setValue(LEVEL, 5));
        }

        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override
        public boolean isSource(FluidState fluidState) {
            return false;
        }

        @Override
        public int getAmount(FluidState fluidState) {
            return (Integer)fluidState.getValue(LEVEL);
        }
    }
}
