package de.syscy.kagegui.inventory.component;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import de.syscy.kagegui.IInventoryWrapper;
import de.syscy.kagegui.icon.ItemIcon;
import de.syscy.kagegui.inventory.listener.ButtonClickListener;
import de.syscy.kagegui.util.LoreBuilder;
import lombok.Getter;
import lombok.Setter;

public class KButton extends KComponent {
	protected @Getter @Setter String title = "Button";
	protected final @Getter LoreBuilder loreBuilder = new LoreBuilder();
	protected @Getter @Setter ItemIcon icon = new ItemIcon(Material.STONE);

	protected @Setter ButtonClickListener clickListener;

	public KButton(int x, int y) {
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
		if(clickListener != null) {
			clickListener.onClick(this, player);

			gui.markDirty();
		}
	}
}