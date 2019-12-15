package de.syscy.kagecore.util;

import java.lang.reflect.InvocationTargetException;

import de.syscy.kagecore.protocol.ProtocolUtil;

import org.bukkit.entity.Player;

import com.comphenix.packetwrapper.WrapperPlayServerGameStateChange;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SkyUtil {
	@Accessors(fluent = true)
	private static final @Getter(lazy = true) Unsafe unsafe = new Unsafe();

	public static void sendSkyColorChange(Player player, SkyColor skyColor) {
		unsafe().sendSkyColorChangeUnsafe(player, skyColor.getInternalValue());
	}

	@RequiredArgsConstructor
	public static enum SkyColor implements IEnum {
		NORMAL(0), RAIN(1), BROWN(2), RED(3), DARKER_RED(4), BLACK_RED(5);

		private final @Getter float internalValue;
	}

	private class Unsafe {
		public void sendSkyColorChangeUnsafe(Player player, float skyColorValue) {
			WrapperPlayServerGameStateChange packet = new WrapperPlayServerGameStateChange();
			packet.setReason(7);
			packet.setValue(skyColorValue);

			try {
				ProtocolUtil.getProtocolManager().sendServerPacket(player, packet.getHandle());
			} catch(InvocationTargetException ex) {
				ex.printStackTrace();
			}
		}
	}
}