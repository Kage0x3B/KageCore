package de.syscy.kagecore.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.syscy.kagecore.command.exception.PlayerRequiredException;

public abstract class PlayerCommandBase extends CommandBase {
	public PlayerCommandBase(String command) {
		super(command, command);
	}

	public PlayerCommandBase(String command, String usage, String... aliases) {
		super(command, usage, aliases);
	}

	@Override
	public void onCommand(CommandSender sender, String[] args) {
		if(!(sender instanceof Player)) {
			throw new PlayerRequiredException();
		}

		onPlayerCommand((Player) sender, args);
	}

	public abstract void onPlayerCommand(Player sender, String[] args);
}