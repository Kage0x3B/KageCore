package de.syscy.kagecore.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import de.syscy.kagecore.translation.Translator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public abstract class CommandBase implements TabCompleter {
	private @Getter String command;
	private @Getter String description;
	private @Getter String usage;
	private @Getter List<String> aliases;

	protected @Setter(value = AccessLevel.PROTECTED) CommandManager commandManager;
	protected @Setter(value = AccessLevel.PROTECTED) JavaPlugin plugin;

	public CommandBase(String command) {
		this(command, "", command);
	}

	public CommandBase(String command, String description) {
		this(command, description, command);
	}

	public CommandBase(String command, String description, String usage, String... aliases) {
		this.command = command;
		this.description = description;
		this.usage = usage;
		this.aliases = new ArrayList<>();

		for(String alias : aliases) {
			this.aliases.add(alias.toLowerCase());
		}
	}

	public abstract void onCommand(CommandSender sender, String[] args);
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return null;
	}

	protected String arrayToString(String[] array, int startingIndex, boolean useCommas) {
		String string = "";

		for(int i = startingIndex; i < array.length; i++) {
			if(i == array.length - 1) {
				string = string + array[i];
			} else {
				string = string + array[i] + (useCommas ? ", " : " ");
			}
		}

		return string;
	}

	protected boolean isAuthorized(CommandSender sender) {
		return (sender.hasPermission(commandManager.getCommandName() + "." + getCommand().trim())) || (sender.isOp());
	}

	protected boolean isExempt(CommandSender sender) {
		return sender.hasPermission(commandManager.getCommandName() + "." + getCommand().trim() + ".exempt");
	}

	protected void sendExemptMessage(CommandSender sender, String player) {
		sender.sendMessage(Translator.translate(sender, "command.exempt", new Object[] { player }));
	}

	protected void sendUsageMessage(CommandSender sender) {
		sender.sendMessage(Translator.translate(sender, "command.usage", new Object[] { commandManager.getCommandName(), this.command, this.usage }));
	}

	protected void sendPlayerNotFoundMessage(CommandSender sender, String player) {
		sender.sendMessage(Translator.translate(sender, "command.playerNotFound", new Object[] { player }));
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
		return (player != null) && (player.isOnline());
	}

	protected void denyAccess(CommandSender sender) {
		Translator.sendMessage(sender, "command.denyAccess");
	}
}