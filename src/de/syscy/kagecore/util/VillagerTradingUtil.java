package de.syscy.kagecore.util;

import lombok.experimental.UtilityClass;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftVillager;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftMerchant;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftMerchantRecipe;
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
		return openTrade(player, ((CraftVillager) villager).getHandle().getScoreboardDisplayName(), villager.getRecipes());
	}

	public static InventoryView openTrade(Player player, IChatBaseComponent title, List<MerchantRecipe> merchantRecipes) {
		final EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();

		final CustomMerchant customMerchant = new CustomMerchant(title, merchantRecipes);
		customMerchant.setTradingPlayer(nmsPlayer);

		OptionalInt optionalint = nmsPlayer.openContainer(new TileInventory((cId, inv, p) -> new ContainerMerchant(cId, inv, customMerchant), title));

		if (optionalint.isPresent()) {
			MerchantRecipeList merchantrecipelist = customMerchant.getOffers();

			if (!merchantrecipelist.isEmpty()) {
				//int villagerLevel = 5;
				Random r = new Random(title.getText().hashCode());
				int villagerLevel = r.nextInt(5) + 1;
				nmsPlayer.openTrade(optionalint.getAsInt(), merchantrecipelist, villagerLevel, customMerchant.getExperience(), customMerchant.ea());

				return player.getOpenInventory();
			}
		}

		/*final Container container = CraftEventFactory.callInventoryOpenEvent(nmsPlayer, new ContainerMerchant(containerCounter, nmsPlayer.inventory, customMerchant));

		if(container == null) {
			return null;
		}

		nmsPlayer.activeContainer = container;
		//nmsPlayer.activeContainer.windowId = containerCounter;
		nmsPlayer.activeContainer.addSlotListener(nmsPlayer);
		InventoryMerchant inventorymerchant = ((ContainerMerchant) nmsPlayer.activeContainer).getType();
		IChatBaseComponent ichatbasecomponent = customMerchant.getScoreboardDisplayName();
		nmsPlayer.playerConnection.sendPacket(new PacketPlayOutOpenWindow(containerCounter, "minecraft:villager", ichatbasecomponent, inventorymerchant.getSize()));
		MerchantRecipeList merchantrecipelist = customMerchant.getOffers(nmsPlayer);

		if(merchantrecipelist != null) {
			PacketDataSerializer packetdataserializer = new PacketDataSerializer(Unpooled.buffer());
			packetdataserializer.writeInt(containerCounter);
			merchantrecipelist.a(packetdataserializer);
			nmsPlayer.playerConnection.sendPacket(new PacketPlayOutCustomPayload("MC|TrList", packetdataserializer));

			return container.getBukkitView();
		}*/

		return null;
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

			for(org.bukkit.inventory.MerchantRecipe merchantRecipe : merchantRecipes) {
				merchantRecipeList.add(CraftMerchantRecipe.fromBukkit(merchantRecipe).toMinecraft());
			}

			return merchantRecipeList;
		}

		@Override
		public void a(net.minecraft.server.v1_14_R1.MerchantRecipe merchantRecipe) {

		}

		@Override
		public void i(ItemStack itemStack) {

		}

		@Override
		public World getWorld() {
			return null;
		}

		@Override
		public int getExperience() {
			return 0;
		}

		@Override
		public void r(int i) {

		}

		@Override
		public boolean ea() {
			return false;
		}

		@Override
		public SoundEffect eb() {
			return null;
		}

		@Override
		public CraftMerchant getCraftMerchant() {
			return null;
		}
	}

	/*public static class CustomMerchant implements IMerchant {
		private final IChatBaseComponent title;
		private final List<MerchantRecipe> merchantRecipes;
		private EntityHuman tradingWith = null;

		public CustomMerchant(IChatBaseComponent title, List<MerchantRecipe> merchantRecipes) {
			this.title = title;
			this.merchantRecipes = merchantRecipes;
		}

		@Override
		public void a(final net.minecraft.server.v1_14_R1.MerchantRecipe merchantRecipe) {

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
		public void a(final net.minecraft.server.v1_14_R1.ItemStack itemStack) {

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

		@Override
		public World u_() {
			return null;
		}

		@Override
		public BlockPosition v_() {
			return null;
		}
	}*/
}