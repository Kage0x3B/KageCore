package de.syscy.kagecore.entityregistry;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import de.syscy.kagecore.versioncompat.VersionCompatClassLoader;

public class EntityRegistry {
	private static IEntityRegistry entityRegistry;

	public static void init() {
		entityRegistry = VersionCompatClassLoader.loadClass(IEntityRegistry.class);
		entityRegistry.init();
	}

	public static IEntityRegistry getInstance() {
		return entityRegistry;
	}

	public static void registerEntity(int entityId, String entityName, Class<?> entityClass) {
		entityRegistry.registerEntity(entityId, entityName, entityClass);
	}

	public static Entity spawnEntity(Class<?> entityClass, Location location) {
		return entityRegistry.spawnEntity(entityClass, location);
	}
}