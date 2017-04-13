package de.syscy.kagecore.command.exception;

public class UnknownTimeUnitException extends CommandException {
	private static final long serialVersionUID = 1L;

	public UnknownTimeUnitException(String timeUnit) {
		super("command.exception.unknownTimeUnit", timeUnit);
	}
}