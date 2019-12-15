package de.syscy.kagecore.command.argument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.google.common.base.Function;

import lombok.Getter;

public class StringListArgument extends CommandArgument<List<String>> {
	protected @Getter Function<CommandSender, List<String>> suggestions;

	public StringListArgument(StringListArgumentBuilder builder) {
		super(builder);

		suggestions = builder.suggestions;
	}

	@Override
	public List<String> getValue(CommandSender sender, String[] args) {
		if(args.length <= index) {
			return defaultValue;
		}

		List<String> stringList = new ArrayList<>();

		for(int i = index; i < args.length; i++) {
			stringList.add(args[i]);
		}

		return stringList;
	}

	@Override
	public void checkArg(CommandSender sender, String[] args) {
		if(args.length <= index) {
			return;
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args) {
		List<String> suggestions = new ArrayList<>();

		for(String possibleSuggestion : this.suggestions.apply(sender)) {
			if(possibleSuggestion.toLowerCase().startsWith(args[index].toLowerCase())) {
				suggestions.add(possibleSuggestion);
			}
		}

		return suggestions;
	}

	public static StringListArgumentBuilder create(String name) {
		return new StringListArgumentBuilder(name);
	}

	public static class StringListArgumentBuilder extends CommandArgumentBuilder<List<String>> {
		protected Function<CommandSender, List<String>> suggestions;

		private StringListArgumentBuilder(String name) {
			super(name);
		}

		public StringListArgumentBuilder suggestions(Function<CommandSender, List<String>> suggestionsFunction) {
			suggestions = suggestionsFunction;

			return this;
		}

		public StringListArgumentBuilder suggestions(final String[] suggestionArray) {
			suggestions = new Function<CommandSender, List<String>>() {
				@Override
				public List<String> apply(CommandSender sender) {
					return Arrays.asList(suggestionArray);
				}
			};

			return this;
		}

		public StringListArgumentBuilder suggestions(final List<String> suggestionList) {
			suggestions = new Function<CommandSender, List<String>>() {
				@Override
				public List<String> apply(CommandSender sender) {
					return suggestionList;
				}
			};

			return this;
		}

		@Override
		public CommandArgument<List<String>> build() {
			return new StringListArgument(this);
		}
	}
}