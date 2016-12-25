package de.syscy.kagecore;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.packetwrapper.WrapperPlayClientSettings;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.google.common.base.Joiner;

import de.syscy.kagecore.command.CommandManager;
import de.syscy.kagecore.event.LanguageChangeEvent;
import de.syscy.kagecore.translation.PacketTranslator;
import de.syscy.kagecore.translation.Translator;
import de.syscy.kagecore.util.BoundingBox;
import de.syscy.kagecore.util.ExecuteJSCommand;
import de.syscy.kagecore.util.book.BookUtil;
import de.syscy.kagecore.util.bungee.BungeePluginMessageListener;
import de.syscy.kagecore.util.bungee.KagePluginMessageListener;
import de.syscy.kagecore.util.specialblock.SpecialBlockManager;
import de.syscy.kagecore.versioncompat.VersionCompatClassLoader;
import de.syscy.kagegui.KageGUI;
import lombok.Getter;
import lombok.Setter;

public class KageCore extends JavaPlugin {
	private static @Getter KageCore instance;

	private static @Getter File pluginDirectory;
	private static @Getter @Setter boolean debug = true;

	private @Getter KageCoreConfig kageCoreConfig;

	private static @Getter ProtocolManager protocolManager;

	private static @Getter SpecialBlockManager specialBlockManager;
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

		VersionCompatClassLoader.init();

		kageCoreConfig = new KageCoreConfig(getConfig());
		kageCoreConfig.init();

		debug = kageCoreConfig.isDebug();

		specialBlockManager = new SpecialBlockManager(this);

		kcCommandManager = new CommandManager<KageCore>(this, "kageCore");
		kcCommandManager.addCommand(specialBlockManager);
		kcCommandManager.addCommand(new ExecuteJSCommand(this));
		getCommand("kageCore").setExecutor(kcCommandManager);
		getCommand("kageCore").setTabCompleter(kcCommandManager);

		getServer().getPluginManager().registerEvents(specialBlockManager, this);
		Bukkit.getScheduler().runTaskLater(this, new Runnable() {
			@Override
			public void run() {
				specialBlockManager.load();
			}
		}, 1);

		Translator.addLanguageFiles(this, new File(pluginDirectory, "lang"));
		Translator.addLanguageFiles(null, new File(pluginDirectory.getParentFile().getParentFile(), "lang"));

		for(World world : Bukkit.getWorlds()) {
			getConfig().addDefault("hotbar." + world.getName(), "");
		}

		initPacketListening();

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

		specialBlockManager.save();

		KageGUI.dispose();
	}

	private void initPacketListening() {
		protocolManager = ProtocolLibrary.getProtocolManager();

		protocolManager.addPacketListener(new PacketAdapter(this, PacketType.Play.Client.SETTINGS) {
			@Override
			public void onPacketReceiving(final PacketEvent event) {
				Bukkit.getScheduler().runTaskLater(KageCore.this, new Runnable() {
					@Override
					public void run() {
						WrapperPlayClientSettings wrapper = new WrapperPlayClientSettings(event.getPacket());

						String language = wrapper.getLocale().substring(0, 2);
						String lastLanguage = Translator.getPlayerLanguages().get(event.getPlayer());

						if(lastLanguage == null || lastLanguage.isEmpty()) {
							lastLanguage = Translator.getDefaultLocale();
						}

						Translator.getPlayerLanguages().put(event.getPlayer(), language);

						Bukkit.getPluginManager().callEvent(new LanguageChangeEvent(event.getPlayer(), language, lastLanguage));
					}
				}, 1);
			}
		});

		PacketTranslator packetTranslator = new PacketTranslator(this, protocolManager);
		packetTranslator.initPacketRewriting();
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