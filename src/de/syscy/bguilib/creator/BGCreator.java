package de.syscy.bguilib.creator;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.syscy.bguilib.BGGUI;
import de.syscy.bguilib.BGHotbarGUI;
import de.syscy.bguilib.BGUILib;
import de.syscy.bguilib.YesNoGUI;
import de.syscy.bguilib.callbacks.YesNoCallback;
import de.syscy.bguilib.creator.gui.EditGUI;
import de.syscy.bguilib.creator.guidata.GUIData;
import de.syscy.bguilib.creator.hgui.EditHotbarGUI;
import de.syscy.bguilib.creator.hotbarguidata.HotbarGUIData;
import de.syscy.kagecore.KageCorePlugin;
import de.syscy.kagecore.translation.Translator;
import lombok.Getter;

public class BGCreator {
	private static @Getter Map<String, GUIData> guis = new HashMap<>();

	private static @Getter Map<String, HotbarGUIData> hotbarGuis = new HashMap<>();

	public static void init() {
		File guiDirectory = new File(KageCorePlugin.getPluginDirectory(), "gui");

		File[] guiFiles = guiDirectory.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".gd");
			}
		});

		if(guiFiles != null) {
			for(int i = 0; i < guiFiles.length; i++) {
				File guiFile = guiFiles[i];
				String name = guiFile.getName();
				name = name.replaceAll(".gd", "");

				guis.put(name.toLowerCase(), new GUIData(name.toLowerCase()));
			}
		}

		File[] hotbarGuiFiles = guiDirectory.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".hgd");
			}
		});

		if(hotbarGuiFiles != null) {
			for(int i = 0; i < hotbarGuiFiles.length; i++) {
				File hotbarGuiFile = hotbarGuiFiles[i];
				String name = hotbarGuiFile.getName();
				name = name.replaceAll(".hgd", "");

				hotbarGuis.put(name.toLowerCase(), new HotbarGUIData(name.toLowerCase()));
			}
		}
	}

	public static boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if((label.equalsIgnoreCase("createGUI")) && (sender.hasPermission("bg.createGUI"))) {
			if(args.length < 1) {
				return false;
			}

			String name = args[0].toLowerCase();

			guis.put(name, new GUIData(name));

			Translator.sendMessage(sender, "bggui.createdGUI", name);

			return true;
		}
		if((label.equalsIgnoreCase("editGUI")) && ((sender instanceof Player)) && (sender.hasPermission("bg.editGUI"))) {
			if(args.length < 1) {
				return false;
			}

			String name = args[0].toLowerCase();

			GUIData guiData = (GUIData) guis.get(name);

			Player player = (Player) sender;

			if(guiData != null) {
				BGUILib.showGUI(new EditGUI(guiData), player);
			} else {
				Translator.sendMessage(sender, "bggui.notExistingGUI", name);
			}

			return true;
		}
		if((label.equalsIgnoreCase("removeGUI")) && (sender.hasPermission("bg.removeGUI"))) {
			if(args.length < 1) {
				return false;
			}

			final String name = args[0].toLowerCase();

			if(guis.containsKey(name)) {
				final GUIData gui = (GUIData) guis.get(name);

				if((sender instanceof Player)) {
					final Player player = (Player) sender;

					YesNoGUI prompt = new YesNoGUI(new YesNoCallback() {
						public void onResult(boolean result) {
							if(result) {
								gui.delete();
								BGCreator.guis.remove(name);
								player.sendMessage("Deleted the gui!");
							}
						}
					}, "Do you really want to delete the \"" + name + "\" gui?", "Delete gui");

					BGUILib.showGUI(prompt, player);
				} else {
					gui.delete();
					guis.remove(name);
					sender.sendMessage("Deleted the gui!");
				}
			} else {
				Translator.sendMessage(sender, "bggui.notExistingGUI", name);

				return false;
			}

			return true;
		}
		if((label.equalsIgnoreCase("showGUI")) && (sender.hasPermission("bg.showGUI"))) {
			if(args.length < 1) {
				return false;
			}

			String name = args[0].toLowerCase();

			if(guis.containsKey(name)) {
				BGGUI gui = ((GUIData) guis.get(name)).toGUI();

				if((args.length == 1) && ((sender instanceof Player))) {
					BGUILib.showGUI(gui, (Player) sender);
				} else if(args.length > 1) {
					String playerName = args[1];

					Player player = Bukkit.getPlayer(playerName);
					if(player != null) {
						BGUILib.showGUI(gui, player);
					}
				} else {
					return false;
				}
			} else {
				Translator.sendMessage(sender, "bggui.notExistingGUI", name);
				return false;
			}
			return true;
		}
		if((label.equalsIgnoreCase("createHGUI")) && (sender.hasPermission("bg.createGUI"))) {
			if(args.length < 1) {
				return false;
			}

			String name = args[0].toLowerCase();

			hotbarGuis.put(name, new HotbarGUIData(name));

			sender.sendMessage("Created hotbar gui \"" + name + "\"!");

			return true;
		}
		if((label.equalsIgnoreCase("editHGUI")) && ((sender instanceof Player)) && (sender.hasPermission("bg.editGUI"))) {
			if(args.length < 1) {
				return false;
			}

			String name = args[0].toLowerCase();

			HotbarGUIData guiData = (HotbarGUIData) hotbarGuis.get(name);

			Player player = (Player) sender;
			if(guiData != null) {
				BGUILib.showGUI(new EditHotbarGUI(guiData), player);
			} else {
				Translator.sendMessage(sender, "bggui.notExistingHotbarGUI", name);
			}
			return true;
		}
		if((label.equalsIgnoreCase("removeHGUI")) && (sender.hasPermission("bg.removeGUI"))) {
			if(args.length < 1) {
				return false;
			}

			final String name = args[0].toLowerCase();

			if(hotbarGuis.containsKey(name)) {
				final HotbarGUIData gui = (HotbarGUIData) hotbarGuis.get(name);

				if((sender instanceof Player)) {
					final Player player = (Player) sender;

					YesNoGUI prompt = new YesNoGUI(new YesNoCallback() {
						public void onResult(boolean result) {
							if(result) {
								gui.delete();
								BGCreator.hotbarGuis.remove(name);
								Translator.sendMessage(player, "bggui.deletedHGUI", name);
							}
						}
					}, "Do you really want to delete the \"" + name + "\" hotbar gui?", "Delete hotbar gui");

					BGUILib.showGUI(prompt, player);
				} else {
					gui.delete();
					hotbarGuis.remove(name);
					Translator.sendMessage(sender, "bggui.deletedHGUI", name);
				}
			} else {
				Translator.sendMessage(sender, "bggui.notExistingHotbarGUI", name);

				return false;
			}

			return true;
		}
		if((label.equalsIgnoreCase("showHGUI")) && (sender.hasPermission("bg.showGUI"))) {
			if(args.length < 1) {
				return false;
			}

			String name = args[0].toLowerCase();

			if(hotbarGuis.containsKey(name)) {
				BGHotbarGUI gui = ((HotbarGUIData) hotbarGuis.get(name)).toHGUI();

				if((args.length == 1) && ((sender instanceof Player))) {
					BGUILib.setHotbarGUI(gui, (Player) sender);
				} else if(args.length > 1) {
					String playerName = args[1];

					Player player = Bukkit.getPlayer(playerName);

					if(player != null) {
						BGUILib.setHotbarGUI(gui, player);
					}
				} else {
					return false;
				}
			} else {
				Translator.sendMessage(sender, "bggui.notExistingHotbarGUI", name);

				return false;
			}

			return true;
		}
		if((label.equalsIgnoreCase("hideHGUI")) && (sender.hasPermission("bg.showGUI"))) {
			if(args.length == 1) {
				Player player = Bukkit.getPlayer(args[0]);

				if(player != null) {
					BGUILib.removeHotbarGUI(player);
				}
			} else if((sender instanceof Player)) {
				BGUILib.removeHotbarGUI((Player) sender);
			}

			return true;
		}

		if((label.equalsIgnoreCase("bindHotbarToWorld")) && (sender.hasPermission("bg.bindHotbar"))) {
			if(args.length < 2) {
				return false;
			}

			String hotbarName = args[0];
			String worldName = args[1];

			if(!hotbarGuis.containsKey(hotbarName)) {
				Translator.sendMessage(sender, "bggui.notExistingHotbarGUI", hotbarName);
				return false;
			}

			if(Bukkit.getWorld(worldName) == null) {
				Translator.sendMessage(sender, "bggui.notExistingWorld", worldName);
				return false;
			}

			KageCorePlugin.getPluginConfig().set("hotbar." + worldName, hotbarName);

			Translator.sendMessage(sender, "bggui.boundHGUI", hotbarName, worldName);

			return true;
		}
		if((label.equalsIgnoreCase("listGUIS")) && (sender.hasPermission("bg.listguis"))) {
			Translator.sendMessage(sender, "bggui.allGuis");

			for(String gui : getGuis().keySet()) {
				sender.sendMessage("  - " + gui);
			}

			Translator.sendMessage(sender, "bggui.allHotbarGuis");

			for(String hotbarGUI : getHotbarGuis().keySet()) {
				sender.sendMessage("  - " + hotbarGUI);
			}

			return true;
		}

		return false;
	}

	public static void dispose() {
		for(GUIData guiData : guis.values()) {
			guiData.save();
		}

		for(HotbarGUIData guiData : hotbarGuis.values()) {
			guiData.save();
		}
	}
}