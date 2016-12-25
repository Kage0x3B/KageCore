package de.syscy.kagecore.command.exception;

import de.syscy.kagecore.command.CommandBase;

public class InvalidUsageException extends CommandException {
	private static final long serialVersionUID = 1L;

	public InvalidUsageException(CommandBase<?> command) {
		this(command.getCommandManager().getCommand(), command.getCommand(), command.getUsageString());
	}

	public InvalidUsageException(String mainCommandName, String commandName, String usage) {
		super("command.exception.invalidUsage", mainCommandName, commandName, usage);
	}
}