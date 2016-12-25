package de.syscy.kagecore.command.argument;

import java.util.List;

import org.bukkit.command.CommandSender;

import de.syscy.kagecore.command.exception.InvalidCommandArgumentException;

public class UnknownArgument extends CommandArgument<String> {
	public UnknownArgument(UnknownArgumentBuilder builder) {
		super(builder);
	}

	@Override
	public String getValue(CommandSender sender, String[] args) {
		return args.length <= index ? defaultValue : args[index];
	}

	@Override
	public void checkArg(CommandSender sender, String[] args) {
		if(args.length <= index) {
			return;
		}

		if(allowedValuesFunction != null) {
			List<String> allowedValues = allowedValuesFunction.apply(sender);

			if(!allowedValues.isEmpty() && !allowedValues.contains(args[index])) {
				throw new InvalidCommandArgumentException(name);
			}
		}
	}

	public static UnknownArgumentBuilder create(String name) {
		return new UnknownArgumentBuilder(name);
	}

	public static class UnknownArgumentBuilder extends CommandArgumentBuilder<String> {
		private UnknownArgumentBuilder(String name) {
			super(name);
		}

		@Override
		public CommandArgument<String> build() {
			return new UnknownArgument(this);
		}
	}
}