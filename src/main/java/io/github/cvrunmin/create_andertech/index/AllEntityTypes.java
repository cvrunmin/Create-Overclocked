package io.github.cvrunmin.create_andertech.index;

import com.tterrag.registrate.util.entry.EntityEntry;
import io.github.cvrunmin.create_andertech.content.entity.ChronendicBlaze;
import io.github.cvrunmin.create_andertech.content.entity.ChronendicBlazeRenderer;
import io.github.cvrunmin.create_andertech.content.misc.NyliumTransmuterEntity;
import io.github.cvrunmin.create_andertech.content.misc.NyliumTransmuterRenderer;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.storage.loot.LootTable;

import static io.github.cvrunmin.create_andertech.CreateAndertech.REGISTRATE;

public class AllEntityTypes {

    public static EntityEntry<NyliumTransmuterEntity> NYLIUM_TRANSMUTER = REGISTRATE
            .<NyliumTransmuterEntity>entity("nylium_transmuter", NyliumTransmuterEntity::new, MobCategory.MISC)
            .properties(builder -> builder.fireImmune().sized(0.49f, 0.49f).eyeHeight(0.075f).clientTrackingRange(10).updateInterval(10))
            .renderer(() -> NyliumTransmuterRenderer::new)
            .register();

    public static EntityEntry<ChronendicBlaze> CHRONENDIC_BLAZE = REGISTRATE
            .<ChronendicBlaze>entity("chronendic_blaze", ChronendicBlaze::new, MobCategory.MONSTER)
            .properties(builder -> builder.fireImmune().sized(0.6f, 1.8f).clientTrackingRange(8))
            .attributes(ChronendicBlaze::createAttributes)
            .tag(EntityTypeTags.FALL_DAMAGE_IMMUNE, EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES)
            .loot((tables, entityType) -> tables.add(entityType, LootTable.lootTable()))
            .defaultSpawnEgg(0xeb1cbc, 0xbccdff)
            .renderer(() -> ChronendicBlazeRenderer::new)
            .register();

    public static void register(){}
}
