package de.syscy.kagecore.factory;

import org.bukkit.configuration.file.YamlConfiguration;

public interface IFactoryTemplate<T> {
	void load(IFactory<T> factory, String name, YamlConfiguration template) throws Exception;

	T create(Object... args) throws Exception;
}