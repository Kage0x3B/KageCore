package de.syscy.kagegui.crafting;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;

import de.syscy.kagegui.IInventoryGUI;
import de.syscy.kagegui.IInventoryWrapper;
import de.syscy.kagegui.KageGUI;
import lombok.Getter;

public class KCraftingGUI implements IInventoryGUI<KCraftingComponent> {
	public static final int RESULT_SLOT = 0;
	public static final int UPPER_LEFT_SLOT = 1;
	public static final int UPPER_RIGHT_SLOT = 2;
	public static final int LOWER_LEFT_SLOT = 3;
	public static final int LOWER_RIGHT_SLOT = 4;

	protected @Getter Player player;
	protected @Getter CraftingInventory bukkitInventory;
	protected @Getter IInventoryWrapper inventoryWrapper;

	private @Getter boolean dirty = true;

	protected KCraftingComponent[] components = new KCraftingComponent[6];

	public void show(Player player) {
		this.player = player;

		Inventory inventory = player.getOpenInventory().getTopInventory();

		if(!(inventory instanceof CraftingInventory)) {
			KageGUI.removeCraftingGUI(player);

			return;
		}

		bukkitInventory = (CraftingInventory) inventory;

		inventoryWrapper = new KCraftingInventoryWrapper(this);
	}

	@Override
	public void add(KCraftingComponent component) {
		if(component.getCraftingSlot() >= 0 && component.getCraftingSlot() < 5) {
			components[component.getCraftingSlot()] = component;

			component.setGui(this);
		}
	}

	@Override
	public void update() {
		for(KCraftingComponent component : components) {
			if(component != null) {
				component.update();
			}
		}
	}

	@Override
	public void render() {
		for(KCraftingComponent component : components) {
			if(component != null) {
				component.render(inventoryWrapper);
			}
		}

		inventoryWrapper.flush(true);
	}

	public void close() {
		KageGUI.removeCraftingGUI(player);
	}

	@Override
	public void markDirty() {

	}

	public void onClick(InventoryClickEvent event, int slot) {
		if(components[slot] != null) {
			components[slot].onClick(event, player, slot, 0);
		}
	}
}