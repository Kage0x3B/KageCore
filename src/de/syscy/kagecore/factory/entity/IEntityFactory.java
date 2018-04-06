package de.syscy.kagecore.factory.entity;

import de.syscy.kagecore.factory.IFactory;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public interface IEntityFactory extends IFactory<Entity> {
	Entity create(String templateName, Location location);
}