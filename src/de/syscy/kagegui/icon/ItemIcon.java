package de.syscy.kagegui.icon;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.syscy.kagegui.IInventoryGUI;
import de.syscy.kagegui.util.Lore;
import lombok.Getter;

public class ItemIcon {
	protected @Getter ItemStack itemStack;

	public ItemIcon(Material material) {
		itemStack = new ItemStack(material);
	}

	public ItemIcon(ItemStack item) {
		itemStack = item.clone();
	}

	public void update(IInventoryGUI<?> gui) {

	}

	public ItemStack getItem(String title, Lore lore) {
		ItemMeta newItemMeta = itemStack.getItemMeta();

		if(title != null && !title.isEmpty()) {
			newItemMeta.setDisplayName(title);
		}

		if(lore != null && !lore.getAsList().isEmpty()) {
			newItemMeta.setLore(lore.getAsList());
		}

		itemStack.setItemMeta(newItemMeta);

		return itemStack;
	}
}