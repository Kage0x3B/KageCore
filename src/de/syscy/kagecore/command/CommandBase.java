package de.syscy.kagecore.command;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public abstract class CommandBase<T extends JavaPlugin> implements TabCompleter {
	protected final T plugin;
	protected @Getter String command;
	private @Getter String usage;
	private @Getter List<String> aliases;

	protected @Getter @Setter(value = AccessLevel.PROTECTED) CommandManager<?> commandManager;

	public CommandBase(T plugin, String command) {
		this(plugin, command, command);
	}

	public CommandBase(T plugin, String command, String usage, String... aliases) {
		this.plugin = plugin;
		this.command = command;
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

	protected List<String> getPossibleSuggestions(String[] args, Iterable<String> allPossibleSuggestions) {
		List<String> suggestions = new ArrayList<>();
		int currentArgIndex = args.length - 1;
		Iterator<String> iterator = allPossibleSuggestions.iterator();

		while(iterator.hasNext()) {
			String possibleSuggestion = iterator.next();
			if(possibleSuggestion.toLowerCase().startsWith(args[currentArgIndex].toLowerCase())) {
				suggestions.add(possibleSuggestion);
			}
		}

		return suggestions;
	}

	protected List<String> getPossibleSuggestions(String[] args, String... allPossibleSuggestions) {
		List<String> suggestions = new ArrayList<>();
		int currentArgIndex = args.length - 1;

		for(String possibleSuggestion : allPossibleSuggestions) {
			if(possibleSuggestion.toLowerCase().startsWith(args[currentArgIndex].toLowerCase())) {
				suggestions.add(possibleSuggestion);
			}
		}

		return suggestions;
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
}