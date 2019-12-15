package de.syscy.kagegui.crafting;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import de.syscy.kagegui.IInventoryWrapper;
import de.syscy.kagegui.icon.ItemIcon;
import de.syscy.kagegui.util.ClickType;
import de.syscy.kagegui.util.LoreBuilder;
import lombok.Getter;
import lombok.Setter;

public class KCraftingButton extends KCraftingComponent {
	protected @Getter @Setter String title = "Button";
	protected final @Getter LoreBuilder loreBuilder = new LoreBuilder();
	protected @Getter @Setter ItemIcon icon = new ItemIcon(Material.STONE);

	protected CraftingButtonClickListener[] clickListener = new CraftingButtonClickListener[ClickType.VALUES.length];

	public KCraftingButton(int craftingSlot) {
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
		ClickType clickType = ClickType.getType(event.getAction());

		if(clickListener[clickType.ordinal()] != null) {
			clickListener[clickType.ordinal()].onClick(this, player);

			gui.markDirty();
		} else if(clickListener[ClickType.GENERAL.ordinal()] != null) {
			clickListener[ClickType.GENERAL.ordinal()].onClick(this, player);

			gui.markDirty();
		}
	}

	public void setClickListener(CraftingButtonClickListener clickListener, String actionDescription) {
		setClickListener(ClickType.GENERAL, clickListener, actionDescription);
	}

	public void setClickListener(ClickType clickType, CraftingButtonClickListener clickListener, String actionDescription) {
		this.clickListener[clickType.ordinal()] = clickListener;

		if(actionDescription != null && !actionDescription.isEmpty()) {
			loreBuilder.set(LoreBuilder.CLICK_ACTIONS + clickType.ordinal(), clickType.getLoreButtonPrefix() + ": " + actionDescription);
		}
	}
}