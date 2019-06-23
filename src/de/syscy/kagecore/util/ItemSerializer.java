package de.syscy.kagecore.util;

import lombok.experimental.ExtensionMethod;
import net.minecraft.server.v1_14_R1.NBTCompressedStreamTools;
import net.minecraft.server.v1_14_R1.NBTTagCompound;
import net.minecraft.server.v1_14_R1.NBTTagList;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.math.BigInteger;

@ExtensionMethod(LombokExtensionUtility.class)
public final class ItemSerializer {
	public final static String serializeItemStacks(final ItemStack[] itemStacks) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
		NBTTagList nbtTagList = new NBTTagList();

		for(ItemStack itemStack : itemStacks) {
			NBTTagCompound outputObject = new NBTTagCompound();
			net.minecraft.server.v1_14_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);

			if(nmsItemStack != null) {
				nmsItemStack.save(outputObject);
			}

			nbtTagList.add(outputObject);
		}

		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		nbtTagCompound.set("items", nbtTagList);

		try {
			NBTCompressedStreamTools.a(nbtTagCompound, (DataOutput) dataOutputStream);
		} catch(IOException ex) {
			ex.printStackTrace();
		}

		return new BigInteger(1, byteArrayOutputStream.toByteArray()).toString(32);
	}

	public static ItemStack[] deserializeItemStacks(String data) {
		if(data == null || data.isEmpty()) {
			return new ItemStack[0];
		}

		ByteArrayInputStream inputStream = new ByteArrayInputStream(new BigInteger(data, 32).toByteArray());

		NBTTagList itemList;

		try {
			itemList = NBTCompressedStreamTools.a(new DataInputStream(inputStream)).getList("items", 10); //The 10 stands for the type id of the NBT object the list contains, in this case 10 for NBTTagCompound
		} catch(IOException ex) {
			ex.printStackTrace();

			return new ItemStack[0];
		}

		ItemStack[] items = new ItemStack[itemList.size()];

		for(int i = 0; i < itemList.size(); i++) {
			NBTTagCompound inputObject = (NBTTagCompound) itemList.get(i);

			if(!inputObject.isEmpty()) {

				items[i] = CraftItemStack.asCraftMirror(net.minecraft.server.v1_14_R1.ItemStack.a(inputObject));
			}
		}

		return items;
	}
}