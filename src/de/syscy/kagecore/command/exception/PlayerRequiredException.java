package de.syscy.kagecore.command.exception;

public class PlayerRequiredException extends CommandException {
	private static final long serialVersionUID = 1L;

	public PlayerRequiredException() {
		super("command.exception.playerRequired");
	}
}