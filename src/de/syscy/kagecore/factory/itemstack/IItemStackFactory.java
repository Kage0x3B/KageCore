package de.syscy.kagecore.factory.itemstack;

import de.syscy.kagecore.factory.IFactory;

import org.bukkit.inventory.ItemStack;

public interface IItemStackFactory extends IFactory<ItemStack> {
	ItemStack create(String templateName);
}