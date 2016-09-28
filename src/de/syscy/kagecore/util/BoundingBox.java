package de.syscy.kagecore.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import lombok.Getter;

public class BoundingBox {
	private @Getter Location min;
	private @Getter Location max;

	public BoundingBox(Location min, Location max) {
		if(min.getWorld() != max.getWorld()) {
			throw new IllegalArgumentException("Both provided locations need to be in the same world");
		}

		this.min = new Location(min.getWorld(), min.getX() < max.getX() ? min.getX() : max.getX(), min.getY() < max.getY() ? min.getY() : max.getY(), min.getZ() < max.getZ() ? min.getZ() : max.getZ());
		this.max = new Location(min.getWorld(), min.getX() > max.getX() ? min.getX() : max.getX(), min.getY() > max.getY() ? min.getY() : max.getY(), min.getZ() > max.getZ() ? min.getZ() : max.getZ());
	}
	
	public boolean contains(Location loc) {
		return (min.getWorld() == null || loc.getWorld() == min.getWorld()) && min.getX() <= loc.getX() && max.getX() >= loc.getX() && min.getY() <= loc.getY() && max.getY() >= loc.getY() && min.getZ() <= loc.getZ() && max.getZ() >= loc.getZ();
	}

	@Override
	public String toString() {
		return min.getWorld().getName() + "," + min.getBlockX() + "," + min.getBlockY() + "," + min.getBlockZ() + "," + max.getBlockX() + "," + max.getBlockY() + "," + max.getBlockZ();
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
}