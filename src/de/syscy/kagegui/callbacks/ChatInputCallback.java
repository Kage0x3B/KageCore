package de.syscy.kagegui.callbacks;

import org.bukkit.entity.Player;

public interface ChatInputCallback {
	public void onChatInput(Player player, String message);
}