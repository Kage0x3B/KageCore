package de.syscy.kagecore.factory;

public class InvalidTemplateException extends RuntimeException {
	private static final long serialVersionUID = -5294096244434784554L;
	
	public InvalidTemplateException(String message) {
		super(message);
	}
}