package de.syscy.kagegui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public interface IComponent {
	public void update();

	public void render(IInventoryWrapper inventory);

	public void onClick(InventoryClickEvent event, Player player, int localX, int localY);
}