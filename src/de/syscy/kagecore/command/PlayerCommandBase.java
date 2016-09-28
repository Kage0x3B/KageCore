package de.syscy.kagecore.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.syscy.kagecore.translation.Translator;

public abstract class PlayerCommandBase extends CommandBase {
	public PlayerCommandBase(String command) {
		super(command, "", command);
	}

	public PlayerCommandBase(String command, String description) {
		super(command, description, command);
	}

	public PlayerCommandBase(String command, String description, String usage, String... aliases) {
		super(command, description, usage, aliases);
	}

	public void onCommand(CommandSender sender, String[] args) {
		if(!(sender instanceof Player)) {
			Translator.sendMessage(sender, "command.playerRequired");

			return;
		}

		onPlayerCommand((Player) sender, args);
	}

	public abstract void onPlayerCommand(Player sender, String[] args);
}