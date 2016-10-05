package de.syscy.bguilib.components;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.Setter;

public class BGItemContainer extends BGComponent {
	private static final ItemStack AIR;

	static {
		AIR = new ItemStack(Material.AIR);
	}

	private ItemStack item;
	private @Getter @Setter boolean refill = true;
	private @Getter @Setter boolean empty = false;

	public BGItemContainer(int x, int y, ItemStack item) {
		super(x, y, 1, 1);

		this.item = item;
	}

	public void update() {

	}

	public void render(BGInventory inventory) {
		if(!empty) {
			this.renderItem(inventory, this.x, this.y, this.width, this.height, this.item.clone(), true);
		} else {
			this.renderItem(inventory, this.x, this.y, this.width, this.height, AIR.clone(), false);
		}
	}

	public void onClick(InventoryClickEvent event, Player player, int localX, int localY) {
		switch(event.getAction()) {
			case PLACE_ALL:
			case PLACE_SOME:
			case PLACE_ONE:
			case HOTBAR_MOVE_AND_READD:
			case SWAP_WITH_CURSOR:
				break; //We don't want that
			case CLONE_STACK:
				event.setCancelled(false); //We just do nothing and let the player have the items because the player is in creative
				break;
			default:
				event.setCancelled(false); //We let the player have the items and set this container to empty if it isn't refilled

				if(!refill) {
					empty = true;
				}
				break;
		}
	}
}