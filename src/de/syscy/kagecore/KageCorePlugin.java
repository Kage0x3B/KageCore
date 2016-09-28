package de.syscy.kagecore;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import de.syscy.bguilib.BGUILib;
import de.syscy.bguilib.creator.BGCreator;
import de.syscy.kagecore.translation.Translator;
import lombok.Getter;
import lombok.Setter;

public class KageCorePlugin extends JavaPlugin {
	private static @Getter KageCorePlugin instance;

	private static @Getter File pluginDirectory;
	private static @Getter FileConfiguration pluginConfig;
	private static @Getter @Setter boolean debug = true;
	private static @Getter @Setter boolean useGUICreator = false;

	public void onEnable() {
		instance = this;

		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

		pluginDirectory = this.getDataFolder();

		Translator.addLanguageFiles(this, new File(pluginDirectory, "lang"));

		this.saveDefaultConfig();

		for(World world : Bukkit.getWorlds()) {
			this.getConfig().addDefault("hotbar." + world.getName(), "");
		}

		pluginConfig = this.getConfig();
		setUseGUICreator(this.getConfig().getBoolean("useGUICreator"));
		BGUILib.init(this);

		if(isUseGUICreator()) {
			BGCreator.init();
		}
	}

	public void onDisable() {
		super.onDisable();

		if(isUseGUICreator()) {
			BGCreator.dispose();
		}

		BGUILib.dispose();
	}

	public static void debugMessage(String message) {
		if(debug) {
			String debugMessage = "[" + ChatColor.RED + "DEBUG" + ChatColor.RESET + "(" + ChatColor.GOLD + Thread.currentThread().getStackTrace()[2].getFileName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + ChatColor.RESET + ")] " + " " + message;

			for(OfflinePlayer operator : Bukkit.getServer().getOperators()) {
				if(operator.isOnline()) {
					operator.getPlayer().sendMessage(debugMessage);
				}
			}

			Bukkit.getLogger().info("[DEBUG (" + Thread.currentThread().getStackTrace()[2].getFileName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + ")] " + " " + message);
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("toggleDebug")) {
			debug = !debug;

			Translator.sendMessage(sender, debug ? "command.enableDebug" : "command.disableDebug");
		} else if(isUseGUICreator()) {
			return BGCreator.onCommand(sender, cmd, label, args);
		} else {
			Translator.sendMessage(sender, "command.guiCreatorDisabled");
		}

		return true;
	}
}