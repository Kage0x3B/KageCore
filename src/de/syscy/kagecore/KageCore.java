package de.syscy.kagecore;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.base.Joiner;

import de.syscy.kagecore.command.CommandManager;
import de.syscy.kagecore.entityregistry.EntityRegistry;
import de.syscy.kagecore.protocol.ProtocolUtil;
import de.syscy.kagecore.translation.Translator;
import de.syscy.kagecore.util.BoundingBox;
import de.syscy.kagecore.util.TestNotification;
import de.syscy.kagecore.util.book.BookUtil;
import de.syscy.kagecore.util.bungee.BungeePluginMessageListener;
import de.syscy.kagecore.util.bungee.KagePluginMessageListener;
import de.syscy.kagegui.KageGUI;
import lombok.Getter;
import lombok.Setter;

public class KageCore extends JavaPlugin {
	private static @Getter KageCore instance;

	private static @Getter File pluginDirectory;
	private static @Getter @Setter boolean debug = true;

	private @Getter KageCoreConfig kageCoreConfig;

	private @Getter CommandManager<KageCore> kcCommandManager;

	@Override
	public void onEnable() {
		instance = this;

		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new BungeePluginMessageListener());

		getServer().getMessenger().registerOutgoingPluginChannel(this, "KageCore");
		getServer().getMessenger().registerIncomingPluginChannel(this, "KageCore", new KagePluginMessageListener());

		pluginDirectory = getDataFolder();

		ConfigurationSerialization.registerClass(BoundingBox.class);

		saveDefaultConfig();

		kageCoreConfig = new KageCoreConfig(getConfig());
		kageCoreConfig.init();

		debug = kageCoreConfig.isDebug();

		kcCommandManager = new CommandManager<KageCore>(this, "kageCore");
		//		kcCommandManager.addCommand(new ExecuteJSCommand(this));
		kcCommandManager.addCommand(new TestNotification(this));
		getCommand("kageCore").setExecutor(kcCommandManager);
		getCommand("kageCore").setTabCompleter(kcCommandManager);

		Translator.addLanguageFiles(this);
		Translator.addLanguageFiles(null, new File(pluginDirectory.getParentFile().getParentFile(), "lang"));

		for(World world : Bukkit.getWorlds()) {
			getConfig().addDefault("hotbar." + world.getName(), "");
		}

		try {
			ProtocolUtil.init(this);
		} catch(Exception ex) {
			KageCore.debugMessage("ProtocolLib not installed. Translations and more disabled!");
		}

		EntityRegistry.init();
		BookUtil.init();

		Bukkit.getScheduler().runTaskLater(this, new Runnable() {
			@Override
			public void run() {
				KageGUI.init(KageCore.this);
			}
		}, 1);
	}

	@Override
	public void onDisable() {
		super.onDisable();

		KageGUI.dispose();
	}

	public static void debugObjects(Object... objects) {
		debugMessage(Joiner.on(", ").join(objects), 3);
	}

	public static void debugMessage(String message) {
		debugMessage(message, 3);
	}

	public static void debugMessage(String message, int stackTraceIndex) {
		if(debug) {
			StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[stackTraceIndex];
			String debugMessage = "[" + ChatColor.RED + "DEBUG" + ChatColor.RESET + " (" + ChatColor.GOLD + stackTraceElement.getFileName() + "." + stackTraceElement.getMethodName() + ":" + stackTraceElement.getLineNumber() + ChatColor.RESET + ")] " + " " + message;

			for(OfflinePlayer operator : Bukkit.getServer().getOperators()) {
				if(operator.isOnline()) {
					operator.getPlayer().sendMessage(debugMessage);
				}
			}

			Bukkit.getLogger().info("[DEBUG (" + stackTraceElement.getFileName() + "." + stackTraceElement.getMethodName() + ":" + stackTraceElement.getLineNumber() + ")] " + " " + message);
		}
	}
}