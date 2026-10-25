package io.github.cvrunmin.create_andertech.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import io.github.cvrunmin.create_andertech.foundation.MyLang;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.ChatFormatting;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(IRotate.SpeedLevel.class)
public class SpeedLevelMixin {
    @WrapMethod(method = "getFormattedSpeedText")
    private static LangBuilder getFormattedSpeedTextForOverspeed(float speed, boolean overstressed, Operation<LangBuilder> original){
        if(Math.abs(speed) <= 256){
            return original.call(speed, overstressed);
        }
        LangBuilder builder = CreateLang.text(TooltipHelper.makeProgressBar(4, 4));
        builder.add(MyLang.builder().translate("tooltip.speedRequirement.overspeed"))
                .space()
                .text("(")
                .add(CreateLang.number(Math.abs(speed)))
                .space()
                .translate("generic.unit.rpm")
                .text(")")
                .space();
        if (overstressed)
            builder.style(ChatFormatting.DARK_GRAY)
                    .style(ChatFormatting.STRIKETHROUGH);
        else
            builder.style(ChatFormatting.DARK_PURPLE);
        return builder;
    }
}
