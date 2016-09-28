package de.syscy.bguilib.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.syscy.bguilib.BGHotbarGUI;
import de.syscy.bguilib.BGUILib;
import de.syscy.bguilib.creator.BGCreator;
import de.syscy.bguilib.creator.hotbarguidata.HotbarGUIData;
import de.syscy.kagecore.KageCorePlugin;

public class HotbarGUIListener implements Listener {
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		this.updatePlayerHotbar(event.getPlayer());
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		if(BGUILib.getCurrentHotbarGuis().containsKey(event.getPlayer())) {
			BGUILib.removeHotbarGUI(event.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		this.updatePlayerHotbar(event.getPlayer());
	}

	public void updatePlayerHotbar(Player player) {
		String worldName = player.getLocation().getWorld().getName();
		String worldHotbar = KageCorePlugin.getPluginConfig().getString("hotbar." + worldName);

		if(worldHotbar != null) {
			if(!worldHotbar.isEmpty()) {
				HotbarGUIData gui = (HotbarGUIData) BGCreator.getHotbarGuis().get(worldHotbar);

				if(gui != null) {
					BGUILib.setHotbarGUI(gui.toHGUI(), player);
				}
			} else if(BGUILib.getCurrentHotbarGuis().containsKey(player)) {
				BGUILib.removeHotbarGUI(player);
			}
		} else if(BGUILib.getCurrentHotbarGuis().containsKey(player)) {
			BGUILib.removeHotbarGUI(player);
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(BGUILib.getCurrentHotbarGuis().containsKey(event.getPlayer())) {
			BGHotbarGUI hotbarGUI = (BGHotbarGUI) BGUILib.getCurrentHotbarGuis().get(event.getPlayer());
			hotbarGUI.onClick(event.getPlayer().getInventory().getHeldItemSlot());

			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onDropItem(PlayerDropItemEvent event) {
		if(BGUILib.getCurrentHotbarGuis().containsKey(event.getPlayer())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if(event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();

			if(BGUILib.getCurrentHotbarGuis().containsKey(player)) {
				event.getDrops().clear();
				event.setDroppedExp(0);
			}
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();

		if(BGUILib.getCurrentHotbarGuis().containsKey(player)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onInventoryPickupItem(InventoryPickupItemEvent event) {
		if(event.getInventory().getHolder() instanceof Player) {
			Player player = (Player) event.getInventory().getHolder();

			if(BGUILib.getCurrentHotbarGuis().containsKey(player)) {
				event.setCancelled(true);
			}
		}
	}
}