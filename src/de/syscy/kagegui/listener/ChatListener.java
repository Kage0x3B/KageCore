package de.syscy.kagegui.listener;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import de.syscy.kagegui.callbacks.ChatInputCallback;

public class ChatListener implements Listener {
	private static Map<Player, ChatInputCallback> callbacks = new HashMap<>();

	public static void addPlayerChatInputCallback(Player player, String question, ChatInputCallback callback) {
		player.sendMessage(question);
		callbacks.put(player, callback);
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		if(callbacks.containsKey(event.getPlayer())) {
			event.setCancelled(true);

			((ChatInputCallback) callbacks.get(event.getPlayer())).onChatInput(event.getPlayer(), event.getMessage());
			callbacks.remove(event.getPlayer());
		}
	}
}