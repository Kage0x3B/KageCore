package de.syscy.kagecore.factory.entity.versioncompat;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import de.syscy.kagecore.factory.entity.EntityFactoryNMS;

public class EntityFactoryNMS_Fallback implements EntityFactoryNMS {
	@Override
	public Entity createEntity(String entityTypeID, Location location, String nbt) {
		return null;
	}
}