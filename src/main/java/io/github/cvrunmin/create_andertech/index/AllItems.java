package io.github.cvrunmin.create_andertech.index;

import com.simibubi.create.foundation.data.AssetLookup;
import com.tterrag.registrate.util.entry.ItemEntry;
import io.github.cvrunmin.create_andertech.content.kinetics.gearbox.VerticalCrimschistGearboxItem;

import static io.github.cvrunmin.create_andertech.CreateAndertech.REGISTRATE;

public class AllItems {
    static {
        REGISTRATE.setCreativeTab(AllCreativeTabs.MAIN_TAB);
    }
//    public static final ItemEntry<BeltConnectorItem> BELT_CONNECTOR =
//            REGISTRATE.item("belt_connector", BeltConnectorItem::new)
//                    .lang("Mechanical Belt")
//                    .register();

    public static final ItemEntry<VerticalCrimschistGearboxItem> VERTICAL_CRIMSCHIST_GEARBOX =
            REGISTRATE.item("vertical_crimschist_gearbox", VerticalCrimschistGearboxItem::new)
                    .model(AssetLookup.customBlockItemModel("crimschist_gearbox", "item_vertical"))
                    .register();

    public static void register() {

    }
}
