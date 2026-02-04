package io.github.cvrunmin.create_andertech.content.kinetics.deployer;

import com.mojang.authlib.GameProfile;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.infrastructure.config.CKinetics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.UsernameCache;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.entity.EntityEvent;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.UUID;

@EventBusSubscriber
public class OCDeployerFakePlayer extends FakePlayer {
    public static final UUID fallbackID = UUID.fromString("cd0a4584-144f-490d-af00-a89a0008aa8e");
    public Pair<BlockPos, Float> blockBreakingProgress;
    public ItemStack spawnedItemEffects;
    public boolean placedTracks;
    public boolean onMinecartContraption;
    private UUID owner;

    public OCDeployerFakePlayer(ServerLevel world, @Nullable UUID owner) {
        super(world, new DeployerGameProfile(fallbackID, "Deployer", owner));
        this.owner = owner;
    }

    @Override
    public OptionalInt openMenu(MenuProvider menuProvider) {
        return OptionalInt.empty();
    }

    @Override
    public Component getDisplayName() {
        return CreateLang.translateDirect("block.deployer.damage_source_name");
    }

    @Override
    public void tick() {
        this.getCooldowns().tick();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public EntityDimensions getDefaultDimensions(Pose pose) {
        return super.getDefaultDimensions(pose).withEyeHeight(0);
    }

    @Override
    public Vec3 position() {
        return new Vec3(getX(), getY(), getZ());
    }

    @Override
    public float getCurrentItemAttackStrengthDelay() {
        return 1 / 64f;
    }

    @Override
    public boolean canEat(boolean ignoreHunger) {
        return false;
    }

    @Override
    public ItemStack eat(Level world, ItemStack stack, FoodProperties foodProperties) {
        stack.shrink(1);
        return stack;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance pEffectInstance) {
        return false;
    }

    @Override
    public UUID getUUID() {
        return owner == null ? super.getUUID() : owner;
    }

    @SuppressWarnings("removal")
    @SubscribeEvent
    public static void deployerHasEyesOnHisFeet(EntityEvent.Size event) {
        if (event.getEntity() instanceof OCDeployerFakePlayer)
            event.setNewSize(event.getNewSize().withEyeHeight(0));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void deployerCollectsDropsFromKilledEntities(LivingDropsEvent event) {
        DamageSource source = event.getSource();
        Entity trueSource = source.getEntity();
        if (trueSource != null && trueSource instanceof OCDeployerFakePlayer fakePlayer) {
            event.getDrops()
                    .forEach(stack -> fakePlayer.getInventory()
                            .placeItemBackInInventory(stack.getItem()));
            event.setCanceled(true);
        }
    }

    @Override
    protected boolean doesEmitEquipEvent(EquipmentSlot p_217035_) {
        return false;
    }

    @Override
    public void remove(RemovalReason p_150097_) {
        if (blockBreakingProgress != null && !level().isClientSide)
            level().destroyBlockProgress(getId(), blockBreakingProgress.getKey(), -1);
        super.remove(p_150097_);
    }

    @SubscribeEvent
    public static void dropLessXPForOCDeployer(LivingExperienceDropEvent event) {
        if (event.getAttackingPlayer() instanceof OCDeployerFakePlayer deployer) {
            event.setDroppedExperience(event.getDroppedExperience() / 3);
        }
    }

    @SubscribeEvent
    public static void entitiesDontRetaliate(LivingChangeTargetEvent event) {
        if (!(event.getOriginalAboutToBeSetTarget() instanceof OCDeployerFakePlayer))
            return;
        LivingEntity entityLiving = event.getEntity();
        if (!(entityLiving instanceof Mob mob))
            return;

        CKinetics.DeployerAggroSetting setting = AllConfigs.server().kinetics.ignoreDeployerAttacks.get();

        switch (setting) {
            case ALL -> event.setCanceled(true);
            case CREEPERS -> {
                if (mob instanceof Creeper)
                    event.setCanceled(true);
            }
        }
    }

    // Credit to Mekanism for this approach. Helps fake players get past claims and
    // protection by other mods
    private static class DeployerGameProfile extends GameProfile {

        private UUID owner;

        public DeployerGameProfile(UUID id, String name, UUID owner) {
            super(id, name);
            this.owner = owner;
        }

        @Override
        public UUID getId() {
            return owner == null ? super.getId() : owner;
        }

        @Override
        public String getName() {
            if (owner == null)
                return super.getName();
            String lastKnownUsername = UsernameCache.getLastKnownUsername(owner);
            return lastKnownUsername == null ? super.getName() : lastKnownUsername;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o)
                return true;
            if (!(o instanceof GameProfile otherProfile))
                return false;
            return Objects.equals(getId(), otherProfile.getId()) && Objects.equals(getName(), otherProfile.getName());
        }

        @Override
        public int hashCode() {
            UUID id = getId();
            String name = getName();
            int result = id == null ? 0 : id.hashCode();
            result = 31 * result + (name == null ? 0 : name.hashCode());
            return result;
        }
    }
}
