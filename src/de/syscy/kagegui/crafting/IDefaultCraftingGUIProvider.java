package de.syscy.kagegui.crafting;

import org.bukkit.entity.Player;

public interface IDefaultCraftingGUIProvider {
	public KCraftingGUI createInstance(Player player);
}