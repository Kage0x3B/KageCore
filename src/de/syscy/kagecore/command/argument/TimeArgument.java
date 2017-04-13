package de.syscy.kagecore.command.argument;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.command.CommandSender;

import com.google.common.base.Splitter;

import de.syscy.kagecore.command.exception.InvalidCommandArgumentException;
import de.syscy.kagecore.command.exception.UnknownTimeUnitException;
import lombok.Setter;
import lombok.experimental.Accessors;

public class TimeArgument extends CommandArgument<Long> {
	private static final Pattern LETTER_PATTERN = Pattern.compile("[a-z]");
	private static final List<String> SUGGESTIONS = Arrays.asList("seconds", "minutes", "hours", "days", "weeks", "months", "years");

	private long min;
	private long max;

	public TimeArgument(TimeArgumentBuilder builder) {
		super(builder);

		min = builder.min;
		max = builder.max;
	}

	@Override
	public Long getValue(CommandSender sender, String[] args) {
		if(args.length <= index) {
			return defaultValue;
		}

		long time = 0;

		for(String timeString : Splitter.on(',').trimResults().omitEmptyStrings().split(args[index].toLowerCase())) {
			Matcher letterMatcher = LETTER_PATTERN.matcher(timeString);

			if(letterMatcher.find()) {
				int firstLetterIndex = letterMatcher.start();

				String timeUnit = timeString.substring(firstLetterIndex);
				long timeUnitAmount = Long.parseLong(timeString.substring(0, firstLetterIndex));

				time += toMilliSeconds(timeUnit, timeUnitAmount);
			} else {
				time += Long.parseLong(timeString) * 1000 * 60; //Usually in minutes
			}
		}

		return time;
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

		long value = getValue(sender, args);

		if(value < min || value > max) {
			throw new InvalidCommandArgumentException(name);
		}
	}

	private long toMilliSeconds(String timeUnit, long timeUnitAmount) {
		switch(timeUnit) {
			case "s":
			case "sec":
			case "seconds":
				return timeUnitAmount * 1000;
			case "m":
			case "min":
			case "minutes":
				return timeUnitAmount * 1000 * 60;
			case "h":
			case "hours":
				return timeUnitAmount * 1000 * 60 * 60;
			case "d":
			case "days":
				return timeUnitAmount * 1000 * 60 * 60 * 24;
			case "w":
			case "weeks":
				return timeUnitAmount * 1000 * 60 * 60 * 24 * 7;
			case "mo":
			case "months":
				return timeUnitAmount * 1000 * 60 * 60 * 24 * 30;
			case "y":
			case "years":
				return timeUnitAmount * 1000 * 60 * 60 * 24 * 30 * 12;
		}

		throw new UnknownTimeUnitException(timeUnit);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args) {
		return SUGGESTIONS;
	}

	public static TimeArgumentBuilder create(String name) {
		return new TimeArgumentBuilder(name);
	}

	@Accessors(fluent = true)
	public static class TimeArgumentBuilder extends CommandArgumentBuilder<Long> {
		private @Setter long min = 1;
		private @Setter long max = Long.MAX_VALUE;

		private TimeArgumentBuilder(String name) {
			super(name);
		}

		public TimeArgumentBuilder range(long min, long max) {
			return min(min).max(max);
		}

		@Override
		public TimeArgument build() {
			return new TimeArgument(this);
		}
	}
}