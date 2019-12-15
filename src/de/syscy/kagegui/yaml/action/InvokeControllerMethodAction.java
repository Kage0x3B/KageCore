package de.syscy.kagegui.yaml.action;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import de.syscy.kagegui.yaml.IYamlGUI;

public class InvokeControllerMethodAction implements IAction {
	private final String methodName;

	public InvokeControllerMethodAction(ConfigurationSection actionSection) {
		methodName = actionSection.getString("methodName", "");
	}

	@Override
	public void onTrigger(IYamlGUI<?> gui, Player player, Object... args) {
		gui.getControllerClass().call(methodName, gui, player, args);
	}
}