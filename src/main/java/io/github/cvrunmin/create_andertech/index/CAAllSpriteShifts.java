package io.github.cvrunmin.create_andertech.index;

import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTSpriteShifter;
import com.simibubi.create.foundation.block.connected.CTType;
import io.github.cvrunmin.create_andertech.CreateAndertech;
import net.minecraft.resources.ResourceLocation;

public class CAAllSpriteShifts {
    public static final CTSpriteShiftEntry CHRONENDITE_CRAFTER_SIDE = getCT(AllCTTypes.VERTICAL, "chronendite_crafter_side"),
            CHRONENDITE_CRAFTER_OTHERSIDE = getCT(AllCTTypes.HORIZONTAL, "chronendite_crafter_side");
    public static final CTSpriteShiftEntry CRIMSCHIST_CASING = getCT(AllCTTypes.OMNIDIRECTIONAL, "crimschist_casing");
    public static final CTSpriteShiftEntry CHRONENDITE_CASING = getCT(AllCTTypes.OMNIDIRECTIONAL, "chronendite_casing");

    private static CTSpriteShiftEntry getCT(CTType type, String blockTextureName) {
        return getCT(type, blockTextureName, blockTextureName);
    }

    private static CTSpriteShiftEntry getCT(CTType type, String blockTextureName, String connectedTextureName) {
        return CTSpriteShifter.getCT(type, ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/" + blockTextureName),
                ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/" + connectedTextureName + "_connected"));
    }
}
