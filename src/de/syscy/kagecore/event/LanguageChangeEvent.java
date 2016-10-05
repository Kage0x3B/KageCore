package de.syscy.kagecore.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LanguageChangeEvent extends org.bukkit.event.Event {
	private static final HandlerList handlerList = new HandlerList();
	
	private final @Getter Player player;
	
	private final @Getter String language;
	private final @Getter String lastLanguage;

	@Override
	public HandlerList getHandlers() {
		return handlerList;
	}

	public static HandlerList getHandlerList() {
		return handlerList;
	}
}