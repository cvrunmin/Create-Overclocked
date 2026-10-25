package io.github.cvrunmin.create_andertech.content.kinetics.transmission;

import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import io.github.cvrunmin.create_andertech.Constants;
import io.github.cvrunmin.create_andertech.CreateAndertech;
import io.github.cvrunmin.create_andertech.content.kinetics.transmission.CrimschistBlackboxDifferentialBlock.Mode;
import io.github.cvrunmin.create_andertech.content.kinetics.transmission.CrimschistBlackboxDifferentialBlock.Port;
import io.github.cvrunmin.create_andertech.foundation.IOverspeedKineticBE;
import io.github.cvrunmin.create_andertech.foundation.MyLang;
import io.github.cvrunmin.create_andertech.mixin.RotationPropagatorInvoker;
import net.createmod.catnip.lang.FontHelper;
import net.createmod.catnip.lang.Lang;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CrimschistBlackboxDifferentialBlockEntity extends SplitShaftBlockEntity implements IOverspeedKineticBE {

    public float lastCalculatedSpeed;

    public CrimschistBlackboxDifferentialBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public float getRotationSpeedModifier(Direction face) {
        if(getBlockState().getValue(CrimschistBlackboxDifferentialBlock.AXIS).test(face) &&
                getBlockState().getValue(face.getAxisDirection() == Direction.AxisDirection.NEGATIVE ? CrimschistBlackboxDifferentialBlock.NEGATIVE_MODE : CrimschistBlackboxDifferentialBlock.POSITIVE_MODE) == Mode.NONE){
            return 0;
        }
        return 1;
    }

    // Ports

    /** The kinetic block entity connected to the given port, or null when nothing is connected there. */
    @Nullable
    public KineticBlockEntity portNeighbour(Port port) {
        if (level == null)
            return null;
        if (port == Port.COG)
            return dominantRingNeighbour();
        Direction.Axis axis = getBlockState().getValue(RotatedPillarKineticBlock.AXIS);
        return connectedNeighbour(worldPosition.relative(CrimschistBlackboxDifferentialBlock.directionOf(axis, port == Port.POS)));
    }

    /** Whether the given port is both connected and currently turning, i.e. qualifies as a driven input. */
    public boolean isDriven(Port port) {
        KineticBlockEntity neighbour = portNeighbour(port);
        return neighbour != null && neighbour.getTheoreticalSpeed() != 0;
    }

    /**
     * What the given port's neighbour conveys into this block. Never called for the OUT port, whose value is
     * {@link #solve()} instead.
     */
    public float portSpeed(Port port) {
        boolean isOutput;
        if(port == Port.COG){
            isOutput = CrimschistBlackboxDifferentialBlock.cogMode(getBlockState()) == Mode.OUT;
        }else if(port == Port.NEG){
            isOutput = CrimschistBlackboxDifferentialBlock.negativeMode(getBlockState()) == Mode.OUT;
        }else{
            isOutput = CrimschistBlackboxDifferentialBlock.positiveMode(getBlockState()) == Mode.OUT;
        }
        if(isOutput){
            return getSpeed();
        }
        KineticBlockEntity neighbour = portNeighbour(port);
        if (neighbour == null)
            return 0;
        return neighbour.getTheoreticalSpeed()
                * RotationPropagatorInvoker.invokeGetRotationSpeedModifier(neighbour, this);
    }

    @Nullable
    private KineticBlockEntity connectedNeighbour(BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof KineticBlockEntity neighbour))
            return null;
        return RotationPropagator.isConnected(this, neighbour) ? neighbour : null;
    }

    /**
     * The ring can mesh with up to a dozen neighbours at once; the port value comes from the dominant one - the
     * fastest turning connected gear, or any connected one when none of them turns.
     */
    @Nullable
    private KineticBlockEntity dominantRingNeighbour() {
        KineticBlockEntity dominant = null;
        for (int x = -1; x <= 1; x++)
            for (int y = -1; y <= 1; y++)
                for (int z = -1; z <= 1; z++) {
                    if (Math.abs(x) + Math.abs(y) + Math.abs(z) != 2)
                        continue;
                    KineticBlockEntity candidate = connectedNeighbour(worldPosition.offset(x, y, z));
                    if (candidate == null)
                        continue;
                    if (dominant == null) {
                        dominant = candidate;
                        continue;
                    }
                    float candidateSpeed = Math.abs(candidate.getTheoreticalSpeed());
                    float dominantSpeed = Math.abs(dominant.getTheoreticalSpeed());
                    if (candidateSpeed != 0 && (dominantSpeed == 0 || candidateSpeed > dominantSpeed))
                        dominant = candidate;
                }
        return dominant;
    }

    private float dominantSpeed;

    @Override
    public void setSpeed(float speed) {
        // no-op
    }

    public float getDominantSpeed() {
        return dominantSpeed;
    }

    public void setDominantSpeed(float dominantSpeed) {
        this.dominantSpeed = dominantSpeed;
    }

    @Override
    public float getTheoreticalSpeed() {
        if(!hasSource()) return 0;
        return solve();
    }

    // Solving

    /** The value of the OUT side, or 0 when the mode combination cannot be solved. */
    public float solve() {
        BlockState state = getBlockState();
        Mode negative = CrimschistBlackboxDifferentialBlock.negativeMode(state);
        Mode positive = CrimschistBlackboxDifferentialBlock.positiveMode(state);
        Mode cog = CrimschistBlackboxDifferentialBlock.cogMode(state);

        int outs = (negative == Mode.OUT ? 1 : 0) + (positive == Mode.OUT ? 1 : 0) + (cog == Mode.OUT ? 1 : 0);
        int ins = (negative == Mode.IN ? 1 : 0) + (positive == Mode.IN ? 1 : 0) + (cog == Mode.IN ? 1 : 0);
        if (outs != 1 || ins == 0)
            return 0;

        float wNegative = negative == Mode.IN ? portSpeed(Port.NEG) : 0;
        float wPositive = positive == Mode.IN ? portSpeed(Port.POS) : 0;
        if (cog == Mode.OUT)
            return (wNegative + wPositive) / 2f;

        float wCog = portSpeed(Port.COG);
        return negative == Mode.OUT ? 2f * wCog - wPositive : 2f * wCog - wNegative;
    }

    private int flickerTally = 0;

    @Override
    public void onSpeedChanged(float previousSpeed) {
        super.onSpeedChanged(previousSpeed);
        flickerTally = getFlickerScore() + 5;
    }

    @Override
    public int getFlickerScore() {
        return flickerTally;
    }

    @Override
    public void tick() {
        super.tick();
        if(getFlickerScore() > 0) flickerTally = getFlickerScore() - 1;
    }

    // Rotation spread

    @Override
    public List<BlockPos> addPropagationLocations(IRotate block, BlockState state, List<BlockPos> neighbours) {
        BlockPos.betweenClosedStream(new BlockPos(-1, -1, -1), new BlockPos(1, 1, 1))
                .forEach(offset -> {
                    if (offset.distSqr(BlockPos.ZERO) == 2)
                        neighbours.add(worldPosition.offset(offset));
                });
        return neighbours;
    }

    // Tooltips


    @Override
    public boolean addToTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        var hasTooltips = super.addToTooltip(tooltip, isPlayerSneaking);

        BlockState state = getBlockState();
        int outs = 0;
        int ins = 0;
        for (Port port : Port.values()) {
            Mode mode = CrimschistBlackboxDifferentialBlock.modeOf(state, port);
            if (mode == Mode.OUT)
                outs++;
            if (mode == Mode.IN)
                ins++;
        }

        if (outs != 1 || ins == 0) {
            hasTooltips = true;
            MyLang.builder().translate("tooltip.differential.misconfigured")
                    .style(ChatFormatting.GOLD)
                    .forGoggles(tooltip);
            MutableComponent mutated = MyLang.builder().translate("tooltip.differential.misconfigured.hint", I18n.get(getBlockState().getBlock().getDescriptionId())).component();
            List<Component> cutString = TooltipHelper.cutTextComponent(mutated, FontHelper.Palette.GRAY_AND_WHITE);
            for (Component component : cutString) {
                MyLang.builder().add(component.copy()).forGoggles(tooltip);
            }
        }

        return hasTooltips;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        MyLang.builder().translate("differential.header").forGoggles(tooltip);
        addPortReadout(tooltip);
        return true;
    }

    private void addPortReadout(List<Component> tooltip) {
        BlockState state = getBlockState();
        if (!(state.getBlock() instanceof CrimschistBlackboxDifferentialBlock))
            return;

        for (Port port : Port.values())
            addPortLine(tooltip, state, port);

    }

    private void addPortLine(List<Component> tooltip, BlockState state, Port port) {
        Mode mode = CrimschistBlackboxDifferentialBlock.modeOf(state, port);
        LangBuilder line = MyLang.builder()
                .translate("differential.port." + Lang.asId(port.name()))
                .style(ChatFormatting.GRAY)
                .text(": ")
                .add(MyLang.builder()
                        .translate("differential.mode." + mode.getSerializedName())
                        .style(modeColor(mode)));

        if (mode != Mode.NONE) {
            float speed = mode == Mode.OUT ? getTheoreticalSpeed() : portSpeed(port);
            line.text(" (")
                    .add(CreateLang.number(speed))
                    .add(CreateLang.translate("generic.unit.rpm"))
                    .text(")");
        }

        line.forGoggles(tooltip);
    }

    private static ChatFormatting modeColor(Mode mode) {
        return switch (mode) {
            case IN -> ChatFormatting.GREEN;
            case OUT -> ChatFormatting.GOLD;
            case NONE -> ChatFormatting.DARK_GRAY;
        };
    }
}
