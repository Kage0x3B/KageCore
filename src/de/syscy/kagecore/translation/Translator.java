package de.syscy.kagecore.translation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.base.Splitter;

import de.syscy.kagecore.KageCore;
import de.syscy.kagecore.util.Util;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Translator {
	public static char SIGN = '$';
	private static Pattern tsPattern = Pattern.compile(SIGN + "[\\w\\d.]+(;!?[A-Za-z0-9 ]+)*;");
	private static List<Character> partTypeIdentifiers = Arrays.asList('i', 'd', 'l', 'f');

	private static @Getter @Setter(value = AccessLevel.PROTECTED) boolean enabled = false;

	private static final @Getter File mainLanguageDirectory = new File("./lang");

	private static final Splitter languageFileSplitter = Splitter.on('=').limit(2);
	private static @Getter String defaultLocale = "en";

	private static Map<String, Map<String, String>> translations = new HashMap<>();

	private static @Getter Map<Player, String> playerLanguages = new HashMap<>();

	public static void addMinecraftLanguageFiles() {
		addLanguageFiles(null, new File(mainLanguageDirectory, "Minecraft"));
	}

	public static void addLanguageFiles(JavaPlugin plugin) {
		addLanguageFiles(plugin, new File(mainLanguageDirectory, plugin.getName()));
	}

	public static void addLanguageFiles(JavaPlugin plugin, File languageDirectory) {
		if(!languageDirectory.exists()) {
			languageDirectory.mkdirs();
		}

		File defaultLocaleFile = new File(languageDirectory, defaultLocale + ".lang");

		if(!defaultLocaleFile.exists() && plugin != null) {
			InputStream defaultLocaleResource = plugin.getResource(defaultLocale + ".lang");

			if(defaultLocaleResource != null) {
				Util.copy(defaultLocaleResource, defaultLocaleFile);
			}
		}

		File[] languageFiles = languageDirectory.listFiles((dir, name) -> {
			return name.matches("..\\.lang"); //Matches language file filenames like "en.lang", "xx.lang"
		});

		if(languageFiles != null) {
			for(File languageFile : languageFiles) {
				String locale = languageFile.getName().split("\\.")[0].toLowerCase();

				Map<String, String> currentTranslations = new HashMap<>();

				try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(languageFile), "UTF8"))) {
					String line;

					while((line = reader.readLine()) != null) {
						if(!line.isEmpty()) {
							List<String> lineSplit = languageFileSplitter.splitToList(line);

							String key = lineSplit.get(0).toLowerCase();
							String translation = lineSplit.size() == 2 ? lineSplit.get(1) : "";

							currentTranslations.put(key, translation);
						}
					}
				} catch(IOException ex) {
					ex.printStackTrace();
				}

				if(!currentTranslations.isEmpty()) {
					if(!translations.containsKey(locale)) {
						translations.put(locale, currentTranslations);
					} else {
						translations.get(locale).putAll(currentTranslations);
					}
				}
			}
		}
	}

	public static String getLanguage(Entity entity) {
		if(entity instanceof Player) {
			return playerLanguages.containsKey(entity) ? playerLanguages.get(entity) : defaultLocale;
		}

		return defaultLocale;
	}

	public static String getLanguage(CommandSender sender) {
		if(sender instanceof Player) {
			return playerLanguages.containsKey(sender) ? playerLanguages.get(sender) : defaultLocale;
		}

		return defaultLocale;
	}

	public static String getLanguage(Player player) {
		return playerLanguages.containsKey(player) ? playerLanguages.get(player) : defaultLocale;
	}

	public static void sendMessage(Entity entity, String key, Object... args) {
		if(entity instanceof Player) {
			entity.sendMessage(translate((Player) entity, key, args));
		}
	}

	public static void sendMessage(CommandSender sender, String key, Object... args) {
		sender.sendMessage(translate(sender, key, args));
	}

	public static void sendMessage(Player player, String key, Object... args) {
		player.sendMessage(translate(player, key, args));
	}

	public static String translate(Entity entity, String key, Object... args) {
		if(entity instanceof Player) {
			return translate((Player) entity, key, args);
		} else {
			return translate(defaultLocale, key, args);
		}
	}

	public static String translate(CommandSender sender, String key, Object... args) {
		if(sender instanceof Player) {
			return translate((Player) sender, key, args);
		} else {
			return translate(defaultLocale, key, args);
		}
	}

	public static String translate(Player player, String key, Object... args) {
		String language = getLanguage(player).toLowerCase();

		return translate(language, key, args);
	}

	public static String translate(String language, String key, Object... args) {
		if(key.contains(" ")) {
			return key;
		}

		language = language.toLowerCase();
		key = key.toLowerCase();

		String text = key;

		if(translations.containsKey(language) && translations.get(language).containsKey(key)) {
			text = ChatColor.translateAlternateColorCodes('&', translations.get(language).get(key));
		} else if(translations.get(defaultLocale).containsKey(key)) {
			text = ChatColor.translateAlternateColorCodes('&', translations.get(defaultLocale).get(key));
		} else {
			KageCore.debugMessage("Invalid translation key: " + key);
		}

		if(args != null && !text.equals(key)) {
			try {
				return String.format(text, args);
			} catch(IllegalFormatException ex) {
				String argsString = "";

				for(Object arg : args) {
					argsString += arg.toString() + "(" + arg.getClass().getSimpleName() + "), ";
				}

				argsString = argsString.isEmpty() ? argsString : argsString.substring(0, argsString.length() - 2);

				KageCore.debugMessage("Format error: " + text + " (" + key + ") with " + args.length + " args: " + argsString);
			}
		}

		return text;
	}

	/**
	 * Usually returns the same string. If the packet translator doesn't work, it returns the translated version (using the default locale)
	 * @param packetTranslationString A packet translation string (A string containing one or multiple translation keys
	 * @return The packetTranslationString or the translated result of it
	 */
	public String processPacketTranslationString(String packetTranslationString) {
		if(enabled) {
			return packetTranslationString;
		} else {
			return tryTranslateString(packetTranslationString, defaultLocale);
		}
	}

	public static String tryTranslateString(String string, Player player) {
		return tryTranslateString(string, getLanguage(player));
	}

	public static String tryTranslateString(String string, String language) {
		if(string == null || string.indexOf(Translator.SIGN) < 0) {
			return string;
		}

		TranslateString[] parts = parse(string);

		StringBuilder result = new StringBuilder();

		for(TranslateString part : parts) {
			part.append(result, language);
		}

		return result.toString();
	}

	private static TranslateString[] parse(String string) {
		ArrayList<TranslateString> translateStrings = new ArrayList<>();
		Matcher matcher = tsPattern.matcher(string);

		for(int i = 0, len = string.length(); i < len;) {
			if(matcher.find(i)) {
				if(matcher.start() != i) {
					translateStrings.add(new FixedString(string.substring(i, matcher.start())));
				}

				translateStrings.add(new TranslatableString(string.substring(matcher.start(), matcher.end())));
				i = matcher.end();
			} else {
				translateStrings.add(new FixedString(string.substring(i)));

				break;
			}
		}

		return translateStrings.toArray(new TranslateString[translateStrings.size()]);
	}

	private interface TranslateString {
		public void append(StringBuilder stringBuilder, String language);
	}

	@AllArgsConstructor
	private static class FixedString implements TranslateString {
		private final String string;

		@Override
		public void append(StringBuilder stringBuilder, String language) {
			stringBuilder.append(string);
		}
	}

	@AllArgsConstructor
	private static class TranslatableString implements TranslateString {
		private String string;

		@Override
		public void append(StringBuilder stringBuilder, String language) {
			string = string.substring(1, string.length() - 1);
			String[] parts = string.split(";");
			Object[] args = new Object[parts.length - 1];

			for(int i = 1; i < parts.length; i++) {
				if(parts[i] == null || parts[i].isEmpty()) {
					continue;
				}

				String part = parts[i].toLowerCase();
				char partType = part.charAt(part.length() - 1);

				if(partTypeIdentifiers.contains(partType)) {
					try {
						part = part.substring(0, part.length() - 1);

						NumberFormat formatter = NumberFormat.getInstance();
						ParsePosition pos = new ParsePosition(0);
						Number number = formatter.parse(part, pos);

						if(part.length() == pos.getIndex()) {
							switch(partType) {
								case 'i':
									args[i - 1] = number.intValue();
									break;
								case 'd':
									args[i - 1] = number.doubleValue();
									break;
								case 'l':
									args[i - 1] = number.longValue();
									break;
								case 'f':
									args[i - 1] = number.floatValue();
									break;
							}
						}
					} catch(Exception ex) {
						ex.printStackTrace();

						args[i - 1] = parts[i];
					}
				} else {
					args[i - 1] = parts[i];
				}
			}

			stringBuilder.append(Translator.translate(language, parts[0], args));
		}
	}
}