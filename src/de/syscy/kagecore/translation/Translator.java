package de.syscy.kagecore.translation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.base.Splitter;

import de.syscy.kagecore.KageCore;
import de.syscy.kagecore.util.Util;
import lombok.Getter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Translator {
	private static final Splitter languageFileSplitter = Splitter.on('=').limit(2);
	private static @Getter String defaultLocale = "en";

	private static Map<String, Map<String, String>> translations = new HashMap<>();

	private static @Getter Map<Player, String> playerLanguages = new HashMap<>();

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

		File[] languageFiles = languageDirectory.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.matches("..\\.lang"); //Matches language file filenames like "en.lang", "xx.lang"
			}
		});

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

	public static String getLanguage(Entity player) {
		if(player instanceof Player) {
			return playerLanguages.containsKey(player) ? playerLanguages.get(player) : defaultLocale;
		}

		return defaultLocale;
	}

	public static String getLanguage(CommandSender player) {
		if(player instanceof Player) {
			return playerLanguages.containsKey(player) ? playerLanguages.get(player) : defaultLocale;
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
}