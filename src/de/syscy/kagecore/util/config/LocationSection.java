package de.syscy.kagecore.util.config;

import org.bukkit.Location;

import lombok.Getter;

public class LocationSection extends KConfigurationSection {
	@ConfigValue(ignore = true)
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