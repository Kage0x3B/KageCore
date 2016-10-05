package de.syscy.bguilib.components;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import de.syscy.bguilib.components.icon.ItemIcon;
import de.syscy.bguilib.components.listener.ButtonClickListener;
import de.syscy.bguilib.util.Lore;
import lombok.Getter;
import lombok.Setter;

public class BGButton extends BGComponent {
	private List<ButtonClickListener> listeners = new ArrayList<>();
	private @Getter @Setter ItemIcon buttonIcon;
	private @Getter @Setter String title = "Button";
	private @Getter @Setter Lore lore = new Lore("");

	public BGButton(int x, int y, String title) {
		super(x, y, 1, 1);
		this.title = title;
		this.setButtonIcon(new ItemIcon(new ItemStack(Material.STONE)));
	}

	public void update() {
		this.buttonIcon.update();
	}

	public void render(BGInventory inventory) {
		this.renderItem(inventory, this.x, this.y, this.width, this.height, this.buttonIcon, this.title, this.lore);
	}

	public void onClick(InventoryClickEvent event, Player player, int localX, int localY) {
		for(ButtonClickListener listener : listeners) {
			listener.onClick(player);
		}
	}

	public void addClickListener(ButtonClickListener listener) {
		this.listeners.add(listener);
	}
}