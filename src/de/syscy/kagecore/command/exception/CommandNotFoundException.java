package de.syscy.kagecore.command.exception;

public class CommandNotFoundException extends CommandException {
	private static final long serialVersionUID = 1L;

	public CommandNotFoundException(String commandName) {
		super("command.exception.notFound", commandName);
	}
}