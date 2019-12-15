package de.syscy.kagecore.util.bungee;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import lombok.AccessLevel;
import lombok.Getter;

public class KagePluginMessageListener implements PluginMessageListener {
	private static @Getter(value = AccessLevel.PROTECTED) ArrayListMultimap<String, KageMessageListener> kageMessageListeners = ArrayListMultimap.create();

	@Override
	public void onPluginMessageReceived(String mainChannel, Player player, byte[] data) {
		ByteArrayDataInput in = ByteStreams.newDataInput(data);

		String subChannel = in.readUTF().toLowerCase();

		for(KageMessageListener kageMessageListener : kageMessageListeners.get(subChannel)) {
			kageMessageListener.handleMessage(subChannel, player, in);
		}
	}
}