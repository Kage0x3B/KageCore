package de.syscy.kagecore.worldedit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@UtilityClass
public class WorldEditUtil {
	private static @Getter WorldEditPlugin worldEditPlugin;
	private static @Getter WorldEdit worldEdit;

	private static @Getter(value = AccessLevel.PROTECTED) Map<org.bukkit.World, World> worldCache = new HashMap<>();

	protected static boolean initWorldEdit() {
		if(worldEditPlugin == null) {
			Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldEdit");

			if(plugin != null) {
				worldEditPlugin = (WorldEditPlugin) plugin;
				worldEdit = worldEditPlugin.getWorldEdit();
			} else {
				Logger.getLogger("KageCore").info("WorldEdit is not installed!");

				return false;
			}
		}

		return true;
	}

	public static World getWEWorld(org.bukkit.World world) {
		if(!initWorldEdit()) {
			return null;
		}

		if(worldCache.containsKey(world)) {
			return worldCache.get(world);
		}

		World weWorld = new BukkitWorld(world);
		worldCache.put(world, weWorld);

		return weWorld;
	}

	public static EditSession createSession(World world) {
		return worldEdit.getEditSessionFactory().getEditSession(world, Integer.MAX_VALUE);
	}

	public static Location toLocation(World world, BlockVector3 vector) {
		return new Location(Bukkit.getWorld(world.getName()), vector.getX(), vector.getY(), vector.getZ());
	}

	public static Location toLocation(org.bukkit.World world, BlockVector3 vector) {
		return new Location(world, vector.getX(), vector.getY(), vector.getZ());
	}

	public static BlockVector3 toVector(Location location) {
		return BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}

	public static BlockVector3 toVector(org.bukkit.util.Vector vector) {
		return BlockVector3.at(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
	}
}