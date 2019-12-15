package de.syscy.kagecore.factory.entity;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public interface EntityFactoryNMS {
	public Entity createEntity(String entityTypeID, Location location, String nbt);
}