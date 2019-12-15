package de.syscy.kagecore.command.exception;

public class InvalidSpecialBlockTypeException extends CommandException {
	private static final long serialVersionUID = 1L;

	public InvalidSpecialBlockTypeException(String typeName) {
		super("command.specialBlock.invalidType", typeName);
	}
}