package de.syscy.kagecore.factory.itemstack.versioncompat;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import de.syscy.kagecore.factory.itemstack.IItemFactoryNMS;

public class ItemFactoryNMS_Fallback implements IItemFactoryNMS {
	@Override
	public ItemStack createItemStack(Material material, int data, String nbt) {
		return new ItemStack(material, 1, (short) data);
	}
}