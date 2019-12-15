package de.syscy.kagecore.util;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ParseException extends RuntimeException {
	private static final long serialVersionUID = -6832574887607572216L;

	public ParseException(String message) {
		super(message);
	}

	public ParseException(String message, Throwable throwable) {
		super(message, throwable);
	}
}