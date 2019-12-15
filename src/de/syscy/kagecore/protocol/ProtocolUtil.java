package de.syscy.kagecore.protocol;

import org.bukkit.Bukkit;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import de.syscy.kagecore.KageCore;
import de.syscy.kagecore.translation.PacketTranslator;
import lombok.Getter;

public class ProtocolUtil {
	private static @Getter ProtocolManager protocolManager;

	public static void init(KageCore plugin) {
		if(Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
			protocolManager = ProtocolLibrary.getProtocolManager();

			PacketTranslator.initPacketRewriting(plugin);
		}
	}
}