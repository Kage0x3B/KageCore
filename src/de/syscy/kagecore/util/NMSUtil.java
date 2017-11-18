package de.syscy.kagecore.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(LombokExtensionUtility.class)
public class NMSUtil {
	public static void sendPacket(Player player, Object packet) {
		try {
			Object handle = player.getClass().getMethod("getHandle").invoke(player);
			Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
			playerConnection.getClass().getMethod("sendPacket", "Packet".getNMSClass()).invoke(playerConnection, packet);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void sendActionBar(Player player, String message) {
		try {
			Object chatComponentText = "ChatComponentText".getNMSClass().getConstructor(new Class[] { String.class }).newInstance(new Object[] { message });
			Object chatPacket = "PacketPlayOutChat".getNMSClass().getConstructor(new Class[] { "IChatBaseComponent".getNMSClass(), Byte.TYPE }).newInstance(new Object[] { chatComponentText, (byte) 2 });

			sendPacket(player, chatPacket);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void sendTitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle) {
		try {
			Object object;
			Object chatTitle;
			Object chatSubtitle;
			Constructor<?> subtitleConstructor;
			Object titlePacket;
			Object subtitlePacket;

			if(title != null) {
				object = "PacketPlayOutTitle".getNMSClass().getDeclaredClasses()[0].getField("TIMES").get((Object) null);
				chatTitle = "IChatBaseComponent".getNMSClass().getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke((Object) null, new Object[] { "{\"text\":\"" + title + "\"}" });
				subtitleConstructor = "PacketPlayOutTitle".getNMSClass().getConstructor(new Class[] { "PacketPlayOutTitle".getNMSClass().getDeclaredClasses()[0], "IChatBaseComponent".getNMSClass(), Integer.TYPE, Integer.TYPE, Integer.TYPE });
				titlePacket = subtitleConstructor.newInstance(new Object[] { object, chatTitle, fadeIn, stay, fadeOut });
				sendPacket(player, titlePacket);

				object = "PacketPlayOutTitle".getNMSClass().getDeclaredClasses()[0].getField("TITLE").get((Object) null);
				chatTitle = "IChatBaseComponent".getNMSClass().getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke((Object) null, new Object[] { "{\"text\":\"" + title + "\"}" });
				subtitleConstructor = "PacketPlayOutTitle".getNMSClass().getConstructor(new Class[] { "PacketPlayOutTitle".getNMSClass().getDeclaredClasses()[0], "IChatBaseComponent".getNMSClass() });
				titlePacket = subtitleConstructor.newInstance(new Object[] { object, chatTitle });
				sendPacket(player, titlePacket);
			}

			if(subtitle != null) {
				object = "PacketPlayOutTitle".getNMSClass().getDeclaredClasses()[0].getField("TIMES").get((Object) null);
				chatSubtitle = "IChatBaseComponent".getNMSClass().getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke((Object) null, new Object[] { "{\"text\":\"" + title + "\"}" });
				subtitleConstructor = "PacketPlayOutTitle".getNMSClass().getConstructor(new Class[] { "PacketPlayOutTitle".getNMSClass().getDeclaredClasses()[0], "IChatBaseComponent".getNMSClass(), Integer.TYPE, Integer.TYPE, Integer.TYPE });
				subtitlePacket = subtitleConstructor.newInstance(new Object[] { object, chatSubtitle, fadeIn, stay, fadeOut });
				sendPacket(player, subtitlePacket);

				object = "PacketPlayOutTitle".getNMSClass().getDeclaredClasses()[0].getField("SUBTITLE").get((Object) null);
				chatSubtitle = "IChatBaseComponent".getNMSClass().getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke((Object) null, new Object[] { "{\"text\":\"" + subtitle + "\"}" });
				subtitleConstructor = "PacketPlayOutTitle".getNMSClass().getConstructor(new Class[] { "PacketPlayOutTitle".getNMSClass().getDeclaredClasses()[0], "IChatBaseComponent".getNMSClass(), Integer.TYPE, Integer.TYPE, Integer.TYPE });
				subtitlePacket = subtitleConstructor.newInstance(new Object[] { object, chatSubtitle, fadeIn, stay, fadeOut });
				sendPacket(player, subtitlePacket);
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void clearTitle(Player player) {
		sendTitle(player, 0, 0, 0, "", "");
	}

	public static void sendTabTitle(Player player, String header, String footer) {
		if(header == null) {
			header = "";
		}
		header = ChatColor.translateAlternateColorCodes('&', header);

		if(footer == null) {
			footer = "";
		}
		footer = ChatColor.translateAlternateColorCodes('&', footer);

		header = header.replaceAll("%player%", player.getDisplayName());
		footer = footer.replaceAll("%player%", player.getDisplayName());

		try {
			Object tabHeader = "IChatBaseComponent".getNMSClass().getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + header + "\"}");
			Object tabFooter = "IChatBaseComponent".getNMSClass().getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + footer + "\"}");
			Constructor<?> titleConstructor = "PacketPlayOutPlayerListHeaderFooter".getNMSClass().getConstructor("IChatBaseComponent".getNMSClass());
			Object packet = titleConstructor.newInstance(tabHeader);
			Field field = packet.getClass().getDeclaredField("b");
			field.setAccessible(true);
			field.set(packet, tabFooter);
			sendPacket(player, packet);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}