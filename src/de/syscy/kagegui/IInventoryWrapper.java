package de.syscy.kagegui;

import org.bukkit.inventory.ItemStack;

public interface IInventoryWrapper {
	public void setItem(int slot, ItemStack itemStack);

	public void flush(boolean sendToPlayer);

	public void sendBuffer();

	public void clear();

	public ItemStack[] getLastItems();
}