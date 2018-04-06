package de.syscy.kagecore.factory.entity;

import java.io.File;

import de.syscy.kagecore.factory.AbstractFactory;
import de.syscy.kagecore.factory.IFactoryProviderPlugin;
import de.syscy.kagecore.factory.IFactoryTemplate;
import de.syscy.kagecore.versioncompat.VersionCompatClassLoader;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.metadata.FixedMetadataValue;

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

		IFactoryTemplate<Entity> template = templates.get(templateName.toLowerCase());

		if(template == null) {
			EntityType entityType = EntityType.valueOf(templateName.toUpperCase());

			Entity entity = location.getWorld().spawnEntity(location, entityType);
			entity.setMetadata("templateName", new FixedMetadataValue(plugin, templateName.toLowerCase()));

			return entity;
		}

		try {
			return template.create(location);
		} catch(Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	@Override
	protected IFactoryTemplate<Entity> createTemplate() {
		return new EntityFactoryTemplate(entityFactoryNMS);
	}
}