package de.syscy.kagegui.inventory.component;

import de.syscy.kagegui.IInventoryWrapper;
import de.syscy.kagegui.icon.ItemIcon;
import de.syscy.kagegui.util.LoreBuilder;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString(exclude = "loreBuilder")
public class KLabel extends KComponent {
	protected @Getter @Setter String title = "Label";
	protected @Getter @Setter LoreBuilder loreBuilder = new LoreBuilder();
	protected @Getter @Setter ItemIcon icon = new ItemIcon(Material.NAME_TAG);

	public KLabel(int x, int y) {
		super(x, y);
	}

	@Override
	public void update() {
		icon.update(gui);
	}

	@Override
	public void render(IInventoryWrapper inventory) {
		this.renderItem(inventory, x, y, width, height, icon, title, loreBuilder);
	}

	@Override
	public void onClick(InventoryClickEvent event, Player player, int localX, int localY) {

	}
}