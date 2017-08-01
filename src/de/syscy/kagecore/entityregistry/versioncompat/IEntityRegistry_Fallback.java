package de.syscy.kagecore.entityregistry.versioncompat;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import de.syscy.kagecore.KageCore;
import de.syscy.kagecore.entityregistry.IEntityRegistry;

public class IEntityRegistry_Fallback implements IEntityRegistry {
	@Override
	public void init() {
		KageCore.debugMessage("Custom entities are not supported on this version.");
	}

	@Override
	public void registerEntity(int entityId, String entityName, Class<?> entityClass) {
		throw new UnsupportedOperationException("Custom entities are not supported on this version.");
	}

	@Override
	public Entity spawnEntity(Class<?> entityClass, Location location) {
		throw new UnsupportedOperationException("Custom entities are not supported on this version.");
	}
}