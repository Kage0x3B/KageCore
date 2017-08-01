package de.syscy.kagegui.crafting;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.comphenix.packetwrapper.WrapperPlayServerSetSlot;

import de.syscy.kagecore.protocol.ProtocolUtil;
import de.syscy.kagegui.IInventoryWrapper;
import lombok.Getter;

public class KCraftingInventoryWrapper implements IInventoryWrapper {
	private final ItemStack background = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);

	private final KCraftingGUI gui;

	private ItemStack[] buffer;
	private @Getter ItemStack[] lastItems;

	public KCraftingInventoryWrapper(KCraftingGUI gui) {
		this.gui = gui;

		buffer = new ItemStack[getSize()];
		lastItems = new ItemStack[getSize()];

		for(int i = 0; i < getSize(); i++) {
			buffer[i] = background;
		}
	}

	@Override
	public void setItem(int slot, ItemStack itemStack) {
		buffer[slot] = itemStack == null ? background : itemStack;
	}

	@Override
	public void clear() {
		for(int i = 0; i < getSize(); i++) {
			buffer[i] = background;
		}
	}

	@Override
	public void flush(boolean sendToPlayer) {
		for(int i = getSize() - 1; i >= 0; i--) {
			if(buffer[i] != background || i == 0) {
				if(sendToPlayer) {
					WrapperPlayServerSetSlot packet = new WrapperPlayServerSetSlot();

					packet.setWindowId(0);
					packet.setSlot(i);
					packet.setSlotData(buffer[i]);

					try {
						ProtocolUtil.getProtocolManager().sendServerPacket(gui.getPlayer(), packet.getHandle());
					} catch(InvocationTargetException ex) {
						ex.printStackTrace();
					}
				}
			}
		}

		lastItems = buffer.clone();
	}

	@Override
	public void sendBuffer() {

	}

	public int getSize() {
		return 5;
	}
}