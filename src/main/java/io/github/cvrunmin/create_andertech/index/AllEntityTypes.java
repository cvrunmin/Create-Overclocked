package io.github.cvrunmin.create_andertech.index;

import com.tterrag.registrate.util.entry.EntityEntry;
import io.github.cvrunmin.create_andertech.content.misc.NyliumTransmuterEntity;
import io.github.cvrunmin.create_andertech.content.misc.NyliumTransmuterRenderer;
import net.minecraft.world.entity.MobCategory;

import static io.github.cvrunmin.create_andertech.CreateAndertech.REGISTRATE;

public class AllEntityTypes {

    public static EntityEntry<NyliumTransmuterEntity> NYLIUM_TRANSMUTER = REGISTRATE
            .<NyliumTransmuterEntity>entity("nylium_transmuter", NyliumTransmuterEntity::new, MobCategory.MISC)
            .properties(builder -> builder.fireImmune().sized(0.49f, 0.49f).eyeHeight(0.075f).clientTrackingRange(10).updateInterval(10))
            .renderer(() -> NyliumTransmuterRenderer::new)
            .register();

    public static void register(){}
}
