package de.syscy.kagecore.event;

import de.syscy.kagecore.factory.entity.IEntityTemplate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@RequiredArgsConstructor
public class SpawnTemplateEntityEvent extends Event {
	private static final HandlerList handlerList = new HandlerList();

	private final @Getter Entity entity;
	private final @Getter Location location;

	private final @Getter IEntityTemplate template;
	private final @Getter String templateName;

	@Override
	public HandlerList getHandlers() {
		return handlerList;
	}

	public static HandlerList getHandlerList() {
		return handlerList;
	}
}