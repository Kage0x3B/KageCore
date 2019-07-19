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

	public static void registerEntity(String entityName, String replacingName, Object entityConstructor) {
		entityRegistry.registerEntity(entityName, replacingName, entityConstructor);
	}

	public static Entity spawnEntity(String entityName, Location location) {
		return entityRegistry.spawnEntity(entityName, location);
	}
}