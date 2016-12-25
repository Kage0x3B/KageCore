package de.syscy.kagecore.translation;

import java.io.IOException;

import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;

import com.google.common.io.ByteArrayDataOutput;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.handler.codec.EncoderException;
import net.minecraft.server.v1_11_R1.Item;
import net.minecraft.server.v1_11_R1.ItemStack;
import net.minecraft.server.v1_11_R1.NBTCompressedStreamTools;
import net.minecraft.server.v1_11_R1.NBTReadLimiter;
import net.minecraft.server.v1_11_R1.NBTTagCompound;

public class NetworkUtil {
	public static void writeItemStack(ByteArrayDataOutput out, org.bukkit.inventory.ItemStack bukkitItemStack) {
		ItemStack itemStack = CraftItemStack.asNMSCopy(bukkitItemStack);

		if(itemStack == null || itemStack.getItem() == null) {
			out.writeShort(-1);
		} else {
			out.writeShort(Item.getId(itemStack.getItem()));
			out.writeByte(itemStack.getCount());
			out.writeShort(itemStack.getData());

			NBTTagCompound nbtTagCompound = null;

			if(itemStack.getItem().usesDurability() || itemStack.getItem().q()) {
				itemStack = itemStack.cloneItemStack();
				CraftItemStack.setItemMeta(itemStack, CraftItemStack.getItemMeta(itemStack));
				nbtTagCompound = itemStack.getTag();
			}

			writeNBT(out, nbtTagCompound);
		}
	}

	public static void writeNBT(ByteArrayDataOutput out, NBTTagCompound nbttagcompound) {
		if(nbttagcompound == null) {
			out.writeByte(0);
		} else {
			try {
				NBTCompressedStreamTools.a(nbttagcompound, out);
			} catch(Exception ex) {
				throw new EncoderException(ex);
			}
		}
	}

	public static org.bukkit.inventory.ItemStack readItemStack(ByteBuf in) {
		ItemStack itemstack = null;
		short itemID = in.readShort();

		if(itemID >= 0) {
			byte amount = in.readByte();
			short data = in.readShort();
			itemstack = new ItemStack(Item.getById(itemID), (int) amount, (int) data);
			itemstack.setTag(readNBT(in));

			if(itemstack.getTag() != null) {
				CraftItemStack.setItemMeta(itemstack, CraftItemStack.getItemMeta(itemstack));
			}
		}

		return CraftItemStack.asCraftMirror(itemstack);
	}

	public static NBTTagCompound readNBT(ByteBuf in) {
		int i = in.readerIndex();
		byte firstByte = in.readByte();

		if(firstByte == 0) {
			return null;
		}

		in.readerIndex(i);

		try {
			return NBTCompressedStreamTools.a(new ByteBufInputStream(in), new NBTReadLimiter(0x200000));
		} catch(IOException ioexception) {
			throw new EncoderException(ioexception);
		}
	}
}