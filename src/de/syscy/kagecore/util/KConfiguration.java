package de.syscy.kagecore.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.bukkit.configuration.file.FileConfiguration;

import lombok.Getter;

public class KConfiguration {
	protected @Getter final FileConfiguration config;

	public KConfiguration(FileConfiguration config) {
		this.config = config;

		populateFields();
	}

	private void populateFields() {
		for(Field field : getClass().getDeclaredFields()) {
			try {
				if(!Modifier.isFinal(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
					field.setAccessible(true);

					if(KConfigurationSection.class.isAssignableFrom(field.getType())) {
						if(field.get(this) == null) {
							field.set(this, field.getType().getConstructor(KConfiguration.class).newInstance(this));
						} else {
							((KConfigurationSection) field.get(this)).populateFields(this);
						}

						continue;
					}

					String path = "";

					if(field.getAnnotation(ConfigValue.class) != null) {
						if(field.getAnnotation(ConfigValue.class).ignore()) {
							continue;
						}

						path = field.getAnnotation(ConfigValue.class).path();
					}

					if(!path.isEmpty() && !path.endsWith(".")) {
						path += ".";
					}

					if(config.contains(path + field.getName())) {
						field.set(this, config.get(path + field.getName()));
					}
				}
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}