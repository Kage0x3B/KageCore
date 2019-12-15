package de.syscy.kagecore.command.argument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.command.CommandSender;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import de.syscy.kagecore.command.exception.InvalidCommandArgumentException;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

public class StringArgument extends CommandArgument<String> {
	protected @Getter Function<CommandSender, List<String>> suggestions;
	private Pattern regexPattern = null;

	public StringArgument(StringArgumentBuilder builder) {
		super(builder);

		suggestions = builder.suggestions;

		if(builder.regexPattern != null && !builder.regexPattern.isEmpty()) {
			regexPattern = Pattern.compile(builder.regexPattern);
		}
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

		if(regexPattern != null) {
			if(!regexPattern.matcher(args[index]).matches()) {
				throw new InvalidCommandArgumentException(name);
			}
		}

		if(allowedValuesFunction != null) {
			List<String> allowedValues = allowedValuesFunction.apply(sender);

			if(!allowedValues.isEmpty() && !allowedValues.contains(args[index])) {
				throw new InvalidCommandArgumentException(name);
			}
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args) {
		if(suggestions == null) {
			return Lists.newArrayList();
		}

		List<String> suggestions = new ArrayList<>();

		for(String possibleSuggestion : this.suggestions.apply(sender)) {
			if(possibleSuggestion.toLowerCase().startsWith(args[index].toLowerCase())) {
				suggestions.add(possibleSuggestion);
			}
		}

		return suggestions;
	}

	public static StringArgumentBuilder create(String name) {
		return new StringArgumentBuilder(name);
	}

	@Accessors(fluent = true)
	public static class StringArgumentBuilder extends CommandArgumentBuilder<String> {
		protected Function<CommandSender, List<String>> suggestions;
		private @Setter String regexPattern = null;

		private StringArgumentBuilder(String name) {
			super(name);
		}

		public StringArgumentBuilder values(String... values) {
			suggestions(values);
			allowedValues(values);

			return this;
		}

		public StringArgumentBuilder values(Function<CommandSender, List<String>> valuesFunction) {
			suggestions(valuesFunction);
			allowedValues(valuesFunction);

			return this;
		}

		public StringArgumentBuilder suggestions(Function<CommandSender, List<String>> suggestionsFunction) {
			suggestions = suggestionsFunction;

			return this;
		}

		public StringArgumentBuilder suggestions(final String[] suggestionArray) {
			suggestions = new Function<CommandSender, List<String>>() {
				@Override
				public List<String> apply(CommandSender sender) {
					return Arrays.asList(suggestionArray);
				}
			};

			return this;
		}

		public StringArgumentBuilder suggestions(final List<String> suggestionList) {
			suggestions = new Function<CommandSender, List<String>>() {
				@Override
				public List<String> apply(CommandSender sender) {
					return suggestionList;
				}
			};

			return this;
		}

		@Override
		public StringArgument build() {
			return new StringArgument(this);
		}
	}
}