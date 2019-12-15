package de.syscy.kagecore.factory;

import de.syscy.kagecore.factory.entity.IEntityFactory;
import de.syscy.kagecore.factory.itemstack.IItemStackFactory;

import org.bukkit.plugin.Plugin;

public interface IFactoryProviderPlugin extends Plugin {
	IItemStackFactory getItemStackFactory();

	IEntityFactory getEntityFactory();
}