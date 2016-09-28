package de.syscy.bguilib.components;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.syscy.bguilib.components.icon.ItemIcon;
import de.syscy.bguilib.components.listener.ButtonClickListener;
import de.syscy.bguilib.util.Lore;
import lombok.Getter;
import lombok.Setter;

public class BGHotbarButton extends BGHotbarComponent {
	private List<ButtonClickListener> listeners = new ArrayList<>();
	private @Getter @Setter ItemIcon buttonIcon;
	private @Getter @Setter String title = "Button";
	private @Getter @Setter Lore lore = new Lore("");

	public BGHotbarButton(int slot, String title) {
		super(slot);
		this.title = title;
		this.setButtonIcon(new ItemIcon(new ItemStack(Material.STONE)));
	}

	public void update() {
		this.buttonIcon.update();
	}

	public void render() {
		this.renderItem(this.slot, this.buttonIcon, this.title, this.lore);
	}

	public void onClick(Player player) {
		for(ButtonClickListener listener : listeners) {
			listener.onClick(player);
		}
	}

	public void addClickListener(ButtonClickListener listener) {
		this.listeners.add(listener);
	}
}