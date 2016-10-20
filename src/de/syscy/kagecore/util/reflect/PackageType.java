package de.syscy.kagecore.util.reflect;

import org.bukkit.Bukkit;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PackageType {
	//@formatter:off
	MINECRAFT_SERVER("net.minecraft.server." + getServerVersion()),
	CRAFTBUKKIT("org.bukkit.craftbukkit." + getServerVersion()),
	CRAFTBUKKIT_ATTRIBUTE(CRAFTBUKKIT, "attribute"),
	CRAFTBUKKIT_BLOCK(CRAFTBUKKIT, "block"),
	CRAFTBUKKIT_BOSS(CRAFTBUKKIT, "boss"),
	CRAFTBUKKIT_CHUNKIO(CRAFTBUKKIT, "chunkio"),
	CRAFTBUKKIT_COMMAND(CRAFTBUKKIT, "command"),
	CRAFTBUKKIT_CONVERSATIONS(CRAFTBUKKIT, "conversations"),
	CRAFTBUKKIT_ENCHANTMENS(CRAFTBUKKIT, "enchantments"),
	CRAFTBUKKIT_ENTITY(CRAFTBUKKIT, "entity"),
	CRAFTBUKKIT_EVENT(CRAFTBUKKIT, "event"),
	CRAFTBUKKIT_GENERATOR(CRAFTBUKKIT, "generator"),
	CRAFTBUKKIT_HELP(CRAFTBUKKIT, "help"),
	CRAFTBUKKIT_INVENTORY(CRAFTBUKKIT, "inventory"),
	CRAFTBUKKIT_MAP(CRAFTBUKKIT, "map"),
	CRAFTBUKKIT_METADATA(CRAFTBUKKIT, "metadata"),
	CRAFTBUKKIT_POTION(CRAFTBUKKIT, "potion"),
	CRAFTBUKKIT_PROJECTILES(CRAFTBUKKIT, "projectiles"),
	CRAFTBUKKIT_SCHEDULER(CRAFTBUKKIT, "scheduler"),
	CRAFTBUKKIT_SCOREBOARD(CRAFTBUKKIT, "scoreboard"),
	CRAFTBUKKIT_UTIL(CRAFTBUKKIT, "util");
	//@formatter:on

	private final @Getter String path;

	/**
	 * Construct a new package type
	 *
	 * @param parent Parent package of the package
	 * @param path Path of the package
	 */
	private PackageType(PackageType parent, String path) {
		this(parent + "." + path);
	}

	/**
	 * Returns the wrapped  class with the given name
	 *
	 * @param className Name of the desired class
	 * @return a Reflect object
	 */
	public Reflect getClass(String className) throws ClassNotFoundException {
		return Reflect.on(this + "." + className);
	}

	@Override
	public String toString() {
		return path;
	}

	/**
	 * Returns the version of your server
	 *
	 * @return The server version
	 */
	public static String getServerVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().substring(23);
	}
}