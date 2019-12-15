package de.syscy.kagegui.inventory.listener;

import org.bukkit.entity.Player;

public interface CheckButtonToggleListener {
	public void onToggle(Player player, boolean enabled);
}