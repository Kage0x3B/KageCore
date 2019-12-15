package de.syscy.kagecore.command.exception;

import lombok.Getter;

public class CommandException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private @Getter Object[] args;

	public CommandException(String message, Object... args) {
		super(message);

		this.args = args;
	}
}