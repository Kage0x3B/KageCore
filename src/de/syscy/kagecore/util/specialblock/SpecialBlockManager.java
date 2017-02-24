package de.syscy.kagecore.util.specialblock;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;

import de.syscy.kagecore.command.CommandBase;
import de.syscy.kagecore.command.CommandManager;
import de.syscy.kagecore.command.PlayerCommandBase;
import de.syscy.kagecore.command.argument.CommandArgument;
import de.syscy.kagecore.command.argument.StringArgument;
import de.syscy.kagecore.command.exception.InvalidSpecialBlockTypeException;
import de.syscy.kagecore.command.exception.MarkSpecialBlockFailedException;
import de.syscy.kagecore.translation.Translator;
import de.syscy.kagecore.util.ParticleEffects.OrdinaryColor;
import de.syscy.kagecore.util.ParticleUtil;
import lombok.Getter;

public class SpecialBlockManager extends CommandManager<JavaPlugin> implements Listener {
	//@formatter:off
	private static Color[] colors = {
		Color.fromRGB(0, 0, 0),
	    Color.fromRGB(0, 0, 170),
	    Color.fromRGB(0, 170, 0),
	    Color.fromRGB(0, 170, 170),
	    Color.fromRGB(170, 0, 0),
	    Color.fromRGB(170, 0, 170),
	    Color.fromRGB(255, 170, 0),
	    Color.fromRGB(170, 170, 170),
	    Color.fromRGB(85, 85, 85),
	    Color.fromRGB(85, 85, 255),
	    Color.fromRGB(85, 255, 85),
	    Color.fromRGB(85, 255, 255),
	    Color.fromRGB(255, 85, 85),
	    Color.fromRGB(255, 85, 255),
	    Color.fromRGB(255, 255, 85),
	    Color.fromRGB(255, 255, 255)
	};
	//@formatter:on

	private @Getter File specialBlockStorageFile;

	private Map<String, Integer> registeredTypes = new HashMap<>(15);
	private Map<String, ISpecialBlockMarkListener> registeredListeners = new HashMap<>(15);
	private ArrayListMultimap<String, Location> specialBlocksByType = ArrayListMultimap.create(15, 2);
	private Map<Location, String> specialBlocksByLocation = new HashMap<>(30);

	private Map<Player, String> currentPlayerChanges = new HashMap<>(2);
	private List<Player> currentHighlightingPlayers = new ArrayList<>(5);

