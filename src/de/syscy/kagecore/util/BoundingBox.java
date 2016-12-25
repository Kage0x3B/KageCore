package de.syscy.kagecore.util;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import com.sk89q.worldedit.bukkit.selections.CuboidSelection;

import lombok.Getter;
import lombok.ToString;

@ToString
public class BoundingBox implements ConfigurationSerializable {
	private @Getter Location min;
	private @Getter Location max;

	public BoundingBox(Map<String, Object> map) {
		this((Location) map.get("min"), (Location) map.get("max"));
	}

	public BoundingBox(CuboidSelection selection) {
		this(selection.getMinimumPoint(), selection.getMaximumPoint());
	}

	public BoundingBox(Location min, Location max) {
		if(min == null || max == null) {
			throw new IllegalArgumentException("The min/max locations can't be null");
		}

		if(min.getWorld() != max.getWorld()) {
			throw new IllegalArgumentException("Both provided locations need to be in the same world");
		}

		this.min = new Location(min.getWorld(), min.getX() < max.getX() ? min.getX() : max.getX(), min.getY() < max.getY() ? min.getY() : max.getY(), min.getZ() < max.getZ() ? min.getZ() : max.getZ());
		this.max = new Location(min.getWorld(), min.getX() > max.getX() ? min.getX() : max.getX(), min.getY() > max.getY() ? min.getY() : max.getY(), min.getZ() > max.getZ() ? min.getZ() : max.getZ());
	}

	public boolean contains(Location loc) {
		return (min.getWorld() == null || loc.getWorld() == min.getWorld()) && min.getX() <= loc.getX() && max.getX() >= loc.getX() - 1 && min.getY() <= loc.getY() && max.getY() >= loc.getY() - 1 && min.getZ() <= loc.getZ() && max.getZ() >= loc.getZ() - 1;
	}

	public static BoundingBox fromString(String string) {
		String[] bbSplit = string.split(",");

		if(bbSplit.length != 7) {
			throw new ParseException("Invalid BoundingBox string");
		}

		try {
			String worldName = bbSplit[0];
			World world = Bukkit.getWorld(worldName);

			int x = Integer.parseInt(bbSplit[1]);
			int y = Integer.parseInt(bbSplit[2]);
			int z = Integer.parseInt(bbSplit[3]);

			Location min = new Location(world, x, y, z);

			x = Integer.parseInt(bbSplit[4]);
			y = Integer.parseInt(bbSplit[5]);
			z = Integer.parseInt(bbSplit[6]);

			Location max = new Location(world, x, y, z);

			return new BoundingBox(min, max);
		} catch(NumberFormatException ex) {
			throw new ParseException("Invalid BoundingBox string. Cannot parse coordinates");
		}
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		map.put("min", min);
		map.put("max", max);

		return map;
	}
}