package de.syscy.kagecore.entityregistry;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public interface IEntityRegistry {
	public void init();

	public void registerEntity(int entityId, String entityName, Class<?> entityClass);

	public Entity spawnEntity(Class<?> entityClass, Location location);
}