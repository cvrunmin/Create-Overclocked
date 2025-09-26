package io.github.cvrunmin.create_andertech.content.kinetics.transmission;

import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class BlackboxDifferential extends RotatedPillarKineticBlock implements ICogWheel {
    // idea: a black box that has two inputs and one output.
    // the RPM of the output is mean of those of two inputs
    // output is a large cogwheel
    // it will attempt to add impact to the one with larger RPM
    // if same RPM, follows right hand rule and use the network thumbs pointing to
    // if there is an input on large cogwheel, do the other way round.
    // if no input on small cogwheel, they share the same speed of the large cogwheel.
    // if an input is added on small cogwheel, make another small cogwheel spin such that the formula holds
    // if one side input is closed, another side will be twice as fast
    // breaks blackbox if small cogwheel spin too fast, or all three cogwheel actively spins

    public BlackboxDifferential(Properties properties) {
        super(properties);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return null;
    }
}
