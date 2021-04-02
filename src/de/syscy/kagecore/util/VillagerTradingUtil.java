package de.syscy.kagecore.util;

import de.syscy.kagecore.translation.PacketTranslator;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftVillager;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftMerchant;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftMerchantRecipe;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MerchantRecipe;

import javax.annotation.Nullable;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;

@UtilityClass
public class VillagerTradingUtil {
    public static InventoryView openTrade(Player player, Villager villager) {
        return openTrade(player, ((CraftVillager) villager).getHandle().getScoreboardDisplayName(), villager.getRecipes(), villager.getType() != EntityType.WANDERING_TRADER);
    }

    public static InventoryView openTrade(Player player, IChatBaseComponent title, List<MerchantRecipe> merchantRecipes) {
        return openTrade(player, title, merchantRecipes, false);
    }

    public static InventoryView openTrade(Player player, IChatBaseComponent title, List<MerchantRecipe> merchantRecipes, boolean regularVillagerInterface) {
        final EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();

        final CustomMerchant customMerchant = new CustomMerchant(title, merchantRecipes, nmsPlayer.getWorld());
        customMerchant.setTradingPlayer(nmsPlayer);

        OptionalInt optionalContainerId = nmsPlayer.openContainer(new TileInventory((cId, inv, p) -> new ContainerMerchant(cId, inv, customMerchant), title));

        if (optionalContainerId.isPresent()) {
            MerchantRecipeList recipeList = customMerchant.getOffers();

            if (!recipeList.isEmpty()) {
                Random r = new Random(title.getText().hashCode());
                int villagerLevel = r.nextInt(5) + 1;

                MerchantRecipeList translatedRecipeList = new MerchantRecipeList();

                for (net.minecraft.server.v1_16_R3.MerchantRecipe merchantRecipe : recipeList) {
                    translatedRecipeList.add(PacketTranslator.translateMerchantRecipe(merchantRecipe, player));
                }

                nmsPlayer.openTrade(optionalContainerId.getAsInt(), translatedRecipeList, villagerLevel, customMerchant.getExperience(), regularVillagerInterface, false);

                return player.getOpenInventory();
            }
        }

        return null;
    }

    public static class CustomMerchant implements IMerchant {
        private final IChatBaseComponent title;
        private final List<MerchantRecipe> merchantRecipes;
        private final @Getter
        World world;
        private EntityHuman tradingWith = null;

        public CustomMerchant(IChatBaseComponent title, List<MerchantRecipe> merchantRecipes, World world) {
            this.title = title;
            this.merchantRecipes = merchantRecipes;
            this.world = world;
        }

        @Override
        public void setTradingPlayer(@Nullable EntityHuman entityHuman) {
            tradingWith = entityHuman;
        }

        @Nullable
        @Override
        public EntityHuman getTrader() {
            return tradingWith;
        }

        @Override
        public MerchantRecipeList getOffers() {
            final MerchantRecipeList merchantRecipeList = new MerchantRecipeList();

            for (org.bukkit.inventory.MerchantRecipe merchantRecipe : merchantRecipes) {
                merchantRecipeList.add(CraftMerchantRecipe.fromBukkit(merchantRecipe).toMinecraft());
            }

            return merchantRecipeList;
        }

        @Override
        public void a(net.minecraft.server.v1_16_R3.MerchantRecipe merchantRecipe) {

        }

        @Override
        public void k(ItemStack itemStack) {

        }

        @Override
        public int getExperience() {
            return 0;
        }

        @Override
        public void setForcedExperience(int i) {

        }

        @Override
        public boolean isRegularVillager() {
            return false;
        }

        @Override
        public SoundEffect getTradeSound() {
            return null;
        }

        @Override
        public CraftMerchant getCraftMerchant() {
            return null;
        }
    }
}