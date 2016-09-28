package de.syscy.kagecore.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.bukkit.Location;
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

		populateFields();
	}
	
	protected void populateFields(KConfiguration config) {
		this.configSection = config.getConfig().getConfigurationSection(path);

		populateFields();
	}

	private void populateFields() {
		for(Field field : getClass().getDeclaredFields()) {
			try {
				if(!Modifier.isFinal(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
					field.setAccessible(true);

					String path = this.path;

					if(KConfigurationSection.class.isAssignableFrom(field.getType())) {
						field.set(this, field.getType().getConstructor(KConfiguration.class).newInstance(this));

						continue;
					}

					if(field.getAnnotation(ConfigValue.class) != null) {
						if(field.getAnnotation(ConfigValue.class).ignore()) {
							continue;
						}
						
						path = field.getAnnotation(ConfigValue.class).path();
					}

					if(!path.endsWith(".")) {
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

	public static class LocationSection extends KConfigurationSection {
		@ConfigValue(ignore=true)
		private @Getter Location location;
		
		private @Getter int blockX;
		private @Getter int blockY;
		private @Getter int blockZ;
		
		public LocationSection(String path) {
			super(path);
		}
		
		@Override
		protected void populateFields(KConfiguration config) {
			super.populateFields(config);
			
			location = new Location(null, blockX, blockY, blockZ);
		}
		
		public void setLocation(Location location) {
			this.location = location;
			
			if(configSection != null) {
				configSection.set("blockX", location.getBlockX());
				configSection.set("blockY", location.getBlockY());
				configSection.set("blockZ", location.getBlockZ());
			}
		}
	}
}