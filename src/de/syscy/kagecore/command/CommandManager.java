package de.syscy.kagecore.command;

import java.util.ArrayList;
import java.util.List;

import de.syscy.kagecore.KageCore;
import de.syscy.kagecore.command.exception.AccessDeniedException;
import de.syscy.kagecore.command.exception.CommandException;
import de.syscy.kagecore.command.exception.CommandNotFoundException;
import de.syscy.kagecore.command.exception.InvalidUsageException;
import de.syscy.kagecore.translation.Translator;
import de.syscy.kagecore.util.Util;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandManager<T extends JavaPlugin> extends CommandBase<T> {
	private List<CommandBase<?>> commands = new ArrayList<>();

	private int cmdPerPage = 6;

	public CommandManager(T plugin, String command) {
		super(plugin, command);

		commandManager = this;
	}

	@Override
	public final void onCommand(CommandSender sender) {
		String[] args = arguments.getCurrentArgs();

		try {
			if(args.length == 0) {
				if(sender.hasPermission(getPermissionPrefix() + ".help")) {
					sendHelpPage(sender, 1);

					return;
				} else {
					throw new AccessDeniedException();
				}
			}

			if(args[0].equalsIgnoreCase("help")) {
				if(!sender.hasPermission(getPermissionPrefix() + ".help")) {
					throw new AccessDeniedException();
				}

				if(args.length > 2) {
					throw new InvalidUsageException(getFullCommand(), "help", "[page|command name]");
				}

				if(args.length == 1) {
					sendHelpPage(sender, 1);

					return;
				} else if(args.length == 2) {
					if(Util.isNumber(args[1])) {
						int page = Integer.parseInt(args[1]);

						if(page > 0) {
							sendHelpPage(sender, page);
						} else {
							sendHelpPage(sender, 1);
						}

						return;
					}

					CommandBase<?> command = getCommand(args[1]);

					if(command == null) {
						throw new CommandNotFoundException(args[1]);
					}

					if(command.isAuthorized(sender)) {
						Translator.sendMessage(sender, "command.help.specifiedCommand.line1", command.getCommand());
						Translator.sendMessage(sender, "command.help.specifiedCommand.line2", Translator.translate(sender, "command." + getHelpDescriptionPrefix() + "." + command.getCommand() + ".description"));
					}
				}

				return;
			}

			CommandBase<?> command = getCommand(args[0]);

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

			command.getArguments().update(sender, cmdArgs);
			command.onCommand(sender);
		} catch(CommandException ex) {
			Translator.sendMessage(sender, ex.getMessage(), ex.getArgs());

			if(KageCore.isDebug()) {
				ex.printStackTrace();
			}
		} catch(Exception ex) {
			Translator.sendMessage(sender, "command.exception", ex.getClass().getPackage() + "." + ex.getClass().getName() + ": " + ex.getMessage());

			if(KageCore.isDebug()) {
				ex.printStackTrace();
			}
		}

		return;
	}

	public void addCommand(CommandBase<?> command) {
		commands.add(command);
		command.setCommandManager(this);
	}

	public void removeCommand(String commandName) {
		CommandBase<?> commandInstance = null;

		for(CommandBase<?> command : commands) {
			if(command.getCommand().equalsIgnoreCase(commandName)) {
				commandInstance = command;

				break;
			}
		}

		if(commandInstance != null) {
			commands.remove(commandInstance);
		}
	}

	private void sendHelpPage(CommandSender sender, int page) {
		List<CommandBase<?>> availableCommands = getAvailableCommands(sender);

		int size = availableCommands.size();
		int totalPages = size / cmdPerPage;

		if(size - cmdPerPage * totalPages > 0) {
			totalPages += 1;
		}

		if(page < 1 || page > totalPages) {
			page = 1;
		}

		Translator.sendMessage(sender, "command.help.header", getFullCommand());

		int startIndex = (page - 1) * cmdPerPage;
		int endIndex = cmdPerPage * page;

		for(int i = startIndex; i < endIndex; i++) {
			if(availableCommands.size() <= i) {
				break;
			}

			Translator.sendMessage(sender, "command.help.entry", getFullCommand(), availableCommands.get(i).getCommand(), Translator.translate(sender, "command." + getPermissionPrefix() + "." + availableCommands.get(i).getCommand() + ".description"));
		}

		Translator.sendMessage(sender, "command.help.footer1", getFullCommand());
		Translator.sendMessage(sender, "command.help.footer2", page, totalPages);
	}

	public CommandBase<?> getCommand(String commandName) {
		for(CommandBase<?> command : commands) {
			if(command.getCommand().equalsIgnoreCase(commandName)) {
				return command;
			}
		}

		return null;
	}

	@Override
	public final List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if(args.length == 1) {
			List<String> allCommands = new ArrayList<>();

			for(CommandBase<?> commandBase : commands) {
				if(commandBase.getCommand().toLowerCase().startsWith(args[0].toLowerCase()) && commandBase.isAuthorized(sender)) {
					allCommands.add(commandBase.getCommand());
				}
			}

			return allCommands;
		} else if(args.length > 1) {
			CommandBase<?> commandBase = getCommand(args[0]);

			if(commandBase != null && commandBase.isAuthorized(sender)) {
				String[] subArgs = new String[args.length - 1];
				System.arraycopy(args, 1, subArgs, 0, subArgs.length);

				return commandBase.onTabComplete(sender, command, alias, subArgs);
			}
		}

		return null;
	}

	private List<CommandBase<?>> getAvailableCommands(CommandSender sender) {
		List<CommandBase<?>> availableCommands = new ArrayList<>();

		for(CommandBase<?> command : commands) {
			if(command.isAuthorized(sender)) {
				availableCommands.add(command);
			}
		}

		return availableCommands;
	}

	public String getHelpDescriptionPrefix() {
		return (commandManager != null && !commandManager.equals(this) ? commandManager.getHelpDescriptionPrefix() + "." : "") + command;
	}

	public String getPermissionPrefix() {
		return getFullCommand().replaceAll(" ", ".");
	}

	public String getFullCommand() {
		return commandManager != null && !commandManager.equals(this) ? commandManager.getPermissionPrefix() + " " + command : command;
	}
}