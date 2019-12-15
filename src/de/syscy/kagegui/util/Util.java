package de.syscy.kagegui.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Util {
	public static int toSlotCoordinate(int x, int y) {
		return x + y * 9;
	}

	public static int[] toXYCoordinate(int slot) {
		return new int[] { Math.round((float) (slot % 9)), slot / 9 };
	}

	public static boolean pointInBoundingBox(int pointX, int pointY, int bbX, int bbY, int bbWidth, int bbHeight) {
		return pointX >= bbX && pointY >= bbY && pointX < bbX + bbWidth && pointY < bbY + bbHeight;
	}

	public static ItemStack itemStackFromString(String itemStackString) {
		return new ItemStack(Material.getMaterial(itemStackString));
	}

	public static String itemStackToString(ItemStack itemStack) {
		return itemStack.getType().name();
	}
}