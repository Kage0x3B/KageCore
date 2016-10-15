package de.syscy.kagecore.command.exception;

public class EntityNotFoundException extends CommandException {
	private static final long serialVersionUID = 1L;

	public EntityNotFoundException() {
		super("command.exception.entityNotFound");
	}
}