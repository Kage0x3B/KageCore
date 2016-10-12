package de.syscy.kagecore.factory;

import org.bukkit.plugin.Plugin;

import de.syscy.kagecore.factory.entity.EntityFactory;
import de.syscy.kagecore.factory.itemstack.ItemStackFactory;

public interface IFactoryProviderPlugin extends Plugin {
	public ItemStackFactory getItemStackFactory();

	public EntityFactory getEntityFactory();
}