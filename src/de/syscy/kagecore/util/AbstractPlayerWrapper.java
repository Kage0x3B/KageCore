package de.syscy.kagecore.util;

import de.syscy.kagecore.versioncompat.reflect.Reflect;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import lombok.Getter;
import lombok.experimental.Delegate;

public abstract class AbstractPlayerWrapper implements Player {
	@Delegate(excludes = ProjectileSource.class)
	private final @Getter Player bukkitPlayer;
	private final @Getter Reflect playerReflect;

	public AbstractPlayerWrapper(Player bukkitPlayer) {
		this.bukkitPlayer = bukkitPlayer;
		playerReflect = Reflect.on(bukkitPlayer);
	}

	@Override
	public int hashCode() {
		return bukkitPlayer.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		return bukkitPlayer.equals(object);
	}

	public Object getHandle() {
		return playerReflect.call("getHandle").get();
	}

	@Override
	public <T extends Projectile> T launchProjectile(Class<? extends T> projectileClass) {
		return bukkitPlayer.launchProjectile(projectileClass);
	}

	@Override
	public <T extends Projectile> T launchProjectile(Class<? extends T> projectileClass, Vector velocity) {
		return bukkitPlayer.launchProjectile(projectileClass, velocity);
	}

	public static Player toBukkitPlayer(Player player) {
		return player instanceof AbstractPlayerWrapper ? ((AbstractPlayerWrapper) player).bukkitPlayer : player;
	}
}