package de.syscy.kagecore.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import de.syscy.kagecore.versioncompat.reflect.Reflect;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(LombokExtensionUtility.class)
public class NMSUtil {
	//TODO: Update to use Reflect!!
	public static void sendPacket(Player player, Object packet) {
		Reflect.on(player.getHandle()).field("playerConnection").call("sendPacket", packet);
	}

	public static void sendActionBar(Player player, String message) {
		try {
			Object chatComponentText = "ChatComponentText".getNMSReflect().type().getConstructor(new Class[] { String.class }).newInstance(new Object[] { message });
			Object chatPacket = "PacketPlayOutChat".getNMSReflect().type().getConstructor(new Class[] { "IChatBaseComponent".getNMSReflect().type(), Byte.TYPE }).newInstance(new Object[] { chatComponentText, (byte) 2 });

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
				object = "PacketPlayOutTitle".getNMSReflect().type().getDeclaredClasses()[0].getField("TIMES").get((Object) null);
				chatTitle = "IChatBaseComponent".getNMSReflect().type().getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke((Object) null, new Object[] { "{\"text\":\"" + title + "\"}" });
				subtitleConstructor = "PacketPlayOutTitle".getNMSReflect().type().getConstructor(new Class[] { "PacketPlayOutTitle".getNMSReflect().type().getDeclaredClasses()[0], "IChatBaseComponent".getNMSReflect().type(), Integer.TYPE, Integer.TYPE, Integer.TYPE });
				titlePacket = subtitleConstructor.newInstance(new Object[] { object, chatTitle, fadeIn, stay, fadeOut });
				sendPacket(player, titlePacket);

				object = "PacketPlayOutTitle".getNMSReflect().type().getDeclaredClasses()[0].getField("TITLE").get((Object) null);
				chatTitle = "IChatBaseComponent".getNMSReflect().type().getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke((Object) null, new Object[] { "{\"text\":\"" + title + "\"}" });
				subtitleConstructor = "PacketPlayOutTitle".getNMSReflect().type().getConstructor(new Class[] { "PacketPlayOutTitle".getNMSReflect().type().getDeclaredClasses()[0], "IChatBaseComponent".getNMSReflect().type() });
				titlePacket = subtitleConstructor.newInstance(new Object[] { object, chatTitle });
				sendPacket(player, titlePacket);
			}

			if(subtitle != null) {
				object = "PacketPlayOutTitle".getNMSReflect().type().getDeclaredClasses()[0].getField("TIMES").get((Object) null);
				chatSubtitle = "IChatBaseComponent".getNMSReflect().type().getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke((Object) null, new Object[] { "{\"text\":\"" + title + "\"}" });
				subtitleConstructor = "PacketPlayOutTitle".getNMSReflect().type().getConstructor(new Class[] { "PacketPlayOutTitle".getNMSReflect().type().getDeclaredClasses()[0], "IChatBaseComponent".getNMSReflect().type(), Integer.TYPE, Integer.TYPE, Integer.TYPE });
				subtitlePacket = subtitleConstructor.newInstance(new Object[] { object, chatSubtitle, fadeIn, stay, fadeOut });
				sendPacket(player, subtitlePacket);

				object = "PacketPlayOutTitle".getNMSReflect().type().getDeclaredClasses()[0].getField("SUBTITLE").get((Object) null);
				chatSubtitle = "IChatBaseComponent".getNMSReflect().type().getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke((Object) null, new Object[] { "{\"text\":\"" + subtitle + "\"}" });
				subtitleConstructor = "PacketPlayOutTitle".getNMSReflect().type().getConstructor(new Class[] { "PacketPlayOutTitle".getNMSReflect().type().getDeclaredClasses()[0], "IChatBaseComponent".getNMSReflect().type(), Integer.TYPE, Integer.TYPE, Integer.TYPE });
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
			Object tabHeader = "IChatBaseComponent".getNMSReflect().type().getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + header + "\"}");
			Object tabFooter = "IChatBaseComponent".getNMSReflect().type().getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + footer + "\"}");
			Constructor<?> titleConstructor = "PacketPlayOutPlayerListHeaderFooter".getNMSReflect().type().getConstructor("IChatBaseComponent".getNMSReflect().type());
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