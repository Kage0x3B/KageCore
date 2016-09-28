package de.syscy.bguilib.components;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;

public class BGHotbarInventory {
	private final @Getter Inventory inventory;

	private ItemStack[] buffer;
	private ItemStack[] lastItems;

	public BGHotbarInventory(Inventory inventory) {
		this.inventory = inventory;

		this.buffer = new ItemStack[9];
		this.lastItems = new ItemStack[9];
	}

	public void setItem(int slot, ItemStack itemStack) {
		this.buffer[slot] = itemStack;
	}

	public void clear() {
		for(int i = 0; i < 9; i++) {
			this.buffer[i] = null;
		}
	}

	public boolean flush() {
		boolean changed = false;
		
		for(int i = 0; i < 9; i++) {
			if(this.buffer[i] != null && !this.buffer[i].equals(this.lastItems[i])) {
				this.inventory.setItem(i, this.buffer[i]);
				changed = true;
			}
		}

		this.lastItems = buffer.clone();
		
		return changed;
	}

	public int getSize() {
		return this.inventory.getSize();
	}
}