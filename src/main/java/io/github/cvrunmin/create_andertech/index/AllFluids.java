package io.github.cvrunmin.create_andertech.index;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tterrag.registrate.builders.FluidBuilder;
import com.tterrag.registrate.util.entry.FluidEntry;
import io.github.cvrunmin.create_andertech.CreateAndertech;
import io.github.cvrunmin.create_andertech.content.fluids.ChronenditeSolutionFluid;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.FogRenderer;
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
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidType;
import org.joml.Vector3f;

import java.util.function.Consumer;

import static io.github.cvrunmin.create_andertech.CreateAndertech.REGISTRATE;

public class AllFluids {
    static {
        REGISTRATE.setCreativeTab(AllCreativeTabs.MAIN_TAB);
    }

    public static final FluidEntry<ChronenditeSolutionFluid.Flowing> CHRONENDITE_SOLUTION = REGISTRATE.fluid("chronendite_solution",
                    ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_fluid_still"),
                    ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/chronendite_fluid_flow"),
                    (properties, stillTexture, flowingTexture) -> new FluidType(properties) {
                        @Override
                        public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                            consumer.accept(new IClientFluidTypeExtensions() {
                                @Override
                                public ResourceLocation getStillTexture() {
                                    return stillTexture;
                                }

                                @Override
                                public ResourceLocation getFlowingTexture() {
                                    return flowingTexture;
                                }

                                @Override
                                public Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
                                    return new Color(0x170021, false).asVectorF();
                                }

                                @Override
                                public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape) {
                                    RenderSystem.setShaderFogStart(0.25f);
                                    RenderSystem.setShaderFogEnd(16f);
                                    if(camera.getEntity() instanceof LocalPlayer localPlayer){
                                        RenderSystem.setShaderFogEnd(16f * Math.max(0.25F, localPlayer.getWaterVision()));
                                    }
                                }
                            });
                        }
                    },
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
