package de.syscy.kagegui.yaml.action;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import de.syscy.kagegui.yaml.IYamlGUI;

public class ExecuteCommandAction implements IAction {
	private final String command;
	private final String executor;

	public ExecuteCommandAction(ConfigurationSection actionSection) {
		command = actionSection.getString("command", "");
		executor = actionSection.getString("executor", "server");
	}

	@Override
	public void onTrigger(IYamlGUI<?> gui, Player player, Object... args) {
		CommandSender commandSender = executor.equalsIgnoreCase("player") ? player : Bukkit.getConsoleSender();

		String processedCommand = command.replaceAll("{player}", player.getName());

		for(int i = 0; i < args.length; i++) {
			if(args[i] != null) {
				processedCommand = processedCommand.replaceAll("{arg" + (i + 1) + "}", args[i].toString());
			}
		}

		Bukkit.getServer().dispatchCommand(commandSender, processedCommand);
	}
}