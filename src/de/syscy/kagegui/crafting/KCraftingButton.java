package de.syscy.kagegui.crafting;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import de.syscy.kagegui.IInventoryWrapper;
import de.syscy.kagegui.icon.ItemIcon;
import de.syscy.kagegui.util.LoreBuilder;
import lombok.Getter;
import lombok.Setter;

public class KCraftingButton extends KCraftingComponent {
	protected @Getter @Setter String title = "Button";
	protected final @Getter LoreBuilder loreBuilder = new LoreBuilder();
	protected @Getter @Setter ItemIcon icon = new ItemIcon(Material.STONE);

	protected @Setter CraftingButtonClickListener clickListener;

	protected KCraftingButton(int craftingSlot) {
		super(craftingSlot);
	}

	@Override
	public void update() {
		icon.update(gui);
	}

	@Override
	public void render(IInventoryWrapper inventory) {
		renderItem(inventory, craftingSlot, icon, title, loreBuilder);
	}

	@Override
	public void onClick(InventoryClickEvent event, Player player, int localX, int localY) {
		if(clickListener != null) {
			clickListener.onClick(this, player);

			gui.markDirty();
		}
	}
}