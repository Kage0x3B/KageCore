package de.syscy.kagecore.factory.entity;

import de.syscy.kagecore.event.SpawnTemplateEntityEvent;
import de.syscy.kagecore.factory.AbstractFactory;
import de.syscy.kagecore.factory.IFactoryProviderPlugin;
import de.syscy.kagecore.factory.IFactoryTemplate;
import de.syscy.kagecore.versioncompat.VersionCompatClassLoader;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.metadata.FixedMetadataValue;

import java.io.File;

public class EntityFactory extends AbstractFactory<Entity> implements IEntityFactory {
	private final @Getter IFactoryProviderPlugin plugin;
	private final EntityFactoryNMS entityFactoryNMS;

	public EntityFactory(IFactoryProviderPlugin plugin) {
		this.plugin = plugin;

		entityFactoryNMS = VersionCompatClassLoader.loadClass(EntityFactoryNMS.class);
	}

	@Override
	public void loadTemplates() {
		loadTemplates(new File(plugin.getDataFolder(), "entityTemplates"), "aet");
	}

	@Override
	public Entity create(String templateName, Location location) {
		if(templateName == null || templateName.isEmpty()) {
			return null;
		}

		IEntityTemplate template = (IEntityTemplate) templates.get(templateName.toLowerCase());

		if(template == null) {
			EntityType entityType = EntityType.valueOf(templateName.toUpperCase());

			Entity entity = location.getWorld().spawnEntity(location, entityType);
			entity.setMetadata("templateName", new FixedMetadataValue(plugin, templateName.toLowerCase()));

			Bukkit.getPluginManager().callEvent(new SpawnTemplateEntityEvent(entity, location, null, templateName));

			return entity;
		}

		try {
			Entity entity = template.create(location);

			Bukkit.getPluginManager().callEvent(new SpawnTemplateEntityEvent(entity, location, null, templateName));

			return entity;
		} catch(Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	@Override
	protected IFactoryTemplate<Entity> createTemplate() {
		return new EntityTemplate(entityFactoryNMS);
	}
}