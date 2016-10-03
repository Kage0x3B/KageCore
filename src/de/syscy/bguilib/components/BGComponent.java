package de.syscy.bguilib.components;

import org.bukkit.entity.Player;

import de.syscy.bguilib.BGGUI;
import de.syscy.bguilib.components.icon.ItemIcon;
import de.syscy.bguilib.util.Lore;
import de.syscy.bguilib.util.Util;
import de.syscy.kagecore.KageCore;
import lombok.Getter;
import lombok.Setter;

public abstract class BGComponent {
	private @Getter BGGUI gui;
	protected @Getter @Setter int x = 0;
	protected @Getter @Setter int y = 0;
	private @Getter @Setter int offsetX = 0;
	private @Getter @Setter int offsetY = 0;
	protected @Getter int width = 0;
	protected @Getter int height = 0;

	public BGComponent(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public void init(BGGUI gui) {
		this.gui = gui;
	}

	public abstract void update();

	public abstract void render(BGInventory inventory);
	

	public void renderItem(BGInventory inventory, int x, int y, int width, int height, ItemIcon item, String title, Lore lore) {
		renderItem(inventory, x, y, width, height, item, title, lore, false);
	}

	public void renderItem(BGInventory inventory, int x, int y, int width, int height, ItemIcon item, String title, Lore lore, boolean forceUpdate) {
		for(int i = x; i < x + width; i++) {
			for(int j = y; j < y + height; j++) {
				int slot = Util.toSlotCoordinate(i + this.offsetX, j + this.offsetY);
				
				if(slot >= 0 && slot < inventory.getSize()) {
					KageCore.debugMessage("rendering "+ this + " at " + x + ", " + y);
					inventory.setItem(slot, item.getItem(title, lore), forceUpdate);
				}
			}
		}
	}

	public abstract void onClick(Player player, int localX, int localY);

	public void setWidth(int width) {
		if(width > 9 - this.x) {
			width = 9 - this.x;
		}

		if(width < 1) {
			width = 1;
		}

		this.width = width;
	}

	public void setHeight(int height) {
		if(height < 1) {
			height = 1;
		}

		if(height > 6) {
			KageCore.debugMessage("Warning! If the height of a gui is greater than 6, it may look weird!");
		}

		this.height = height;
	}

	public void setSize(int width, int height) {
		setWidth(width);
		setHeight(height);
	}
}