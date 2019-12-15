package de.syscy.kagecore.command.argument;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.google.common.base.Function;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

public abstract class CommandArgument<T> {
	protected @Getter @Setter int index;

	protected @Getter String name;
	protected @Getter T defaultValue;
	protected @Getter boolean required;
	protected Function<CommandSender, List<String>> allowedValuesFunction;

	public CommandArgument(CommandArgumentBuilder<T> builder) {
		name = builder.name;
		defaultValue = builder.defaultValue;
		required = builder.required;
		allowedValuesFunction = builder.allowedValuesFunction;
	}

	public abstract T getValue(CommandSender sender, String[] args);

	public abstract void checkArg(CommandSender sender, String[] args);

	public List<String> onTabComplete(CommandSender sender, String[] args) {
		return null;
	}

	@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
	@Accessors(fluent = true)
	public static abstract class CommandArgumentBuilder<T> {
		protected final String name;
		protected @Setter T defaultValue = null;
		protected boolean required = true;
		protected Function<CommandSender, List<String>> allowedValuesFunction;

		public CommandArgumentBuilder<?> allowedValues(Function<CommandSender, List<String>> allowedValuesFunction) {
			this.allowedValuesFunction = allowedValuesFunction;

			return this;
		}

		public CommandArgumentBuilder<?> allowedValues(final String[] allowedValuesArray) {
			allowedValuesFunction = new Function<CommandSender, List<String>>() {
				@Override
				public List<String> apply(CommandSender sender) {
					return Arrays.asList(allowedValuesArray);
				}
			};

			return this;
		}

		public CommandArgumentBuilder<?> allowedValues(final List<String> allowedValuesList) {
			allowedValuesFunction = new Function<CommandSender, List<String>>() {
				@Override
				public List<String> apply(CommandSender sender) {
					return allowedValuesList;
				}
			};

			return this;
		}

		public CommandArgumentBuilder<?> notRequired() {
			this.required = false;

			return this;
		}

		public abstract CommandArgument<T> build();
	}
}