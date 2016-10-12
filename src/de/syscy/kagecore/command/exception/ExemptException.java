package de.syscy.kagecore.command.exception;

public class ExemptException extends CommandException {
	private static final long serialVersionUID = 1L;

	public ExemptException(String playerName) {
		super("command.exception.exempt", playerName);
	}
}