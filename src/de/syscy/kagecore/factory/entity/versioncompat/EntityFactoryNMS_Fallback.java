package de.syscy.kagecore.factory.entity.versioncompat;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import de.syscy.kagecore.KageCore;
import de.syscy.kagecore.factory.entity.EntityFactoryNMS;

public class EntityFactoryNMS_Fallback implements EntityFactoryNMS {
	@Override
	public Entity createEntity(String entityTypeID, Location location, String nbt) {
		KageCore.debugMessage("The Entity Factory doesn't work with this version!");
		return null;
	}
}