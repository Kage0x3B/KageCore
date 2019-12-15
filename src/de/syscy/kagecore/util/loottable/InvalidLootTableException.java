package de.syscy.kagecore.util.loottable;

public class InvalidLootTableException extends RuntimeException {
	private static final long serialVersionUID = -5507692689909588277L;

	public InvalidLootTableException(String message) {
		super(message);
	}
}