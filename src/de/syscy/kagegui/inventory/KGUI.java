package de.syscy.kagegui.inventory;

import java.util.ArrayList;
import java.util.List;

import de.syscy.kagegui.IInventoryGUI;
import de.syscy.kagegui.IInventoryWrapper;
import de.syscy.kagegui.KageGUI;
import de.syscy.kagegui.inventory.component.KComponent;
import de.syscy.kagegui.inventory.container.KPanel;
import de.syscy.kagegui.inventory.container.KTabbedPanel;
import de.syscy.kagegui.inventory.listener.GUICloseListener;
import de.syscy.kagegui.util.Util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import lombok.Getter;
import lombok.Setter;

public class KGUI implements IInventoryGUI<KComponent> {
	public static final int HOPPER_INVENTORY_SIZE = 5;

	protected @Getter Player player;
	protected @Getter Inventory bukkitInventory;
	protected @Getter IInventoryWrapper inventoryWrapper;

	private @Getter boolean dirty = true;

	protected List<GUICloseListener> closeListeners = new ArrayList<>();
	protected @Getter IContainer container;

	protected @Getter @Setter String title = "GUI";
	protected @Getter int size = 9;
	protected @Getter boolean open = false;
	protected @Getter boolean hidden = false;

	/*
	 * 1 means it gets updated every 5 ticks, 2 -> every 10 ticks, 3 -> every 15 ticks...
	 */
	protected @Getter @Setter int updateTick = 2;

	public KGUI() {
		this(new KPanel());
	}

	public KGUI(IContainer container) {
		this.container = container;
		this.container.init(this);
	}

	public void show(Player player) {
		open = true;
		hidden = false;

		this.player = player;

		if(size == HOPPER_INVENTORY_SIZE) {
			bukkitInventory = Bukkit.createInventory(player, InventoryType.HOPPER, title);
		} else {
			bukkitInventory = Bukkit.createInventory(player, size, title);
		}

		markDirty();

		inventoryWrapper = new KInventoryWrapper(this, bukkitInventory);
		player.openInventory(bukkitInventory);

		container.render();
		inventoryWrapper.flush(false);
		inventoryWrapper.sendBuffer();
	}

	@Override
	public void add(KComponent component) {
		container.add(component);

		component.setGui(this);
	}

	@Override
	public void update() {
		container.update();
	}

	@Override
	public void render() {
		if(dirty) {
			container.render();

			inventoryWrapper.flush(true);
		}

		dirty = false;
	}

	public void close() {
		player.closeInventory();

		//Fixes a bug where when a player closes the inventory by clicking a button, the click event is also forwarded to the current crafting gui
		KageGUI.setCraftingGuiInteractBlock(2);
	}

	public void hide() {
		hidden = true;
		player.closeInventory();
	}

	public void unhide() {
		hidden = false;
		KageGUI.showGUI(this, player);
	}

	@Override
	public void markDirty() {
		dirty = true;
	}

	public void addCloseListener(GUICloseListener listener) {
		closeListeners.add(listener);
	}

	public void onClick(InventoryClickEvent event) {
		if(event.getSlot() >= 0 && event.getRawSlot() < size) {
			event.setCancelled(true);

			int[] coordinates = Util.toXYCoordinate(event.getSlot());

			container.onClick(event, coordinates[0], coordinates[1]);
		}
	}

	public void onClose(InventoryCloseEvent event) {
		open = false;
		KageGUI.getCurrentGuis().remove(player.getPlayer());

		if(!hidden) {
			for(GUICloseListener listener : closeListeners) {
				listener.onClose(this);
			}
		}
	}

	/**
	 * Set the size of the inventory
	 * @param size The size (actual slot count)
	 */
	public void setSize(int size) {
		if(size <= 5) {
			this.size = 5;
		} else {
			this.size = size % 9 == 0 ? size : (int) Math.ceil((float) size / 9.0f) * 9;
		}
	}

	/**
	 * Sets the height of the inventory
	 * @param height The height (not slot count)
	 */
	public void setHeight(int height) {
		if(container instanceof KTabbedPanel) {
			height++;
		}

		setSize(height * 9);
	}
}