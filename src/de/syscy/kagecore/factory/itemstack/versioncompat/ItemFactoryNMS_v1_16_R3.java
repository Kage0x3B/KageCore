package de.syscy.kagecore.factory.itemstack.versioncompat;

import de.syscy.kagecore.factory.itemstack.IItemFactoryNMS;
import net.minecraft.server.v1_16_R3.MojangsonParser;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class ItemFactoryNMS_v1_16_R3 implements IItemFactoryNMS {
	@Override
	public ItemStack createItemStack(Material material, int data, String nbt) throws Exception {
		net.minecraft.server.v1_16_R3.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(new ItemStack(material, 1, (short) 0, (byte) data));

		if(nbt != null && !nbt.isEmpty()) {
			nmsItemStack.setTag(MojangsonParser.parse(nbt));
		}

		return CraftItemStack.asCraftMirror(nmsItemStack);
	}
}