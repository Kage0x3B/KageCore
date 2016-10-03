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
import de.syscy.kagecore.util.TranslatorUtil;
import de.syscy.kagecore.util.bungee.BungeePluginMessageListener;
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

		pluginDirectory = this.getDataFolder();
		
		saveDefaultConfig();
		
		kageCoreConfig = new KageCoreConfig(getConfig());
		kageCoreConfig.init();

		Translator.addLanguageFiles(this, new File(pluginDirectory, "lang"));

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
			public void onPacketReceiving(PacketEvent event) {
				WrapperPlayClientSettings wrapper = new WrapperPlayClientSettings(event.getPacket());
				
				Translator.getPlayerLanguages().put(event.getPlayer(), wrapper.getLocale());
				
				Bukkit.getPluginManager().callEvent(new LanguageChangeEvent(event.getPlayer(), wrapper.getLocale()));
			}
		});
		
		TranslatorUtil.initPacketRewriting(this, protocolManager);
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