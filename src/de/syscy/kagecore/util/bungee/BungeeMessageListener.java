package de.syscy.kagecore.util.bungee;

import org.bukkit.entity.Player;

import com.google.common.io.ByteArrayDataInput;

public interface BungeeMessageListener {
	/**
	 * 
	 * @param bungeeCordChannel Forward or ForwardToPlayer
	 * @param bungeeCordSubChannel The custom subchannel used (in lowercase)
	 * @param player The player the message was sent through
	 * @param in The data input
	 */
	public void handleMessage(String bungeeCordChannel, String bungeeCordSubChannel, Player player, ByteArrayDataInput in);
}