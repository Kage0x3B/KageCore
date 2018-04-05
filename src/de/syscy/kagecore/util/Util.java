package de.syscy.kagecore.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.syscy.kagecore.KageCore;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;

import com.comphenix.packetwrapper.AbstractPacket;

import lombok.Getter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Util {
	private static final @Getter Random random = new Random();

	public boolean isNumber(String string) {
		NumberFormat formatter = NumberFormat.getInstance();
		ParsePosition pos = new ParsePosition(0);
		formatter.parse(string, pos);

		return string.length() == pos.getIndex();
	}

	public int clamp(int value, int min, int max) {
		return Math.max(min, Math.min(max, value));
	}

	public double clamp(double value, double min, double max) {
		return Math.max(min, Math.min(max, value));
	}

	public float clamp(float value, float min, float max) {
		return Math.max(min, Math.min(max, value));
	}

	public void copy(InputStream inputStream, File file) {
		try {
			OutputStream outputStream = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int length;

			while((length = inputStream.read(buffer)) > 0) {
				outputStream.write(buffer, 0, length);
			}

			outputStream.close();
			inputStream.close();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	public Entity getLookingAtEntity(Player player, int maxRange) {
		List<Entity> nearbyEntities = player.getNearbyEntities(maxRange, maxRange, maxRange);
		List<LivingEntity> nearbyLivingEntities = new ArrayList<>();

		for(Entity entity : nearbyEntities) {
			if(entity instanceof LivingEntity) {
				nearbyLivingEntities.add((LivingEntity) entity);
			}
		}

		BlockIterator blockIterator = new BlockIterator(player, maxRange);

		Block block;
		Location location;
		int blockX, blockY, blockZ;
		double entityX, entityY, entityZ;

		while(blockIterator.hasNext()) {
			block = blockIterator.next();
			blockX = block.getX();
			blockY = block.getY();
			blockZ = block.getZ();

			for(LivingEntity entity : nearbyLivingEntities) {
				location = entity.getLocation();
				entityX = location.getX();
				entityY = location.getY();
				entityZ = location.getZ();

				if(blockX - 0.75 <= entityX && entityX <= blockX + 1.75 && blockZ - 0.75 <= entityZ && entityZ <= blockZ + 1.75 && blockY - 1 <= entityY && entityY <= blockY + 2.5) {
					return entity;
				}
			}
		}

		return null;
	}

	public List<ItemStack> splitUpItemStack(ItemStack inputItemStack) {
		List<ItemStack> resultItemStacks = new ArrayList<>();

		int maxStackSize = inputItemStack.getType().getMaxStackSize();
		int maxSizeStacks = inputItemStack.getAmount() / maxStackSize;
		int restAmount = inputItemStack.getAmount() % maxSizeStacks;

		for(int i = 0; i < maxSizeStacks; i++) {
			ItemStack itemStack = inputItemStack.clone();
			itemStack.setAmount(maxStackSize);
			resultItemStacks.add(itemStack);
		}

		ItemStack restItemStack = inputItemStack.clone();
		restItemStack.setAmount(restAmount);
		resultItemStacks.add(restItemStack);

		return resultItemStacks;
	}

	public void printValues(AbstractPacket packet) {
		KageCore.debugMessage(packet.getClass().getSimpleName() + " values:");

		for(Object value : packet.getHandle().getModifier().getValues()) {
			KageCore.debugMessage(value.toString() + " (" + value.getClass().getSimpleName() + ")");
		}
	}

	public String improveStringLook(String string) {
		string = string.replaceAll("_", " ");

		String[] stringSplit = string.split(" ");
		StringBuilder stringBuilder = new StringBuilder(string.length());

		for(int i = 0; i < stringSplit.length; i++) {
			String part = stringSplit[i];

			stringBuilder.append(part.substring(0, 1).toUpperCase() + part.substring(1, part.length()).toLowerCase() + (i == stringSplit.length - 1 ? "" : " "));
		}

		return stringBuilder.toString();
	}
}