	public SpecialBlockManager(JavaPlugin plugin) {
		super(plugin, "specialBlock");

		CommandArgument<?> typeArg = StringArgument.create("type").values(new Function<CommandSender, List<String>>() {
			@Override
			public List<String> apply(CommandSender sender) {
				return Lists.newArrayList(registeredTypes.keySet());
			}
		}).build();

		addCommand(new PlayerCommandBase<JavaPlugin>(plugin, "mark", typeArg) {
			@Override
			public void onPlayerCommand(Player sender) {
				String specialBlockType = arguments.getString("type");

				if(!registeredTypes.containsKey(specialBlockType.toLowerCase())) {
					throw new InvalidSpecialBlockTypeException(specialBlockType);
				}

				currentPlayerChanges.put(sender, specialBlockType.toLowerCase());
				Translator.sendMessage(sender, "command.specialBlock.leftClick", specialBlockType);
			}
		});

		addCommand(new PlayerCommandBase<JavaPlugin>(plugin, "unmark") {
			@Override
			public void onPlayerCommand(Player sender) {
				currentPlayerChanges.put(sender, "");
				Translator.sendMessage(sender, "command.specialBlock.unmarkLeftClick");
			}
		});

		addCommand(new CommandBase<JavaPlugin>(plugin, "list") {
			@Override
			public void onCommand(CommandSender sender) {
				for(String specialBlockType : specialBlocksByType.keySet()) {
					ChatColor chatColor = ChatColor.values()[colorIndexFromString(specialBlockType)];
					sender.sendMessage(chatColor + specialBlockType + " (" + specialBlocksByType.get(specialBlockType).size() + ")");
				}
			}
		});

		addCommand(new PlayerCommandBase<JavaPlugin>(plugin, "highlight") {
			@Override
			public void onPlayerCommand(Player sender) {
				if(currentHighlightingPlayers.remove(sender)) {
					Translator.sendMessage(sender, "command.specialBlock.stopHighlighting");
				} else {
					currentHighlightingPlayers.add(sender);
					Translator.sendMessage(sender, "command.specialBlock.startHighlighting");
				}
			}
		});

		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				if(currentHighlightingPlayers.isEmpty()) {
					return;
				}

				for(Entry<Location, String> specialBlockEntry : specialBlocksByLocation.entrySet()) {
					ParticleUtil.drawCube(specialBlockEntry.getKey(), new OrdinaryColor(colors[colorIndexFromString(specialBlockEntry.getValue())]), currentHighlightingPlayers);
				}
			}
		}, 5, 5);

		specialBlockStorageFile = new File(plugin.getDataFolder(), "specialBlockStorage.yml");
	}

	private static int colorIndexFromString(String string) {
		return Math.abs(string.hashCode()) % colors.length;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(event.getAction() == Action.LEFT_CLICK_BLOCK && currentPlayerChanges.containsKey(event.getPlayer())) {
			String specialBlockType = currentPlayerChanges.remove(event.getPlayer());

			Block specialBlock = event.getClickedBlock();
			Location specialBlockLocation = specialBlock.getLocation();

			if(setSpecialBlock(specialBlockLocation, specialBlockType)) {
				Translator.sendMessage(event.getPlayer(), "command.markSpecialBlock." + (specialBlockType.isEmpty() ? "un" : "") + "marked", specialBlockType);
			} else {
				throw new MarkSpecialBlockFailedException(specialBlockType, specialBlock.getType().name().toLowerCase());
			}

			event.setCancelled(true);
		}
	}

	public void registerSpecialBlockType(String name) {
		registerSpecialBlockType(name, Integer.MAX_VALUE);
	}

	public void registerSpecialBlockType(String name, int maxAmount) {
		registerSpecialBlockType(name, maxAmount, null);
	}

	public void registerSpecialBlockType(String name, int maxAmount, ISpecialBlockMarkListener listener) {
		registeredTypes.put(name.toLowerCase(), maxAmount);

		if(listener != null) {
			registeredListeners.put(name.toLowerCase(), listener);
		}
	}

	public void setStorage(File file) {
		specialBlockStorageFile = file;

		load();
	}

	public void load() {
		specialBlocksByType.clear();
		specialBlocksByLocation.clear();

		if(!specialBlockStorageFile.exists()) {
			return;
		}

		YamlConfiguration specialBlockStorageYaml = YamlConfiguration.loadConfiguration(specialBlockStorageFile);

		for(String specialBlockType : specialBlockStorageYaml.getKeys(false)) {
			List<String> serializedLocations = specialBlockStorageYaml.getStringList(specialBlockType);

			for(String serializedLocation : serializedLocations) {
				try {
					String[] locationSplit = serializedLocation.split(",");

					World world = Bukkit.getWorld(locationSplit[0]);
					int blockX = Integer.parseInt(locationSplit[1]);
					int blockY = Integer.parseInt(locationSplit[2]);
					int blockZ = Integer.parseInt(locationSplit[3]);

					Location location = new Location(world, blockX, blockY, blockZ);

					specialBlocksByType.put(specialBlockType, location);
					specialBlocksByLocation.put(location, specialBlockType);

					if(registeredListeners.containsKey(specialBlockType)) {
						registeredListeners.get(specialBlockType).onMarkSpecialBlock(location, world.getBlockAt(location));
					}
				} catch(Exception ex) {

				}
			}
		}
	}

	public void save() {
		YamlConfiguration specialBlockStorageYaml = new YamlConfiguration();

		for(String specialBlockType : registeredTypes.keySet()) {
			List<Location> locations = specialBlocksByType.get(specialBlockType);
			Set<String> serializedLocations = new HashSet<>(locations.size());

			for(Location location : locations) {
				serializedLocations.add(location.getWorld().getName() + "," + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ());
			}

			specialBlockStorageYaml.set(specialBlockType, new ArrayList<>(serializedLocations));
		}

		try {
			if(specialBlockStorageFile.exists()) {
				specialBlockStorageFile.delete();
			}

			specialBlockStorageYaml.save(specialBlockStorageFile);
		} catch(IOException ex) {
			ex.printStackTrace();
		}
	}

	public boolean setSpecialBlock(Location location, String specialBlockType) {
		if(specialBlockType == null || specialBlockType.isEmpty()) {
			String oldSpecialBlockType = specialBlocksByLocation.remove(location);

			if(oldSpecialBlockType != null) {
				specialBlocksByType.remove(oldSpecialBlockType, location);
			}

			return true;
		}

		specialBlockType = specialBlockType.toLowerCase();

		if(!registeredTypes.containsKey(specialBlockType)) {
			throw new IllegalArgumentException("No special block type with the name " + specialBlockType + " is registered!");
		}

		int maxBlocks = registeredTypes.get(specialBlockType);
		List<Location> registeredSpecialBlocks = specialBlocksByType.get(specialBlockType);

		if(!registeredListeners.containsKey(specialBlockType) || registeredListeners.get(specialBlockType).onMarkSpecialBlock(location, location.getBlock())) {
			if(registeredSpecialBlocks.size() >= maxBlocks) {
				Location firstLocation = registeredSpecialBlocks.get(0);
				specialBlocksByType.remove(specialBlockType, firstLocation);
				specialBlocksByLocation.remove(firstLocation);

				registeredSpecialBlocks = specialBlocksByType.get(specialBlockType);
			}

			specialBlocksByType.put(specialBlockType, location);
			specialBlocksByLocation.put(location, specialBlockType);

			return true;
		}

		return false;
	}

	public boolean isSpecialBlockType(Location location, String specialBlockType) {
		return specialBlockType.toLowerCase().equals(specialBlocksByLocation.get(location));
	}

	public Location getLocation(String specialBlockType) {
		List<Location> locations = specialBlocksByType.get(specialBlockType.toLowerCase());
		return !locations.isEmpty() ? locations.get(0) : null;
	}

	public List<Location> getLocations(String specialBlockType) {
		return specialBlocksByType.get(specialBlockType.toLowerCase());
	}
}