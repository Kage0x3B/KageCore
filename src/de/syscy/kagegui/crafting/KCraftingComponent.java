package de.syscy.kagegui.crafting;

import org.bukkit.inventory.ItemStack;

import de.syscy.kagegui.IComponent;
import de.syscy.kagegui.IInventoryWrapper;
import de.syscy.kagegui.icon.ItemIcon;
import de.syscy.kagegui.util.Lore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

public abstract class KCraftingComponent implements IComponent {
	protected @Getter @Setter KCraftingGUI gui;
	protected @Getter int craftingSlot = 0;

	protected KCraftingComponent(Builder<?> builder) {
		craftingSlot = builder.craftingSlot;
	}

	public void renderItem(IInventoryWrapper inventory, int slot, ItemIcon icon, String title, Lore lore) {
		renderItem(inventory, slot, icon.getItem(title, lore));
	}

	public void renderItem(IInventoryWrapper inventory, int slot, ItemStack itemStack) {
		inventory.setItem(slot, itemStack);
	}

	@Accessors(fluent = true)
	public static abstract class Builder<T extends KCraftingComponent> {
		protected @Setter int craftingSlot = 0;

		public abstract T build();
	}
}