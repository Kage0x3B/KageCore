package de.syscy.kagecore.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import de.syscy.kagecore.KageCorePlugin;

public class BungeeUtil {
	public static void sendPlayerToServer(Player player, String serverName) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF(serverName);

		player.sendPluginMessage(KageCorePlugin.getInstance(), "BungeeCord", out.toByteArray());
	}

	public static void sendPlayerToServer(String playerName, String serverName) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("ConnectOther");
		out.writeUTF(playerName);
		out.writeUTF(serverName);

		Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);

		if(player != null) {
			player.sendPluginMessage(KageCorePlugin.getInstance(), "BungeeCord", out.toByteArray());
		}
	}
}