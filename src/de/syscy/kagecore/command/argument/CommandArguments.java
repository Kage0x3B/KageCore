package de.syscy.kagecore.command.argument;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import de.syscy.kagecore.command.CommandBase;
import de.syscy.kagecore.command.exception.InvalidUsageException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommandArguments {
	private final CommandBase<?> commandBase;

	private @Getter List<CommandArgument<?>> commandArguments = new ArrayList<>();
	private Map<String, Integer> nameToIndex = new HashMap<>();
	private int requiredArgAmount = 0;

	private CommandSender currentSender;
	private @Getter String[] currentArgs;

	public List<String> onTabComplete(CommandSender sender, String[] args) {
		int currentArgIndex = args.length - 1;

		if(commandArguments.size() > currentArgIndex) {
			return commandArguments.get(currentArgIndex).onTabComplete(sender, args);
		}

		return null;
	}

	public void update(CommandSender sender, String[] args) {
		currentSender = sender;

		if(args.length < requiredArgAmount) {
			throw new InvalidUsageException(commandBase);
		}

		currentArgs = args;
	}

	public void addCommandArgument(CommandArgument<?> commandArgument) {
		if(commandArgument == null) {
			return;
		}

		int index = commandArguments.size();

		commandArgument.setIndex(index);
		commandArguments.add(commandArgument);
		nameToIndex.put(commandArgument.getName().toLowerCase(), index);

		if(commandArguments.size() > 1 && commandArgument.isRequired() && !commandArguments.get(commandArguments.size() - 2).isRequired()) {
			throw new IllegalStateException("There can't be a required command argument (" + commandArgument.getName() + ") after a not required one (" + commandArguments.get(commandArguments.size() - 2).getName() + ")!");
		}

		if(commandArgument.isRequired()) {
			requiredArgAmount = index + 1;
		}
	}

	public String get(int index) {
		return currentArgs[index];
	}

	// ===== String =====

	public String getString(String argumentName) {
		return getString(argumentName, null);
	}

	public String getString(int index) {
		return getString(index, null);
	}

	public String getString(String argumentName, String defaultValue) {
		if(!nameToIndex.containsKey(argumentName.toLowerCase())) {
			return null;
		}

		return getString(nameToIndex.get(argumentName.toLowerCase()), defaultValue);
	}

	public String getString(int index, String defaultValue) {
		commandArguments.get(index).checkArg(currentSender, currentArgs);

		return ((StringArgument) commandArguments.get(index)).getValue(currentSender, currentArgs);
	}

	// ===== String list =====

	public List<String> getStringList(String argumentName) {
		return getStringList(argumentName, 0);
	}

	public List<String> getStringList(int index) {
		return getStringList(index, 0);
	}

	public List<String> getStringList(String argumentName, double defaultValue) {
		if(!nameToIndex.containsKey(argumentName.toLowerCase())) {
			return new ArrayList<>();
		}

		return getStringList(nameToIndex.get(argumentName.toLowerCase()), defaultValue);
	}

	public List<String> getStringList(int index, double defaultValue) {
		commandArguments.get(index).checkArg(currentSender, currentArgs);

		return ((StringListArgument) commandArguments.get(index)).getValue(currentSender, currentArgs);
	}

	// ===== Integer =====

	public int getInt(String argumentName) {
		return getInt(argumentName, 0);
	}

	public int getInt(int index) {
		return getInt(index, 0);
	}

	public int getInt(String argumentName, int defaultValue) {
		if(!nameToIndex.containsKey(argumentName.toLowerCase())) {
			return 0;
		}

		return getInt(nameToIndex.get(argumentName.toLowerCase()), defaultValue);
	}

	public int getInt(int index, int defaultValue) {
		commandArguments.get(index).checkArg(currentSender, currentArgs);

		return ((IntegerArgument) commandArguments.get(index)).getValue(currentSender, currentArgs);
	}

	// ===== Double =====

	public double getDouble(String argumentName) {
		return getDouble(argumentName, 0);
	}

	public double getDouble(int index) {
		return getDouble(index, 0);
	}

	public double getDouble(String argumentName, double defaultValue) {
		if(!nameToIndex.containsKey(argumentName.toLowerCase())) {
			return 0;
		}

		return getDouble(nameToIndex.get(argumentName.toLowerCase()), defaultValue);
	}

	public double getDouble(int index, double defaultValue) {
		commandArguments.get(index).checkArg(currentSender, currentArgs);

		return ((DoubleArgument) commandArguments.get(index)).getValue(currentSender, currentArgs);
	}

	// ===== Boolean =====

	public boolean getBoolean(String argumentName) {
		return getBoolean(argumentName, false);
	}

	public boolean getBoolean(int index) {
		return getBoolean(index, false);
	}

	public boolean getBoolean(String argumentName, boolean defaultValue) {
		if(!nameToIndex.containsKey(argumentName.toLowerCase())) {
			return false;
		}

		return getBoolean(nameToIndex.get(argumentName.toLowerCase()), defaultValue);
	}

	public boolean getBoolean(int index, boolean defaultValue) {
		commandArguments.get(index).checkArg(currentSender, currentArgs);

		return ((BooleanArgument) commandArguments.get(index)).getValue(currentSender, currentArgs);
	}

	// ===== Player =====

	public OfflinePlayer getPlayer(String argumentName) {
		return getPlayer(argumentName, null);
	}

	public OfflinePlayer getPlayer(int index) {
		return getPlayer(index, null);
	}

	public OfflinePlayer getPlayer(String argumentName, OfflinePlayer defaultValue) {
		if(!nameToIndex.containsKey(argumentName.toLowerCase())) {
			return null;
		}

		return getPlayer(nameToIndex.get(argumentName.toLowerCase()), defaultValue);
	}

	public OfflinePlayer getPlayer(int index, OfflinePlayer defaultValue) {
		commandArguments.get(index).checkArg(currentSender, currentArgs);

		return ((PlayerArgument) commandArguments.get(index)).getValue(currentSender, currentArgs);
	}
}