package de.syscy.kagegui.inventory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.comphenix.packetwrapper.WrapperPlayServerSetSlot;
import com.comphenix.packetwrapper.WrapperPlayServerWindowItems;

import de.syscy.kagecore.KageCore;
import de.syscy.kagecore.util.reflect.Reflect;
import de.syscy.kagegui.IInventoryWrapper;
import lombok.Getter;

public class KInventoryWrapper implements IInventoryWrapper {
	private final KGUI gui;
	private final Inventory bukkitInventory;

	private @Getter boolean dirty = true;
	private boolean cleared = true;

	private ItemStack[] buffer;
	private @Getter ItemStack[] lastItems;

	public KInventoryWrapper(KGUI gui, Inventory inventory) {
		this.gui = gui;
		bukkitInventory = inventory;

		buffer = new ItemStack[getSize()];
		lastItems = new ItemStack[getSize()];
	}

	@Override
	public void setItem(int slot, ItemStack itemStack) {
		buffer[slot] = itemStack;
	}

	@Override
	public void clear() {
		for(int i = 0; i < getSize(); i++) {
			buffer[i] = null;
		}

		gui.markDirty();
	}

	@Override
	public void flush(boolean sendToPlayer) {
		int windowID = Reflect.on(gui.getPlayer()).call("getHandle").field("activeContainer").get("windowId");

		for(int i = 0; i < getSize(); i++) {
			if(!Objects.equals(buffer[i], lastItems[i])) {
				bukkitInventory.setItem(i, buffer[i]);

				if(sendToPlayer && !cleared) {
					WrapperPlayServerSetSlot packet = new WrapperPlayServerSetSlot();

					packet.setWindowId(windowID);
					packet.setSlot(i + 1);
					packet.setSlotData(buffer[i]);

					try {
						KageCore.getProtocolManager().sendServerPacket(gui.getPlayer(), packet.getHandle());
					} catch(InvocationTargetException ex) {
						ex.printStackTrace();
					}
				}
			}
		}

		if(sendToPlayer && cleared) {
			sendBuffer();
		}

		lastItems = buffer.clone();

		buffer = new ItemStack[buffer.length];
	}

	@Override
	public void sendBuffer() {
		int windowID = Reflect.on(gui.getPlayer()).call("getHandle").field("activeContainer").get("windowId");

		WrapperPlayServerWindowItems packet = new WrapperPlayServerWindowItems();

		packet.setWindowId(windowID);
		packet.setSlotData(getBufferList());

		try {
			KageCore.getProtocolManager().sendServerPacket(gui.getPlayer(), packet.getHandle());
		} catch(InvocationTargetException ex) {
			ex.printStackTrace();
		}
	}

	private List<ItemStack> getBufferList() {
		List<ItemStack> bufferList = new ArrayList<>();

		for(ItemStack itemStack : buffer) {
			if(itemStack == null) {
				bufferList.add(new ItemStack(Material.AIR));
			} else {
				bufferList.add(itemStack);
			}
		}

		return bufferList;
	}

	public int getSize() {
		return bukkitInventory.getSize();
	}
}