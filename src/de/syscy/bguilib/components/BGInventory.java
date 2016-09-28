package de.syscy.bguilib.components;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;

public class BGInventory {
	private final @Getter Inventory inventory;

	private ItemStack[] buffer;
	private ItemStack[] lastItems;
	private boolean[] forceUpdateItem;

	public BGInventory(Inventory inventory) {
		this.inventory = inventory;

		buffer = new ItemStack[getSize()];
		lastItems = new ItemStack[getSize()];
		forceUpdateItem = new boolean[getSize()];
	}

	public void setItem(int slot, ItemStack itemStack) {
		setItem(slot, itemStack, false);
	}

	public void setItem(int slot, ItemStack itemStack, boolean forceUpdate) {
		buffer[slot] = itemStack;
		forceUpdateItem[slot] = forceUpdate;
	}

	public void clear() {
		for(int i = 0; i < getSize(); i++) {
			buffer[i] = null;
		}
	}

	public boolean flush() {
		boolean changed = false;

		for(int i = 0; i < getSize(); i++) {
			if((buffer[i] != null && !buffer[i].equals(lastItems[i])) || forceUpdateItem[i]) {
				inventory.setItem(i, buffer[i]);

				changed = true;
			}
		}

		lastItems = buffer.clone();
		forceUpdateItem = new boolean[getSize()];

		return changed;
	}

	public int getSize() {
		return inventory.getSize();
	}
}