package de.syscy.kagegui.inventory.component;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import de.syscy.kagegui.IInventoryWrapper;
import de.syscy.kagegui.icon.ItemIcon;
import de.syscy.kagegui.util.Lore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

public class KLabel extends KComponent {
	private @Getter @Setter String title = "Button";
	private @Getter @Setter Lore lore;
	private @Getter @Setter ItemIcon icon;

	private KLabel(Builder builder) {
		super(builder);

		title = builder.title;
		icon = builder.icon;
		lore = builder.lore;
	}

	@Override
	public void update() {
		icon.update(gui);
	}

	@Override
	public void render(IInventoryWrapper inventory) {
		this.renderItem(inventory, x, y, width, height, icon, title, lore);
	}

	@Override
	public void onClick(InventoryClickEvent event, Player player, int localX, int localY) {

	}

	public static Builder builder() {
		return new Builder();
	}

	@Accessors(fluent = true)
	public static class Builder extends KComponent.Builder<KLabel> {
		private @Setter String title;
		private @Setter Lore lore;
		private @Setter ItemIcon icon = new ItemIcon(Material.NAME_TAG);

		@Override
		public KLabel build() {
			return new KLabel(this);
		}
	}
}