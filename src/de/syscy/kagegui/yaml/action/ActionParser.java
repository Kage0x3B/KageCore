package de.syscy.kagegui.yaml.action;

import org.bukkit.configuration.ConfigurationSection;

public class ActionParser {
	public static IAction parseAction(ConfigurationSection actionSection) {
		String type = actionSection.getString("type", "");

		switch(type.toLowerCase()) {
			case "invoke":
			case "invokecontrollermethod":
				return new InvokeControllerMethodAction(actionSection);
			case "opengui":
				return new OpenGUIAction(actionSection);
			case "command":
			case "executecommand":
				return new ExecuteCommandAction(actionSection);
			default:
				return null;
		}
	}
}