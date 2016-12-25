package de.syscy.kagegui.inventory.listener;

import org.bukkit.entity.Player;

import de.syscy.kagegui.inventory.component.KButton;

public interface ButtonClickListener {
	public void onClick(KButton button, Player player);
}