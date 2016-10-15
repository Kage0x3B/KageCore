package de.syscy.kagecore.util;

import java.util.List;

import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftVillager;
import org.bukkit.craftbukkit.v1_10_R1.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftMerchantRecipe;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.MerchantRecipe;

import io.netty.buffer.Unpooled;
import net.minecraft.server.v1_10_R1.Container;
import net.minecraft.server.v1_10_R1.ContainerMerchant;
import net.minecraft.server.v1_10_R1.EntityHuman;
import net.minecraft.server.v1_10_R1.EntityPlayer;
import net.minecraft.server.v1_10_R1.IChatBaseComponent;
import net.minecraft.server.v1_10_R1.IMerchant;
import net.minecraft.server.v1_10_R1.InventoryMerchant;
import net.minecraft.server.v1_10_R1.MerchantRecipeList;
import net.minecraft.server.v1_10_R1.PacketDataSerializer;
import net.minecraft.server.v1_10_R1.PacketPlayOutCustomPayload;
import net.minecraft.server.v1_10_R1.PacketPlayOutOpenWindow;

public class VillagerTradingUtil {
	public static void openTrade(Player player, Villager villager) {
		openTrade(player, ((CraftVillager) villager).getHandle().getScoreboardDisplayName(), villager.getRecipes());
	}

	public static void openTrade(Player player, IChatBaseComponent title, List<MerchantRecipe> merchantRecipes) {
		final EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();

		final CustomMerchant customMerchant = new CustomMerchant(title, merchantRecipes);
		customMerchant.setTradingPlayer(nmsPlayer);

		final Container container = CraftEventFactory.callInventoryOpenEvent(nmsPlayer, new ContainerMerchant(nmsPlayer.inventory, customMerchant, nmsPlayer.world));

		if(container == null) {
			return;
		}

		int containerCounter = nmsPlayer.nextContainerCounter();

		nmsPlayer.activeContainer = container;
		nmsPlayer.activeContainer.windowId = containerCounter;
		nmsPlayer.activeContainer.addSlotListener(nmsPlayer);
		InventoryMerchant inventorymerchant = ((ContainerMerchant) nmsPlayer.activeContainer).e();
		IChatBaseComponent ichatbasecomponent = customMerchant.getScoreboardDisplayName();
		nmsPlayer.playerConnection.sendPacket(new PacketPlayOutOpenWindow(containerCounter, "minecraft:villager", ichatbasecomponent, inventorymerchant.getSize()));
		MerchantRecipeList merchantrecipelist = customMerchant.getOffers(nmsPlayer);

		if(merchantrecipelist != null) {
			PacketDataSerializer packetdataserializer = new PacketDataSerializer(Unpooled.buffer());
			packetdataserializer.writeInt(containerCounter);
			merchantrecipelist.a(packetdataserializer);
			nmsPlayer.playerConnection.sendPacket(new PacketPlayOutCustomPayload("MC|TrList", packetdataserializer));
		}
	}

	public static class CustomMerchant implements IMerchant {
		private final IChatBaseComponent title;
		private final List<MerchantRecipe> merchantRecipes;
		private EntityHuman tradingWith = null;

		public CustomMerchant(IChatBaseComponent title, List<MerchantRecipe> merchantRecipes) {
			this.title = title;
			this.merchantRecipes = merchantRecipes;
		}

		@Override
		public void a(final net.minecraft.server.v1_10_R1.MerchantRecipe merchantRecipe) {

		}

		@Override
		public MerchantRecipeList getOffers(EntityHuman player) {
			final MerchantRecipeList merchantRecipeList = new MerchantRecipeList();

			for(org.bukkit.inventory.MerchantRecipe merchantRecipe : merchantRecipes) {
				merchantRecipeList.add(CraftMerchantRecipe.fromBukkit(merchantRecipe).toMinecraft());
			}

			return merchantRecipeList;
		}

		@Override
		public void a(final net.minecraft.server.v1_10_R1.ItemStack itemStack) {

		}

		@Override
		public IChatBaseComponent getScoreboardDisplayName() {
			return title;
		}

		@Override
		public EntityHuman getTrader() {
			return tradingWith;
		}

		@Override
		public void setTradingPlayer(final EntityHuman player) {
			tradingWith = player;
		}
	}
}