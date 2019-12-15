package de.syscy.kagecore.util;

import org.bukkit.entity.Player;

public class ExperienceUtil {
	public static void setTotalExperience(Player player, int exp) {
		clearExp(player);

		player.giveExp(exp);
	}

	public static void clearExp(Player player) {
		player.setLevel(-1);
		player.setExp(0);
		player.setTotalExperience(0);
	}
}