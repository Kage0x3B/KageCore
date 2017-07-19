package de.syscy.kagegui.listener;

import java.util.Arrays;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftInventoryCrafting;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import de.syscy.kagegui.KageGUI;
import de.syscy.kagegui.crafting.KCraftingGUI;

public class CraftingGUIListener implements Listener {
	private List<GameMode> gameModes = Arrays.asList(GameMode.SURVIVAL, GameMode.ADVENTURE);

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

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();

		if(KageGUI.getCurrentCraftingGuis().containsKey(player) && gameModes.contains(player.getGameMode()) && player.getOpenInventory().getTopInventory() instanceof CraftInventoryCrafting) {
			KCraftingGUI craftingGUI = KageGUI.getCurrentCraftingGuis().get(player);

			if(event.getRawSlot() >= 0 && event.getRawSlot() <= 4) {
				event.setCancelled(true);

				craftingGUI.onClick(event, event.getRawSlot());
			}
		}
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