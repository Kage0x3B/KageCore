package de.syscy.kagegui.listener;

import de.syscy.kagegui.KageGUI;
import de.syscy.kagegui.crafting.KCraftingGUI;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftInventoryCrafting;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CraftingGUIListener implements Listener {
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if(KageGUI.getDefaultCraftingGUIProvider() != null) {
			KageGUI.setCraftingGUI(KageGUI.getDefaultCraftingGUIProvider().createInstance(event.getPlayer()), event.getPlayer());
		}
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		if(KageGUI.getCurrentCraftingGuis().containsKey(event.getPlayer())) {
			KageGUI.removeCraftingGUI(event.getPlayer());
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if(event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();

			if(KageGUI.getCurrentCraftingGuis().containsKey(player)) {
				KCraftingGUI craftingGUI = KageGUI.getCurrentCraftingGuis().get(player);

				for(ItemStack craftingGUIItem : craftingGUI.getInventoryWrapper().getLastItems()) {
					event.getDrops().remove(craftingGUIItem);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();

		if(player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
			return;
		}

		if(KageGUI.getCraftingGuiInteractBlock() > 0) {
			return;
		}

		if(player.getOpenInventory() == null || !isInventoryCrafting(player.getOpenInventory().getTopInventory())) {
			return;
		}

		if(KageGUI.getCurrentCraftingGuis().containsKey(player)) {
			KCraftingGUI craftingGUI = KageGUI.getCurrentCraftingGuis().get(player);

			if(event.getRawSlot() >= 0 && event.getRawSlot() <= 4) {
				event.setCancelled(true);
				player.updateInventory(); //TODO: Works but maybe don't update the whole inv.
				craftingGUI.getInventoryWrapper().flush(true);

				craftingGUI.onClick(event, event.getRawSlot());
			}
		}
	}

	private boolean isInventoryCrafting(Inventory inventory) {
		if(!(inventory instanceof CraftInventoryCrafting)) {
			return false;
		}

		//Workbenches have a size of 10 (9 crafting slots + 1 result slot), player inv crafting only a size of 5
		return inventory.getSize() <= 5;
	}

	@EventHandler
	public void onDropItem(PlayerDropItemEvent event) {
		if(KageGUI.getCurrentCraftingGuis().containsKey(event.getPlayer())) {
			KCraftingGUI craftingGUI = KageGUI.getCurrentCraftingGuis().get(event.getPlayer());

			for(ItemStack craftingGUIItem : craftingGUI.getInventoryWrapper().getLastItems()) {
				if(event.getItemDrop().getItemStack().equals(craftingGUIItem)) {
					event.getItemDrop().remove();

					craftingGUI.markDirty();
				}
			}
		}
	}
}