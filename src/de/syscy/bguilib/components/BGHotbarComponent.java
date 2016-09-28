package de.syscy.bguilib.components;

import org.bukkit.entity.Player;

import de.syscy.bguilib.BGHotbarGUI;
import de.syscy.bguilib.components.icon.ItemIcon;
import de.syscy.bguilib.util.Lore;
import lombok.Getter;

public abstract class BGHotbarComponent {
	private @Getter BGHotbarGUI hotbarGUI;
	protected @Getter int slot = 0;

	public BGHotbarComponent(int slot) {
		setSlot(slot);
	}

	public void init(BGHotbarGUI hotbarGUI) {
		this.hotbarGUI = hotbarGUI;
	}

	public abstract void update();

	public abstract void render();

	public void renderItem(int slot, ItemIcon item, String title, Lore lore) {
		this.hotbarGUI.getHotbarInventory().setItem(slot, item.getItem(title, lore));
	}

	public abstract void onClick(Player var1);

	public void setSlot(int slot) {
		this.slot = Math.max(0, Math.min(slot, 8));
	}
}