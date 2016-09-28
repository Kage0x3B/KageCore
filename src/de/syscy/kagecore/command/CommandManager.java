package de.syscy.kagecore.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import de.syscy.kagecore.util.Util;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommandManager implements CommandExecutor, TabCompleter {
	private final @Getter JavaPlugin plugin;
	private final @Getter String commandName;
	private final @Getter Theme theme;

	private List<CommandBase> commands = new ArrayList<>();

	private int cmdPerPage = 6;

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0) {
			if(sender.hasPermission(commandName + ".help")) {
				sendHelpPage(sender, 1);

				return true;
			}

			sender.sendMessage(ChatColor.RED + "You do not have access to this command!");

			return true;
		}

		if(args[0].equalsIgnoreCase("help")) {
			if(sender.hasPermission(commandName + ".help")) {
				if(args.length == 1) {
					sendHelpPage(sender, 1);

					return true;
				}

				if(args.length == 2) {
					if(Util.isNumber(args[1])) {
						int page = Integer.parseInt(args[1]);

						if(page > 0) {
							sendHelpPage(sender, page);

							return true;
						}

						sendHelpPage(sender, 1);

						return true;
					}

					CommandBase command;

					if((command = getCommand(args[1])) != null && command.isAuthorized(sender)) {
						sender.sendMessage(ChatColor.GOLD + "Command: " + ChatColor.GRAY + command.getCommand());
						sender.sendMessage(ChatColor.GOLD + "Aliases: " + ChatColor.GRAY + command.getAliases().toString());
						sender.sendMessage(ChatColor.GOLD + "Description: " + ChatColor.GRAY + command.getDescription());
						return true;
					}

					sender.sendMessage(ChatColor.RED + " page/command \"" + args[1] + "\".");

					return true;
				}

				sender.sendMessage(ChatColor.RED + "Usage: " + ChatColor.GRAY + "/" + commandName + " help [page]");

				return true;
			}

			sender.sendMessage(ChatColor.RED + "You do not have access to this command!");

			return true;
		}

		CommandBase command = getCommand(args[0]);

		if(command == null) {
			sender.sendMessage(ChatColor.RED + " Command: " + ChatColor.DARK_RED + args[0].toLowerCase());
		} else {
			String[] cmdArgs = new String[args.length - 1];

			if(args.length > 1) {
				System.arraycopy(args, 1, cmdArgs, 0, cmdArgs.length);
			}

			command.onCommand(sender, cmdArgs);
		}

		return true;
	}

	public void addCommand(CommandBase command) {
		commands.add(command);
		command.setCommandManager(this);
		command.setPlugin(plugin);
	}

	private void sendHelpPage(CommandSender sender, int page) {
		List<CommandBase> availableCommands = getAvailableCommands(sender);

		int size = availableCommands.size();
		int totalPages = (size / this.cmdPerPage);

		if(size - this.cmdPerPage * totalPages > 0) {
			totalPages += 1;
		}

		if((page < 1) || (page > totalPages)) {
			page = 1;
		}

		sender.sendMessage("------Help------");

		int startIndex = (page - 1) * this.cmdPerPage;
		int endIndex = this.cmdPerPage * page;

		for(int i = startIndex; i < endIndex; i++) {
			if(availableCommands.size() <= i) {
				break;
			}

			sender.sendMessage(theme.getCommandColor() + "/" + commandName + " " + availableCommands.get(i).getCommand() + theme.getSplitterColor() + " | " + theme.getDescriptionColor() + availableCommands.get(i).getDescription());
		}

		sender.sendMessage(ChatColor.DARK_GRAY + "Type \"/" + commandName + " help <command>\" to get information on a command.");
		sender.sendMessage(ChatColor.GOLD + "Page number: " + ChatColor.DARK_BLUE + page + "/" + totalPages);
	}

	public CommandBase getCommand(String commandString) {
		for(CommandBase command : commands) {
			if(command.getCommand().equalsIgnoreCase(commandString) || command.getAliases().contains(commandString.toLowerCase())) {
				return command;
			}
		}

		return null;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if(args.length == 1) {
			List<String> allCommands = new ArrayList<String>();

			for(CommandBase commandBase : commands) {
				if(commandBase.isAuthorized(sender)) {
					allCommands.add(commandBase.getCommand());
				}
			}

			return allCommands;
		} else if(args.length > 1) {
			CommandBase commandBase = getCommand(args[0]);

			if(commandBase != null && commandBase.isAuthorized(sender)) {
				String[] subArgs = new String[args.length - 1];
				System.arraycopy(args, 1, subArgs, 0, subArgs.length);

				return commandBase.onTabComplete(sender, command, alias, subArgs);
			}
		}

		return null;
	}
	
	private List<CommandBase> getAvailableCommands(CommandSender sender) {
		List<CommandBase> availableCommands = new ArrayList<CommandBase>();

		for(CommandBase command : commands) {
			if(command.isAuthorized(sender)) {
				availableCommands.add(command);
			}
		}
		
		return availableCommands;
	}
}