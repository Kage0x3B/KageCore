package de.syscy.kagecore.command.exception;

public class AccessDeniedException extends CommandException {
	private static final long serialVersionUID = 1L;

	public AccessDeniedException() {
		super("command.exception.denyAccess");
	}
}