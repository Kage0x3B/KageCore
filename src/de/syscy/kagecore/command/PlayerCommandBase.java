package de.syscy.kagecore.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import de.syscy.kagecore.command.exception.PlayerRequiredException;

public abstract class PlayerCommandBase<P extends JavaPlugin> extends CommandBase<P> {
	public PlayerCommandBase(P plugin, String command) {
		super(plugin, command, command);
	}

	public PlayerCommandBase(P plugin, String command, String usage, String... aliases) {
		super(plugin, command, usage, aliases);
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