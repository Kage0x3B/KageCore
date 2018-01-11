package de.syscy.kagecore.util.config;

import org.bukkit.Location;

import lombok.Getter;

public class LocationSection extends KConfigurationSection {
	@ConfigValue(ignore = true)
	private @Getter Location location;

	private @Getter float x;
	private @Getter float y;
	private @Getter float z;

	public LocationSection(String path) {
		super(path);
	}

	@Override
	protected void populateFields(KConfiguration config) {
		super.populateFields(config);

		location = new Location(null, x, y, z);
	}

	public void setLocation(Location location) {
		this.location = location;

		if(configSection != null) {
			configSection.set("x", location.getBlockX());
			configSection.set("y", location.getBlockY());
			configSection.set("z", location.getBlockZ());
		}
	}
}