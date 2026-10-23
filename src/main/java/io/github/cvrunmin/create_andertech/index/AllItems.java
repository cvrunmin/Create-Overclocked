package io.github.cvrunmin.create_andertech.index;

import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.simibubi.create.foundation.data.AssetLookup;
import com.tterrag.registrate.util.entry.ItemEntry;
import io.github.cvrunmin.create_andertech.CreateAndertech;
import io.github.cvrunmin.create_andertech.content.kinetics.gearbox.VerticalCrimschistGearboxItem;
import io.github.cvrunmin.create_andertech.content.misc.EndopyroticCinderPasteItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.common.Tags;

import static io.github.cvrunmin.create_andertech.CreateAndertech.REGISTRATE;

public class AllItems {
    static {
        REGISTRATE.setCreativeTab(AllCreativeTabs.MAIN_TAB);
    }
//    public static final ItemEntry<BeltConnectorItem> BELT_CONNECTOR =
//            REGISTRATE.item("belt_connector", BeltConnectorItem::new)
//                    .lang("Mechanical Belt")
//                    .register();
    public static final ItemEntry<Item> CRIMSCHIST_ALLOY = REGISTRATE.item("crimschist_alloy", Item::new)
        .tag(AllTags.CRIMSCHIST, com.simibubi.create.AllTags.AllItemTags.CREATE_INGOTS.tag)
        .register();

    public static final ItemEntry<Item> CRIMSCHIST_SHEET = REGISTRATE.item("crimschist_sheet", Item::new)
        .tag(AllTags.CRIMSCHIST)
        .register();

    public static final ItemEntry<Item> CRIMSCHIST_NUGGET = REGISTRATE.item("crimschist_nugget", Item::new)
            .tag(AllTags.CRIMSCHIST, Tags.Items.NUGGETS)
            .register();

    public static final ItemEntry<Item> CHRONENDITE_ORB = REGISTRATE.item("chronendite_orb", Item::new)
            .tag(AllTags.CHRONENDITE)
            .model((ctx, prov) -> prov.generated(ctx, ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "item/chronendite_pearl")))
            .register();

    public static final ItemEntry<Item> CHRONENDITE_SHARD = REGISTRATE.item("chronendite_shard", Item::new)
            .tag(AllTags.CHRONENDITE, Tags.Items.NUGGETS)
            .register();

    public static final ItemEntry<Item> FORGED_CHRONENDITE_ORB = REGISTRATE.item("forged_chronendite_orb", Item::new)
            .register();

    public static final ItemEntry<EndopyroticCinderPasteItem> ENDOPYROTIC_CINDER_PASTE = REGISTRATE.item("endopyrotic_cinder_paste", EndopyroticCinderPasteItem::new)
            .register();

    public static final ItemEntry<VerticalCrimschistGearboxItem> VERTICAL_CRIMSCHIST_GEARBOX =
            REGISTRATE.item("vertical_crimschist_gearbox", VerticalCrimschistGearboxItem::new)
                    .model(AssetLookup.customBlockItemModel("crimschist_gearbox", "item_vertical"))
                    .register();

    public static final ItemEntry<SequencedAssemblyItem> INCOMPLETE_BLACKBOX_DIFFERENTIAL = REGISTRATE.item("incomplete_crimschist_blackbox_differential", SequencedAssemblyItem::new)
            .model((c, p) -> p.withExistingParent(c.getName(), p.modLoc("block/crimschist_blackbox_differential/item")))
            .register();

    public static final ItemEntry<Item> MISCONFIGURED_BLACKBOX_DIFFERENTIAL = REGISTRATE.item("misconfigured_crimschist_blackbox_differential", Item::new)
            .model((c, p) -> p.withExistingParent(c.getName(), p.modLoc("block/crimschist_blackbox_differential/item")))
            .register();

    public static void register() {

    }
}
