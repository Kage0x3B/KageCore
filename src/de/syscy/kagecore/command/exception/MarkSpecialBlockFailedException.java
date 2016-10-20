package de.syscy.kagecore.command.exception;

public class MarkSpecialBlockFailedException extends CommandException {
	private static final long serialVersionUID = 1L;

	public MarkSpecialBlockFailedException(String typeName, String actualTypeName) {
		super("command.specialBlock.failed", typeName, actualTypeName);
	}
}