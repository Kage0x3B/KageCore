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
	public void registerEntity(String entityName, String replacingName, Object entityConstructor) {
		throw new UnsupportedOperationException("Custom entities are not supported on this version.");
	}

	@Override
	public Entity spawnEntity(String entityName, Location location) {
		throw new UnsupportedOperationException("Custom entities are not supported on this version.");
	}
}