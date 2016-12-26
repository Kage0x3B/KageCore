package de.syscy.kagegui.icon;

import org.bukkit.inventory.ItemStack;

import de.syscy.kagegui.util.LoreBuilder;

public class RawItemIcon extends ItemIcon {
	public RawItemIcon(ItemStack item) {
		super(item);
	}

	@Override
	public ItemStack getItem(String title, LoreBuilder lore) {
		return itemStack;
	}
}