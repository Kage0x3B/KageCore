package de.syscy.kagecore.util.bungee;

import com.google.common.io.ByteArrayDataInput;

public interface ResponseHandler {
	public void handleResponse(ByteArrayDataInput in);
}