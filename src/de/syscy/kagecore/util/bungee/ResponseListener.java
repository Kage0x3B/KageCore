package de.syscy.kagecore.util.bungee;

import org.bukkit.entity.Player;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public abstract class ResponseListener<T> {
	private @Getter(value = AccessLevel.PROTECTED) @Setter(value = AccessLevel.PROTECTED) Player player;
	private @Getter(value = AccessLevel.PROTECTED) @Setter(value = AccessLevel.PROTECTED) boolean playerSpecific = false;
	private @Getter(value = AccessLevel.PROTECTED) @Setter(value = AccessLevel.PROTECTED) ResponseHandler responseHandler;

	public abstract boolean onResponse(T response);
}