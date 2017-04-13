package de.syscy.kagecore.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.base.Joiner;

import de.syscy.kagecore.command.argument.CommandArgument;
import de.syscy.kagecore.command.argument.CommandArguments;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public abstract class CommandBase<T extends JavaPlugin> implements CommandExecutor, TabCompleter {
	protected final T plugin;
	protected @Getter String command;

	protected @Getter CommandArguments arguments = new CommandArguments(this);

	protected @Getter @Setter(value = AccessLevel.PROTECTED) CommandManager<?> commandManager;

	public CommandBase(T plugin, String command, CommandArgument<?>... commandArguments) {
		this.plugin = plugin;
		this.command = command;

		if(commandArguments.length > 0) {
			for(CommandArgument<?> commandArgument : commandArguments) {
				arguments.addCommandArgument(commandArgument);
			}
		}
	}

	public abstract void onCommand(CommandSender sender);

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		arguments.update(sender, args);
		onCommand(sender);

		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return arguments.onTabComplete(sender, args);
	}

	public boolean isAuthorized(CommandSender sender) {
		return sender.hasPermission(commandManager.getPermissionPrefix() + "." + getCommand().trim()) || sender.isOp();
	}

	public boolean isExempt(CommandSender sender) {
		return sender.hasPermission(commandManager.getPermissionPrefix() + "." + getCommand().trim() + ".exempt");
	}

	protected Player getPlayer(String name) {
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(player.getName().equalsIgnoreCase(name)) {
				return player;
			}
		}
		return null;
	}

	protected boolean isValidPlayer(Player player) {
		return player != null && player.isOnline();
	}

	public String getUsageString() {
		List<String> usageArguments = new ArrayList<>();

		for(CommandArgument<?> arg : arguments.getCommandArguments()) {
			usageArguments.add(arg.getName());
		}

		return Joiner.on(", ").join(usageArguments);
	}
}