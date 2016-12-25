package de.syscy.kagegui.inventory.component;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import de.syscy.kagegui.IInventoryWrapper;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

public class KItemContainer extends KComponent {
	private static final ItemStack AIR;

	static {
		AIR = new ItemStack(Material.AIR);
	}

	private ItemStack item;
	private @Getter boolean refill;
	private @Getter boolean empty = false;

	private KItemContainer(Builder builder) {
		super(builder);

		item = builder.item;
		refill = builder.refill;
	}

	@Override
	public void update() {

	}

	@Override
	public void render(IInventoryWrapper inventory) {
		if(!empty) {
			this.renderItem(inventory, x, y, width, height, item.clone());
		} else {
			this.renderItem(inventory, x, y, width, height, AIR.clone());
		}
	}

	@Override
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

		gui.markDirty();
	}

	public static Builder builder() {
		return new Builder();
	}

	@Accessors(fluent = true)
	public static class Builder extends KComponent.Builder<KItemContainer> {
		private @Setter ItemStack item = AIR;

		private @Setter boolean refill = true;

		@Override
		public KItemContainer build() {
			return new KItemContainer(this);
		}
	}
}