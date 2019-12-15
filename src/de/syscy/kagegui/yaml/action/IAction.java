package de.syscy.kagegui.yaml.action;

import org.bukkit.entity.Player;

import de.syscy.kagegui.yaml.IYamlGUI;

public interface IAction {
	public void onTrigger(IYamlGUI<?> gui, Player player, Object... args);
}