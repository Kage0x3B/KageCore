package de.syscy.bguilib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import de.syscy.bguilib.listener.ChatListener;
import de.syscy.bguilib.listener.GUIListener;
import de.syscy.bguilib.listener.HotbarGUIListener;
import lombok.Getter;

public class BGUILib {
	private static @Getter JavaPlugin plugin;
	private static @Getter Map<Player, BGGUI> currentGuis = new HashMap<>();
	private static @Getter Map<Player, BGHotbarGUI> currentHotbarGuis = new HashMap<>();

	public static void init(JavaPlugin plugin) {
		BGUILib.plugin = plugin;

		plugin.getServer().getPluginManager().registerEvents(new GUIListener(), plugin);
		plugin.getServer().getPluginManager().registerEvents(new ChatListener(), plugin);
		plugin.getServer().getPluginManager().registerEvents(new HotbarGUIListener(), plugin);
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {
				BGUILib.update();
				BGUILib.render();
			}
		}, 10L, 10L);
	}

	public static void showGUI(BGGUI gui, Player player) {
		if(currentGuis.containsKey(player)) {
			((BGGUI) currentGuis.get(player)).close();
		}

		currentGuis.put(player, gui);
		gui.show(player);
	}

	public static void setHotbarGUI(BGHotbarGUI hotbarGUI, Player player) {
		if(currentHotbarGuis.containsKey(player)) {
			((BGHotbarGUI) currentHotbarGuis.get(player)).close();
		}

		player.getInventory().clear();
		currentHotbarGuis.put(player, hotbarGUI);
		hotbarGUI.show(player);
	}

	public static void removeHotbarGUI(Player player) {
		if(player != null) {
			player.getInventory().clear();
		}

		currentHotbarGuis.remove(player);
	}

	private static void update() {
		for(BGGUI gui : currentGuis.values()) {
			gui.update();
		}

		for(BGHotbarGUI gui : currentHotbarGuis.values()) {
			gui.update();
		}

	}

	private static void render() {
		List<Player> inventoryUpdateNeeded = new ArrayList<Player>();

		for(BGGUI gui : currentGuis.values()) {
			if(gui.render()) {
				inventoryUpdateNeeded.add(gui.getPlayer());
			}
		}

		for(BGHotbarGUI gui : currentHotbarGuis.values()) {
			if(gui.render() && !inventoryUpdateNeeded.contains(gui.getPlayer())) {
				inventoryUpdateNeeded.add(gui.getPlayer());
			}
		}

		for(Player player : inventoryUpdateNeeded) {
			player.updateInventory();
		}
	}

	public static void dispose() {
		for(Player player : currentHotbarGuis.keySet()) {
			removeHotbarGUI(player);
		}
	}
}