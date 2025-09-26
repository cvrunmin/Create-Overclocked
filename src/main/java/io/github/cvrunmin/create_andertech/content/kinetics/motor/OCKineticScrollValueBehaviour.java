package io.github.cvrunmin.create_andertech.content.kinetics.motor;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import io.github.cvrunmin.create_andertech.Constants;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

public class OCKineticScrollValueBehaviour extends ScrollValueBehaviour {
    public OCKineticScrollValueBehaviour(Component label, SmartBlockEntity be, ValueBoxTransform slot) {
        super(label, be, slot);
    }

    @Override
    public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
        ImmutableList<Component> rows = ImmutableList.of(
                Component.literal("\u27f316x").withStyle(ChatFormatting.BOLD),
                Component.literal("\u27f38x").withStyle(ChatFormatting.BOLD),
                Component.literal("\u27f34x").withStyle(ChatFormatting.BOLD),
                Component.literal("\u27f32x").withStyle(ChatFormatting.BOLD),
                Component.literal("\u27f3").withStyle(ChatFormatting.BOLD),
                Component.literal("\u27f2").withStyle(ChatFormatting.BOLD),
                Component.literal("\u27f22x").withStyle(ChatFormatting.BOLD),
                Component.literal("\u27f24x").withStyle(ChatFormatting.BOLD),
                Component.literal("\u27f28x").withStyle(ChatFormatting.BOLD),
                Component.literal("\u27f216x").withStyle(ChatFormatting.BOLD)
                );
        ValueSettingsFormatter formatter = new ValueSettingsFormatter(this::formatSettings);
        return new ValueSettingsBoard(label, 256, 32, rows, formatter);
    }

    private int makeRealValue(int rawRow, int value){
        int tier;
        if(rawRow >= 5){
            tier = rawRow - 5;
        }
        else{
            tier = 4 - rawRow;
        }
        int valueStart, valueMultiplier;
        switch (tier) {
            case 1 -> {
                valueStart = 256;
                valueMultiplier = 1;
            }
            case 2 -> {
                valueStart = 512;
                valueMultiplier = 2;
            }
            case 3 -> {
                valueStart = 1024;
                valueMultiplier = 4;
            }
            case 4 -> {
                valueStart = 2048;
                valueMultiplier = 8;
            }
            default -> {
                valueStart = 0;
                valueMultiplier = 1;
            }
        }
        return valueStart + value * valueMultiplier;
    }

    @Override
    public void setValueSettings(Player player, ValueSettings valueSetting, boolean ctrlDown) {
        int value = Math.max(1, valueSetting.value());
        int rawRow = valueSetting.row();
        boolean clockwise = rawRow < 5;
        if (!valueSetting.equals(getValueSettings()))
            playFeedbackSound(this);
        int realValue = (clockwise ? -1 : 1) * makeRealValue(rawRow, value);
        setValue(realValue);
    }

    @Override
    public ValueSettings getValueSettings() {
        boolean clockwise = value < 0;
        int absValue =  Math.abs(value);
        int row = 5;
        int scrollValue;
        if(absValue <= 256){
            scrollValue = absValue;
        }
        else if(absValue <= 512){
            row = 6;
            scrollValue = absValue - 256;
        }
        else if(absValue <= 1024){
            row = 7;
            scrollValue = (absValue - 512) / 2;
        }
        else if(absValue <= 2048){
            row = 8;
            scrollValue = (absValue - 1024) / 4;
        }
        else if(absValue <= 4096){
            row = 9;
            scrollValue = (absValue - 2048) / 8;
        }
        else{
            scrollValue = absValue;
        }
        if(clockwise){
            row = 9 - row;
        }
        return new ValueSettings(row, scrollValue);
    }

    public MutableComponent formatSettings(ValueSettings settings) {

        return CreateLang.number(Math.max(1, makeRealValue(settings.row(), settings.value())))
                .add(CreateLang.text(settings.row() < 5 ? "\u27f3" : "\u27f2")
                        .style(ChatFormatting.BOLD))
                .component();
    }

    @Override
    public String getClipboardKey() {
        return "SpeedOC";
    }
}
