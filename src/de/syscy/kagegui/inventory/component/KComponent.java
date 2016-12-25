package de.syscy.kagegui.inventory.component;

import org.bukkit.inventory.ItemStack;

import de.syscy.kagegui.IComponent;
import de.syscy.kagegui.IInventoryWrapper;
import de.syscy.kagegui.icon.ItemIcon;
import de.syscy.kagegui.inventory.KGUI;
import de.syscy.kagegui.util.Lore;
import de.syscy.kagegui.util.Util;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

public abstract class KComponent implements IComponent {
	protected @Getter @Setter KGUI gui;

	protected @Getter @Setter int x;
	protected @Getter @Setter int y;
	private @Getter @Setter int offsetX;
	private @Getter @Setter int offsetY;
	protected @Getter @Setter int width;
	protected @Getter @Setter int height;

	protected KComponent(Builder<?> builder) {
		x = builder.x;
		y = builder.y;
		offsetX = builder.offsetX;
		offsetY = builder.offsetY;
		width = builder.width;
		height = builder.height;
	}

	public void renderItem(IInventoryWrapper inventory, int x, int y, int width, int height, ItemIcon icon, String title, Lore lore) {
		renderItem(inventory, x, y, width, height, icon.getItem(title, lore));
	}

	public void renderItem(IInventoryWrapper inventory, int x, int y, int width, int height, ItemStack itemStack) {
		for(int i = x; i < x + width; i++) {
			for(int j = y; j < y + height; j++) {
				inventory.setItem(Util.toSlotCoordinate(i + offsetX, j + offsetY), itemStack);
			}
		}
	}

	@Accessors(fluent = true)
	public static abstract class Builder<T extends KComponent> {
		protected @Setter int x = 0;
		protected @Setter int y = 0;
		protected @Setter int offsetX = 0;
		protected @Setter int offsetY = 0;
		protected @Setter int width = 1;
		protected @Setter int height = 1;

		public abstract T build();
	}
}