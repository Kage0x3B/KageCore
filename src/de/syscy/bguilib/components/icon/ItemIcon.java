package de.syscy.bguilib.components.icon;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.syscy.bguilib.util.Lore;
import lombok.Getter;

public class ItemIcon {
	protected @Getter ItemStack itemStack;

	public ItemIcon(ItemStack item) {
		this.itemStack = item.clone();
	}

	public void update() {
	}

	public ItemStack getItem(String title, Lore lore) {
		ItemMeta newItemMeta = this.itemStack.getItemMeta();
		newItemMeta.setDisplayName(title);
		newItemMeta.setLore(lore.getAsList());
		this.itemStack.setItemMeta(newItemMeta);

		return this.itemStack;
	}
}