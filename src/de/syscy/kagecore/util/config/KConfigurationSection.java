package de.syscy.kagecore.util.config;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.bukkit.configuration.ConfigurationSection;

import lombok.Getter;

public class KConfigurationSection {
	protected @Getter ConfigurationSection configSection;
	private final @Getter String path;
	
	public KConfigurationSection(String path) {
		this.path = path;
	}

	protected KConfigurationSection(KConfiguration config, String path) {
		this.configSection = config.getConfig().getConfigurationSection(path);
		this.path = path;
	}

	protected void populateFields(KConfiguration config) {
		this.configSection = config.getConfig().getConfigurationSection(path);
		
		for(Field field : getClass().getDeclaredFields()) {
			try {
				if(!Modifier.isFinal(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
					field.setAccessible(true);

					String path = "";

					if(KConfigurationSection.class.isAssignableFrom(field.getType())) {
						if(field.get(this) == null) {
							field.set(this, field.getType().getConstructor(KConfiguration.class).newInstance(this));
							((KConfigurationSection) field.get(this)).populateFields(config);
						} else {
							((KConfigurationSection) field.get(this)).populateFields(config);
						}

						continue;
					}

					if(field.getAnnotation(ConfigValue.class) != null) {
						if(field.getAnnotation(ConfigValue.class).ignore()) {
							continue;
						}
						
						path = field.getAnnotation(ConfigValue.class).path();
					}

					if(!path.isEmpty() && !path.endsWith(".")) {
						path += ".";
					}

					if(configSection.contains(path + field.getName())) {
						field.set(this, configSection.get(path + field.getName()));
					}
				}
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}