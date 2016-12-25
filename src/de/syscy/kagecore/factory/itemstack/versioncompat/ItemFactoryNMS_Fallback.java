package de.syscy.kagecore.factory.itemstack.versioncompat;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import de.syscy.kagecore.factory.itemstack.ItemFactoryNMS;

public class ItemFactoryNMS_Fallback implements ItemFactoryNMS {
	@Override
	public ItemStack createItemStack(Material material, int data, String nbt) throws Exception {
		return null;
	}
}