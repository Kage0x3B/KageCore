package de.syscy.kagecore.util.specialblock;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.google.common.collect.ArrayListMultimap;

import de.syscy.kagecore.KageCore;
import de.syscy.kagecore.command.CommandManager;
import de.syscy.kagecore.command.PlayerCommandBase;
import de.syscy.kagecore.command.exception.InvalidSpecialBlockTypeException;
import de.syscy.kagecore.command.exception.InvalidUsageException;
import de.syscy.kagecore.command.exception.MarkSpecialBlockFailedException;
import de.syscy.kagecore.translation.Translator;

public class SpecialBlockManager extends CommandManager<KageCore> implements Listener {
	private Map<String, Integer> registeredTypes = new HashMap<>(15);
	private Map<String, ISpecialBlockMarkListener> registeredListeners = new HashMap<>(15);
	private ArrayListMultimap<String, Location> specialBlocksByType = ArrayListMultimap.create(15, 2);
	private Map<Location, String> specialBlocksByLocation = new HashMap<>(30);

	private Map<Player, String> currentPlayerChanges = new HashMap<>(2);
	//	private List<Player> currentHighlightingPlayers = new ArrayList<>(5);

	public SpecialBlockManager(KageCore plugin) {
		super(plugin, "specialBlock", "sB");

		addCommand(new PlayerCommandBase<KageCore>(plugin, "mark", "[type]", "m") {
			@Override
			public void onPlayerCommand(Player sender, String[] args) {
				if(args.length < 1) {
					throw new InvalidUsageException(this);
				}

				String specialBlockType = args[0].toLowerCase();

				if(!registeredTypes.containsKey(specialBlockType)) {
					throw new InvalidSpecialBlockTypeException(args[0]);
				}

				currentPlayerChanges.put(sender, specialBlockType);
				Translator.sendMessage(sender, "command.specialBlock.leftClick", specialBlockType);
			}

			@Override
			public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
				return getPossibleSuggestions(args, registeredTypes.keySet());
			}
		});

		addCommand(new PlayerCommandBase<KageCore>(plugin, "unmark", "", "um") {
			@Override
			public void onPlayerCommand(Player sender, String[] args) {
				currentPlayerChanges.put(sender, "");
				Translator.sendMessage(sender, "command.specialBlock.unmarkLeftClick");
			}
		});

		//		addCommand(new PlayerCommandBase<KageCore>(plugin, "hightlight", "", "um") {
		//			@Override
		//			public void onPlayerCommand(Player sender, String[] args) {
		//				if(currentHighlightingPlayers.remove(sender)) {
		//					Translator.sendMessage(sender, "command.specialBlock.stopHighlighting");
		//				} else {
		//					Translator.sendMessage(sender, "command.specialBlock.startHighlighting");
		//				}
		//				currentPlayerChanges.put(sender, "");
		//			}
		//		});
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

	public void load() {
		File specialBlockStorageFile = new File(plugin.getDataFolder(), "specialBlockStorage.yml");

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
			File specialBlockStorageFile = new File(plugin.getDataFolder(), "specialBlockStorage.yml");

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