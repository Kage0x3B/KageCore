package de.syscy.bguilib.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import de.syscy.bguilib.BGGUI;
import de.syscy.bguilib.BGUILib;

public class GUIListener implements Listener {
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if(event.getWhoClicked() instanceof Player) {
			Player player = (Player) event.getWhoClicked();

			if(BGUILib.getCurrentGuis().containsKey(player)) {
				((BGGUI) BGUILib.getCurrentGuis().get(player)).onClick(event);
			}
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if(event.getPlayer() instanceof Player) {
			Player player = (Player) event.getPlayer();

			if(BGUILib.getCurrentGuis().containsKey(player)) {
				((BGGUI) BGUILib.getCurrentGuis().get(player)).onClose(event);
			}
		}
	}
}