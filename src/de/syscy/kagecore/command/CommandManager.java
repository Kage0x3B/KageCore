package de.syscy.kagecore.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import de.syscy.kagecore.command.exception.AccessDeniedException;
import de.syscy.kagecore.command.exception.CommandException;
import de.syscy.kagecore.command.exception.CommandNotFoundException;
import de.syscy.kagecore.command.exception.InvalidUsageException;
import de.syscy.kagecore.translation.Translator;
import de.syscy.kagecore.util.Util;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommandManager implements CommandExecutor, TabCompleter {
	private final @Getter JavaPlugin plugin;
	private final @Getter String commandName;

	private List<CommandBase> commands = new ArrayList<>();

	private int cmdPerPage = 6;

	@Override
	public boolean onCommand(CommandSender sender, Command bukkitCommand, String label, String[] args) {
		try {
			if(args.length == 0) {
				if(sender.hasPermission(commandName + ".help")) {
					sendHelpPage(sender, 1);

					return true;
				} else {
					throw new AccessDeniedException();
				}
			}

			if(args[0].equalsIgnoreCase("help")) {
				if(!sender.hasPermission(commandName + ".help")) {
					throw new AccessDeniedException();
				}

				if(args.length > 2) {
					throw new InvalidUsageException(commandName, "help", "[page|command name]");
				}

				if(args.length == 1) {
					sendHelpPage(sender, 1);

					return true;
				} else if(args.length == 2) {
					if(Util.isNumber(args[1])) {
						int page = Integer.parseInt(args[1]);

						if(page > 0) {
							sendHelpPage(sender, page);
						} else {
							sendHelpPage(sender, 1);
						}

						return true;
					}

					CommandBase command = getCommand(args[1]);

					if(command == null) {
						throw new CommandNotFoundException(args[1]);
					}

					if(command.isAuthorized(sender)) {
						Translator.sendMessage(sender, "command.help.specifiedCommand.line1", command.getCommand());
						Translator.sendMessage(sender, "command.help.specifiedCommand.line2", command.getAliases().toString());
						Translator.sendMessage(sender, "command.help.specifiedCommand.line3", Translator.translate(sender, "command." + command.getCommand() + ".description"));
					}
				}

				return true;
			}

			CommandBase command = getCommand(args[0]);

			if(command == null) {
				throw new CommandNotFoundException(args[0]);
			}

			if(!command.isAuthorized(sender)) {
				throw new AccessDeniedException();
			}

			String[] cmdArgs = new String[args.length - 1];

			if(args.length > 1) {
				System.arraycopy(args, 1, cmdArgs, 0, cmdArgs.length);
			}

			command.onCommand(sender, cmdArgs);
		} catch(CommandException ex) {
			Translator.sendMessage(sender, ex.getMessage(), ex.getArgs());
		} catch(Exception ex) {
			Translator.sendMessage(sender, "command.exception", ex.getMessage());
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
		int totalPages = size / cmdPerPage;

		if(size - cmdPerPage * totalPages > 0) {
			totalPages += 1;
		}

		if(page < 1 || page > totalPages) {
			page = 1;
		}

		Translator.sendMessage(sender, "command.help.header", commandName);

		int startIndex = (page - 1) * cmdPerPage;
		int endIndex = cmdPerPage * page;

		for(int i = startIndex; i < endIndex; i++) {
			if(availableCommands.size() <= i) {
				break;
			}

			Translator.sendMessage(sender, "command.help.entry", commandName, availableCommands.get(i).getCommand(), Translator.translate(sender, "command." + availableCommands.get(i).getCommand() + ".description"));
		}

		Translator.sendMessage(sender, "command.help.footer1", commandName);
		Translator.sendMessage(sender, "command.help.footer2", page, totalPages);
	}

	public CommandBase getCommand(String commandName) {
		for(CommandBase command : commands) {
			if(command.getCommand().equalsIgnoreCase(commandName) || command.getAliases().contains(commandName.toLowerCase())) {
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
				if(commandBase.getCommand().toLowerCase().startsWith(args[0].toLowerCase()) && commandBase.isAuthorized(sender)) {
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