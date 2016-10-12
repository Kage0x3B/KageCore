package de.syscy.kagecore;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.packetwrapper.WrapperPlayClientSettings;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import de.syscy.bguilib.BGUILib;
import de.syscy.kagecore.event.LanguageChangeEvent;
import de.syscy.kagecore.translation.Translator;
import de.syscy.kagecore.translation.PacketTranslator;
import de.syscy.kagecore.util.bungee.BungeePluginMessageListener;
import de.syscy.kagecore.util.bungee.KagePluginMessageListener;
import lombok.Getter;
import lombok.Setter;

public class KageCore extends JavaPlugin {
	private static @Getter KageCore instance;

	private static @Getter File pluginDirectory;
	private static @Getter @Setter boolean debug = true;

	private @Getter KageCoreConfig kageCoreConfig;

	public void onEnable() {
		instance = this;

		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new BungeePluginMessageListener());
		
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "KageCore");
		this.getServer().getMessenger().registerIncomingPluginChannel(this, "KageCore", new KagePluginMessageListener());

		pluginDirectory = this.getDataFolder();
		
		saveDefaultConfig();
		
		kageCoreConfig = new KageCoreConfig(getConfig());
		kageCoreConfig.init();

		Translator.addLanguageFiles(this, new File(pluginDirectory, "lang"));
		Translator.addLanguageFiles(null, new File(pluginDirectory.getParentFile().getParentFile(), "lang"));

		for(World world : Bukkit.getWorlds()) {
			this.getConfig().addDefault("hotbar." + world.getName(), "");
		}

		BGUILib.init(this);
		
		initPacketListening();
	}

	public void onDisable() {
		super.onDisable();

		BGUILib.dispose();
	}
	
	private void initPacketListening() {
		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
		
		protocolManager.addPacketListener(new PacketAdapter(this, PacketType.Play.Client.SETTINGS) {
			@Override
			public void onPacketReceiving(final PacketEvent event) {
				Bukkit.getScheduler().runTaskLater(KageCore.this, new Runnable() {
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
}