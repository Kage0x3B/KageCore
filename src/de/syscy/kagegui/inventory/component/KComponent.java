package de.syscy.kagegui.inventory.component;

import org.bukkit.inventory.ItemStack;

import de.syscy.kagegui.IComponent;
import de.syscy.kagegui.IInventoryWrapper;
import de.syscy.kagegui.icon.ItemIcon;
import de.syscy.kagegui.inventory.KGUI;
import de.syscy.kagegui.util.LoreBuilder;
import de.syscy.kagegui.util.Util;
import lombok.Getter;
import lombok.Setter;

public abstract class KComponent implements IComponent {
	protected @Getter @Setter KGUI gui;

	protected @Getter @Setter int x;
	protected @Getter @Setter int y;
	protected @Getter @Setter int offsetX = 0;
	protected @Getter @Setter int offsetY = 0;
	protected @Getter @Setter int width = 1;
	protected @Getter @Setter int height = 1;

	protected @Getter @Setter boolean visible = true;

	protected KComponent(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void renderItem(IInventoryWrapper inventory, int x, int y, int width, int height, ItemIcon icon, String title, final LoreBuilder loreBuilder) {
		renderItem(inventory, x, y, width, height, icon.getItem(title, loreBuilder));
	}

	public void renderItem(IInventoryWrapper inventory, int x, int y, int width, int height, ItemStack itemStack) {
		if(!visible) {
			return;
		}

		for(int i = x; i < x + width; i++) {
			for(int j = y; j < y + height; j++) {
				inventory.setItem(Util.toSlotCoordinate(i + offsetX, j + offsetY), itemStack);
			}
		}
	}
}