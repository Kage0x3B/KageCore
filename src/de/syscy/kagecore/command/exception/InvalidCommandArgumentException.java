package de.syscy.kagecore.command.exception;

public class InvalidCommandArgumentException extends CommandException {
	private static final long serialVersionUID = 1L;

	public InvalidCommandArgumentException(String argName) {
		super("command.exception.invalidCommandArgument", argName);
	}
}