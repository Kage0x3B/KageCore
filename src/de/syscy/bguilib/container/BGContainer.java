package de.syscy.bguilib.container;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import de.syscy.bguilib.BGGUI;
import de.syscy.bguilib.components.BGComponent;
import lombok.Getter;
import lombok.Setter;

public abstract class BGContainer {
	protected @Getter BGGUI gui;
	protected @Getter @Setter ItemStack background;

	public BGContainer() {
		this.background = new ItemStack(Material.AIR);
	}

	public void init(BGGUI gui) {
		this.gui = gui;
	}

	public abstract void add(BGComponent component);

	public abstract void update();

	public abstract void render();

	public abstract void onClick(InventoryClickEvent event, int x, int y);

	protected void clear() {
		this.gui.getBgInventory().clear();
	}
}