package de.syscy.kagecore.factory.itemstack;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface ItemFactoryNMS {
	public ItemStack createItemStack(Material material, int data, String nbt) throws Exception;
}