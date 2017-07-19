package de.syscy.kagecore.factory.itemstack.versioncompat;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import de.syscy.kagecore.factory.itemstack.ItemFactoryNMS;
import net.minecraft.server.v1_12_R1.MojangsonParser;

public class ItemFactoryNMS_v1_12_R1 implements ItemFactoryNMS {
	@Override
	public ItemStack createItemStack(Material material, int data, String nbt) throws Exception {
		net.minecraft.server.v1_12_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(new ItemStack(material));

		nmsItemStack.setData(data);

		if(nbt != null && !nbt.isEmpty()) {
			nmsItemStack.setTag(MojangsonParser.parse(nbt));
		}

		return CraftItemStack.asCraftMirror(nmsItemStack);
	}
}