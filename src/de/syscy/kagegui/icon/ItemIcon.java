package de.syscy.kagegui.icon;

import de.syscy.kagegui.IInventoryGUI;
import de.syscy.kagegui.util.LoreBuilder;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import lombok.Getter;
import lombok.ToString;

@ToString
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

	public ItemStack getItem(String title, final LoreBuilder loreBuilder) {
		ItemMeta newItemMeta = itemStack.getItemMeta();

		if(title != null && !title.isEmpty()) {
			newItemMeta.setDisplayName(title);
		}

		if(!loreBuilder.isEmpty()) {
			newItemMeta.setLore(loreBuilder.getLore());
		}

		itemStack.setItemMeta(newItemMeta);

		return itemStack;
	}
}