package de.syscy.kagegui.crafting;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import de.syscy.kagegui.IInventoryWrapper;
import de.syscy.kagegui.icon.ItemIcon;
import de.syscy.kagegui.util.Lore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

public class KCraftingButton extends KCraftingComponent {
	protected @Getter @Setter String title = "Button";
	protected @Getter @Setter ItemIcon icon;
	protected @Getter @Setter Lore lore;

	private @Setter CraftingButtonClickListener clickListener;

	protected KCraftingButton(Builder builder) {
		super(builder);

		title = builder.title;
		icon = builder.icon;
		lore = builder.lore;
		clickListener = builder.clickListener;
	}

	@Override
	public void update() {
		icon.update(gui);
	}

	@Override
	public void render(IInventoryWrapper inventory) {
		renderItem(inventory, craftingSlot, icon, title, lore);
	}

	@Override
	public void onClick(InventoryClickEvent event, Player player, int localX, int localY) {
		if(clickListener != null) {
			clickListener.onClick(this, player);

			gui.markDirty();
		}
	}

	public static Builder builder() {
		return new Builder();
	}

	@Accessors(fluent = true)
	public static class Builder extends KCraftingComponent.Builder<KCraftingButton> {
		private @Setter String title = "Button";
		private @Setter ItemIcon icon = new ItemIcon(Material.STONE);
		private @Setter Lore lore = new Lore();

		private @Setter CraftingButtonClickListener clickListener = null;

		@Override
		public KCraftingButton build() {
			return new KCraftingButton(this);
		}
	}
}