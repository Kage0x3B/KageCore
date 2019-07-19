package de.syscy.kagecore.entityregistry;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public interface IEntityRegistry {
	void init();

	void registerEntity(String entityName, String replacingName, Object entityConstructor);

	Entity spawnEntity(String entityName, Location location);
}