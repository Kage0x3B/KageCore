package de.syscy.kagecore.factory.entity;

import de.syscy.kagecore.factory.IFactoryTemplate;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public interface IEntityTemplate extends IFactoryTemplate<Entity> {
	public EntityFactory getEntityFactory();
	public String getTemplateName();
	public YamlConfiguration getTemplateYaml();

	public EntityType getEntityType();

	public String getCustomName();

	public int getFireTicks();
	public boolean isGlowing();
	public boolean isGravity();
	public boolean isInvulnerable();
	public boolean isSilent();
}