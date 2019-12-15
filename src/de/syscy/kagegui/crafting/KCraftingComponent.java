package de.syscy.kagegui.crafting;

import org.bukkit.inventory.ItemStack;

import de.syscy.kagegui.IComponent;
import de.syscy.kagegui.IInventoryWrapper;
import de.syscy.kagegui.icon.ItemIcon;
import de.syscy.kagegui.util.LoreBuilder;
import lombok.Getter;
import lombok.Setter;

public abstract class KCraftingComponent implements IComponent {
	protected @Getter @Setter KCraftingGUI gui;
	protected @Getter @Setter int craftingSlot;

	protected KCraftingComponent(int craftingSlot) {
		this.craftingSlot = craftingSlot;
	}

	public void renderItem(IInventoryWrapper inventory, int slot, ItemIcon icon, String title, LoreBuilder loreBuilder) {
		renderItem(inventory, slot, icon.getItem(title, loreBuilder));
	}

	public void renderItem(IInventoryWrapper inventory, int slot, ItemStack itemStack) {
		inventory.setItem(slot, itemStack);
	}
}