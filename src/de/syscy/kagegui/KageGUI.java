package de.syscy.kagegui;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import de.syscy.kagecore.KageCore;
import de.syscy.kagecore.KageCoreConfig;
import de.syscy.kagegui.crafting.IDefaultCraftingGUIProvider;
import de.syscy.kagegui.crafting.KCraftingGUI;
import de.syscy.kagegui.inventory.KGUI;
import de.syscy.kagegui.listener.ChatListener;
import de.syscy.kagegui.listener.CraftingGUIListener;
import de.syscy.kagegui.listener.GUIListener;
import de.syscy.kagegui.yaml.crafting.YamlCraftingGUI;
import de.syscy.kagegui.yaml.inventory.KComponentFactory;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class KageGUI {
	private static @Getter JavaPlugin plugin;
	private static @Getter File guiDirectory;

	private static int timer = 0;

	private static @Getter Map<Player, KGUI> currentGuis = new WeakHashMap<>();
	private static @Getter Map<Player, KCraftingGUI> currentCraftingGuis = new WeakHashMap<>();

	private static @Getter @Setter IDefaultCraftingGUIProvider defaultCraftingGUIProvider;

	private static @Getter @Setter int craftingGuiInteractBlock = 0;

	public static void init(JavaPlugin plugin) {
		KageGUI.plugin = plugin;

		guiDirectory = new File(KageCore.getPluginDirectory(), "gui");

		if(!guiDirectory.exists()) {
			guiDirectory.mkdirs();
		}

		KComponentFactory.init();

		plugin.getServer().getPluginManager().registerEvents(new GUIListener(), plugin);
		plugin.getServer().getPluginManager().registerEvents(new ChatListener(), plugin);
		plugin.getServer().getPluginManager().registerEvents(new CraftingGUIListener(), plugin);
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				update();
				render();
			}
		}, 5L, 5L);

		KageCoreConfig kageCoreConfig = KageCore.getInstance().getKageCoreConfig();
		final String defaultCraftingGUI = kageCoreConfig.getDefaultCraftingGUI();

		if(defaultCraftingGUI != null && !defaultCraftingGUI.isEmpty() && !defaultCraftingGUI.equalsIgnoreCase("none")) {
			defaultCraftingGUIProvider = new IDefaultCraftingGUIProvider() {
				@Override
				public KCraftingGUI createInstance(Player player) {
					return new YamlCraftingGUI(defaultCraftingGUI);
				}
			};
		}

		if(defaultCraftingGUIProvider != null) {
			for(Player player : Bukkit.getOnlinePlayers()) {
				setCraftingGUI(defaultCraftingGUIProvider.createInstance(player), player);
			}
		}
	}

	public static void showGUI(KGUI gui, Player player) {
		if(currentGuis.containsKey(player)) {
			currentGuis.get(player).close();
		}

		currentGuis.put(player, gui);
		gui.show(player);
	}

	public static void setCraftingGUI(KCraftingGUI craftingGUI, Player player) {
		Inventory playerInventory = player.getOpenInventory().getTopInventory();

		for(int i = 1; i < 5; i++) {
			playerInventory.setItem(i, null);
		}

		currentCraftingGuis.put(player, craftingGUI);
		craftingGUI.show(player);
	}

	public static void removeCraftingGUI(Player player) {
		if(currentCraftingGuis.remove(player) != null) {
			Inventory playerInventory = player.getOpenInventory().getTopInventory();

			for(int i = 1; i < 5; i++) {
				playerInventory.setItem(i, null);
			}
		}
	}

	private static void update() {
		for(KGUI gui : currentGuis.values()) {
			if(timer % gui.getUpdateTick() == 0) {
				gui.update();
			}
		}

		for(KCraftingGUI gui : currentCraftingGuis.values()) {
			gui.update();
		}

		craftingGuiInteractBlock--;
	}

	private static void render() {
		for(KGUI gui : currentGuis.values()) {
			if(timer % gui.getUpdateTick() == 0) {
				gui.render();
			}
		}

		for(KCraftingGUI gui : currentCraftingGuis.values()) {
			if(timer % 3 == 0 && gui.getPlayer().getOpenInventory().getTopInventory() instanceof CraftingInventory) {
				gui.render();
			}
		}

		timer++;
	}

	public static void dispose() {
		for(Player player : Collections.synchronizedCollection(currentCraftingGuis.keySet())) {
			removeCraftingGUI(player);
		}
	}
}