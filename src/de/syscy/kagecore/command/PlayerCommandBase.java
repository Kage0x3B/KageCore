package de.syscy.kagecore.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import de.syscy.kagecore.command.argument.CommandArgument;
import de.syscy.kagecore.command.exception.PlayerRequiredException;

public abstract class PlayerCommandBase<P extends JavaPlugin> extends CommandBase<P> {
	public PlayerCommandBase(P plugin, String command, CommandArgument<?>... commandArguments) {
		super(plugin, command, commandArguments);
	}

	@Override
	public void onCommand(CommandSender sender) {
		if(!(sender instanceof Player)) {
			throw new PlayerRequiredException();
		}

		onPlayerCommand((Player) sender);
	}

	public abstract void onPlayerCommand(Player sender);
}