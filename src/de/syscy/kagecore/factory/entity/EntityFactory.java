package de.syscy.kagecore.factory.entity;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import de.syscy.kagecore.factory.AbstractAdventureFactory;
import de.syscy.kagecore.factory.FactoryTemplate;
import de.syscy.kagecore.factory.IFactoryProviderPlugin;
import de.syscy.kagecore.factory.InvalidTemplateException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EntityFactory extends AbstractAdventureFactory<Entity> {
	private final @Getter IFactoryProviderPlugin plugin;

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
				return location.getWorld().spawnEntity(location, entityType);
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
		return new EntityFactoryTemplate();
	}
}