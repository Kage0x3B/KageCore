package de.syscy.bguilib;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import de.syscy.bguilib.components.BGComponent;
import de.syscy.bguilib.components.BGInventory;
import de.syscy.bguilib.components.listener.GUICloseListener;
import de.syscy.bguilib.container.BGContainer;
import de.syscy.bguilib.container.BGPanel;
import de.syscy.bguilib.container.BGTabbedPanel;
import de.syscy.bguilib.util.Util;
import lombok.Getter;
import lombok.Setter;

public class BGGUI {
	public static final int HOPPER_INVENTORY_SIZE = 5;

	protected @Getter Player player;
	protected @Getter Inventory inventory;
	protected @Getter BGInventory bgInventory;
	protected List<GUICloseListener> closeListeners = new ArrayList<>();
	protected @Getter BGContainer container;
	protected @Getter @Setter String title;
	protected @Getter int size;
	protected @Getter boolean open;
	protected @Getter boolean hidden;

	public BGGUI() {
		this(new BGPanel());
	}

	public BGGUI(BGContainer container) {
		this.title = "GUI";
		this.size = 9;
		this.open = false;
		this.hidden = false;
		this.container = container;
		this.container.init(this);
	}

	protected void show(Player player) {
		this.hidden = false;
		this.open = true;
		this.player = player;

		if(size == HOPPER_INVENTORY_SIZE) {
			this.inventory = Bukkit.createInventory(player, InventoryType.HOPPER, this.title);
		} else {
			this.inventory = Bukkit.createInventory(player, this.size, this.title);
		}

		this.bgInventory = new BGInventory(inventory);
		player.openInventory(this.inventory);
		this.container.render();
	}

	public void update() {
		this.container.update();
	}

	public boolean render() {
		this.container.render();
		return this.bgInventory.flush();
	}

	public void close() {
		this.player.closeInventory();
	}

	public void hide() {
		this.hidden = true;
		this.player.closeInventory();
	}

	public void unhide() {
		this.hidden = false;
		BGUILib.showGUI(this, this.getPlayer());
	}

	public void add(BGComponent component) {
		this.container.add(component);
		component.init(this);
	}

	public void addCloseListener(GUICloseListener listener) {
		this.closeListeners.add(listener);
	}

	public void onClick(InventoryClickEvent event) {
		event.setCancelled(true);

		if(event.getSlot() >= 0 && event.getRawSlot() < this.size) {
			int[] coordinates = Util.toXYCoordinate(event.getSlot());
			this.container.onClick(coordinates[0], coordinates[1]);
		}
	}

	public void onClose(InventoryCloseEvent event) {
		this.open = false;
		BGUILib.getCurrentGuis().remove(this.player);

		if(!this.hidden) {
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
		if(this.container instanceof BGTabbedPanel) {
			height++;
		}

		setSize(height * 9);
	}

	public void setContainer(BGContainer container) {
		this.container = container;
		this.container.init(this);
	}
}