package io.github.cvrunmin.create_andertech.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.jei.category.animations.AnimatedMixer;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import net.minecraft.client.gui.GuiGraphics;

public class SequencedAssemblyStirringSubCategory extends SequencedAssemblySubCategory {
    AnimatedMixer mixer;

    public SequencedAssemblyStirringSubCategory() {
        super(25);
        mixer = new AnimatedMixer();
    }

    @Override
    public void draw(SequencedRecipe<?> recipe, GuiGraphics graphics, double mouseX, double mouseY, int index) {
        PoseStack ms = graphics.pose();
        mixer.offset = index;
        ms.pushPose();
        ms.translate(-5, 50, 0);
        ms.scale(.6f, .6f, .6f);
        mixer.draw(graphics, getWidth() / 2, 0);
        ms.popPose();
    }
}
