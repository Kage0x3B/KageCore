package de.syscy.kagecore.factory.entity;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.metadata.FixedMetadataValue;

import de.syscy.kagecore.KageCore;
import de.syscy.kagecore.factory.AbstractAdventureFactory;
import de.syscy.kagecore.factory.FactoryTemplate;
import de.syscy.kagecore.factory.IFactoryProviderPlugin;
import de.syscy.kagecore.factory.InvalidTemplateException;
import de.syscy.kagecore.versioncompat.VersionCompatClassLoader;
import lombok.Getter;

public class EntityFactory extends AbstractAdventureFactory<Entity> {
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

	public Entity create(String templateName, Location location) {
		if(templateName == null || templateName.isEmpty()) {
			return null;
		}

		FactoryTemplate<Entity> template = templates.get(templateName.toLowerCase());

		if(template == null) {
			EntityType entityType = EntityType.valueOf(templateName.toUpperCase());

			if(entityType != null) {
				Entity entity = location.getWorld().spawnEntity(location, entityType);
				entity.setMetadata("templateName", new FixedMetadataValue(KageCore.getInstance(), templateName.toLowerCase()));

				return entity;
			} else {
				throw new InvalidTemplateException("There is no template/EntityType with the name \"" + templateName.toLowerCase() + "\"!");
			}
		}

		try {
			return template.create(location);
		} catch(Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	@Override
	protected FactoryTemplate<Entity> createTemplate() {
		return new EntityFactoryTemplate(entityFactoryNMS);
	}
}