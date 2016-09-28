package de.syscy.kagecore.command;

import org.bukkit.ChatColor;

import de.syscy.kagecore.util.KConfiguration;
import de.syscy.kagecore.util.KConfigurationSection;
import lombok.Getter;

public class Theme {
	private final @Getter ChatColor commandColor;
	private final @Getter ChatColor splitterColor;
	private final @Getter ChatColor descriptionColor;

	public Theme(ThemeSection themeSection) {
		this.commandColor = parseChatColor(themeSection.getCommandColor());
		this.splitterColor = parseChatColor(themeSection.getSplitterColor());
		this.descriptionColor = parseChatColor(themeSection.getDescriptionColor());
	}

	private ChatColor parseChatColor(String chatColorName) {
		ChatColor chatColor = null;

		try {
			chatColor = ChatColor.valueOf(chatColorName.toUpperCase());
		} catch(Exception ex) {

		}

		return chatColor == null ? ChatColor.WHITE : chatColor;
	}

	public static class ThemeSection extends KConfigurationSection {
		private @Getter String commandColor;
		private @Getter String splitterColor;
		private @Getter String descriptionColor;

		public ThemeSection(KConfiguration config) {
			super(config, "theme");
		}
	}
}