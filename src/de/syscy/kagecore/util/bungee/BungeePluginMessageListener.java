package de.syscy.kagecore.util.bungee;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import lombok.AccessLevel;
import lombok.Getter;

public class BungeePluginMessageListener implements PluginMessageListener {
	private static @Getter(value=AccessLevel.PROTECTED) ArrayListMultimap<String, BungeeMessageListener> bungeeMessageListeners = ArrayListMultimap.create();
	
	@Override
	public void onPluginMessageReceived(String mainChannel, Player player, byte[] data) {
		ByteArrayDataInput in = ByteStreams.newDataInput(data);

		String bungeeCordChannel = in.readUTF();
		
		if(bungeeCordChannel.equals("Forward") || bungeeCordChannel.equals("ForwardToPlayer")) {
			String bungeeCordSubChannel = in.readUTF().toLowerCase();
			
			for(BungeeMessageListener bungeeMessageListener : bungeeMessageListeners.get(bungeeCordSubChannel)) {
				bungeeMessageListener.handleMessage(bungeeCordChannel, bungeeCordSubChannel, player, in);
			}
			
			return;
		}

		List<ResponseListener<?>> allListeners = BungeeUtil.getResponseListeners().get(bungeeCordChannel);

		ResponseListener<?> finalResponseListener = null;

		for(ResponseListener<?> responseListener : allListeners) {
			if(!responseListener.isPlayerSpecific() || responseListener.getPlayer().equals(player)) {
				finalResponseListener = responseListener;

				break;
			}
		}

		if(finalResponseListener != null) {
			finalResponseListener.getResponseHandler().handleResponse(in);
			BungeeUtil.getResponseListeners().remove(bungeeCordChannel, finalResponseListener);
		}
	}
}