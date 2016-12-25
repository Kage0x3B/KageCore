package de.syscy.kagegui.icon;

import org.bukkit.inventory.ItemStack;

import de.syscy.kagegui.util.Lore;

public class RawItemIcon extends ItemIcon {
	public RawItemIcon(ItemStack item) {
		super(item);
	}

	@Override
	public ItemStack getItem(String title, Lore lore) {
		return itemStack;
	}
}