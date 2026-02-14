package io.github.cvrunmin.create_andertech.index;

import com.tterrag.registrate.util.entry.FluidEntry;
import io.github.cvrunmin.create_andertech.CreateAndertech;
import io.github.cvrunmin.create_andertech.content.fluids.ChronenditeSolutionFluid;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.material.MapColor;

import static io.github.cvrunmin.create_andertech.CreateAndertech.REGISTRATE;

public class AllFluids {
    static {
        REGISTRATE.setCreativeTab(AllCreativeTabs.MAIN_TAB);
    }

    public static final FluidEntry<ChronenditeSolutionFluid.Flowing> CHRONENDITE_SOLUTION = CreateAndertech.REGISTRATE.fluid("chronendite_solution",
                    ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_fluid_still"),
                    ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_fluid_flow"),
                    ChronenditeSolutionFluid.Flowing::new)
            .lang("Chronendite Solution")
            .properties(p -> p.canDrown(true).canSwim(true).canPushEntity(true).viscosity(1000).density(1400))
            .source(ChronenditeSolutionFluid.Source::new)
            .block()
            .properties(p -> p.mapColor(MapColor.COLOR_BLACK))
            .build()
            .bucket()
            .onRegister(bucketItem -> {
                DispenserBlock.registerBehavior(bucketItem, new DefaultDispenseItemBehavior(){
                    @Override
                    protected ItemStack execute(BlockSource blockSource, ItemStack item) {
                        DispensibleContainerItem dispensibleContainerItem = (DispensibleContainerItem) item.getItem();
                        BlockPos pos = blockSource.pos().relative(blockSource.state().getValue(DispenserBlock.FACING));
                        Level level = blockSource.level();
                        if (dispensibleContainerItem.emptyContents(null, level, pos, null, item)) {
                            return new ItemStack(Items.BUCKET);
                        }
                        return DEFAULT.dispense(blockSource, item);
                    }
                });
            })
            .build()
            .register();

    private static final DispenseItemBehavior DEFAULT = new DefaultDispenseItemBehavior();

    public static void register() {

    }
}
