package de.syscy.kagecore.factory;

import org.bukkit.configuration.file.YamlConfiguration;

public interface FactoryTemplate<T> {
	public void load(AdventureFactory<T> factory, String name, YamlConfiguration template) throws Exception;

	public T create(Object... args) throws Exception;
}