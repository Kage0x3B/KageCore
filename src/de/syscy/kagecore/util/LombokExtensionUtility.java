package de.syscy.kagecore.util;

import java.lang.reflect.InvocationTargetException;

import de.syscy.kagecore.KageCore;
import de.syscy.kagecore.protocol.ProtocolUtil;
import de.syscy.kagecore.translation.Translator;
import de.syscy.kagecore.versioncompat.reflect.Reflect;
import de.syscy.kagecore.versioncompat.reflect.ReflectException;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.comphenix.packetwrapper.AbstractPacket;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.mysql.jdbc.StringUtils;

public class LombokExtensionUtility {
	private static final String serverVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

	public static boolean isNullOrEmpty(String string) {
		return StringUtils.isNullOrEmpty(string);
	}

	public static String improveLook(String string) {
		return Util.improveStringLook(string);
	}

	public static ByteArrayDataInput toByteStream(byte[] data) {
		return ByteStreams.newDataInput(data);
	}

	public static void instantRespawn(PlayerDeathEvent event) {
		event.getEntity().setHealth(20);
		event.getEntity().getActivePotionEffects().clear();
		event.getEntity().setFoodLevel(20);
		event.getEntity().setFireTicks(0);
		event.getEntity().teleport(event.getEntity().getLocation());
	}

	public static Player toBukkitPlayer(Player player) {
		return player instanceof AbstractPlayerWrapper ? ((AbstractPlayerWrapper) player).getBukkitPlayer() : player;
	}

	public static void sendPacket(AbstractPacket packetWrapper, Player player) {
		if(ProtocolUtil.getProtocolManager() != null) {
			try {
				ProtocolUtil.getProtocolManager().sendServerPacket(player, packetWrapper.getHandle());
			} catch(InvocationTargetException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void debug(Object object) {
		KageCore.debugMessage(object.toString(), 3);
	}

	public static Object getHandle(Object object) {
		try {
			return Reflect.on(object).call("getHandle").get();
		} catch(ReflectException ex) {
			KageCore.debugMessage(object.getClass().getSimpleName() + " has no getHandle() method!", 3);
		}

		return null;
	}

	public static Reflect getNMSReflect(String name) {
		return Reflect.on("net.minecraft.server." + serverVersion + "." + name);
	}

	public static Reflect getCraftBukkitReflect(String name) {
		return Reflect.on("org.bukkit.craftbukkit." + serverVersion + "." + name);
	}

	public static String getVersionString(Server bukkitServer) {
		return serverVersion;
	}

	public static void sendTr(String key, CommandSender sender, Object... args) {
		Translator.sendMessage(sender, key, args);
	}

	public static void sendTr(String key, Player player, Object... args) {
		Translator.sendMessage(player, key, args);
	}

	public static String translate(String key, CommandSender sender, Object... args) {
		return Translator.translate(sender, key, args);
	}

	public static String translate(String key, Player player, Object... args) {
		return Translator.translate(player, key, args);
	}
}