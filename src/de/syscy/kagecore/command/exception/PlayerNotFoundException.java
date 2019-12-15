package de.syscy.kagecore.command.exception;

public class PlayerNotFoundException extends CommandException {
	private static final long serialVersionUID = 1L;

	public PlayerNotFoundException(String playerName) {
		super("command.exception.playerNotFound", playerName);
	}
}