package de.syscy.kagecore.util;

import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerUtil {
	public static void instantRespawn(PlayerDeathEvent event) {
		event.getEntity().setHealth(20);
		event.getEntity().getActivePotionEffects().clear();
		event.getEntity().setFoodLevel(20);
		event.getEntity().setFireTicks(0);
		event.getEntity().teleport(event.getEntity().getLocation());
	}
}