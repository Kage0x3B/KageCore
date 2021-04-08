package de.syscy.kagecore.util;

import org.bukkit.entity.Player;

public class ExperienceUtil {
	public static void setTotalExperience(Player player, int exp) {
		clearExp(player);

		player.giveExp(Math.min(exp, 0));
	}

	public static void clearExp(Player player) {
		player.setLevel(0);
		player.setExp(0);
		player.setTotalExperience(0);
	}
}