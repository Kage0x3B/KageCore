package de.syscy.kagegui;

import org.bukkit.inventory.Inventory;

public interface IInventoryGUI<T extends IComponent> {
	public void add(T component);

	public void update();

	public void render();

	public void markDirty();

	public boolean isDirty();

	public Inventory getBukkitInventory();

	public IInventoryWrapper getInventoryWrapper();
}