package de.syscy.kagegui.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;

import de.syscy.kagegui.inventory.component.KComponent;

public interface IContainer {
	public void init(KGUI gui);

	public void add(KComponent component);

	public void update();

	public void render();

	public void onClick(InventoryClickEvent event, int x, int y);
}