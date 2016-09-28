package de.syscy.bguilib;

import org.bukkit.entity.Player;

import de.syscy.bguilib.components.BGHotbarComponent;
import de.syscy.bguilib.components.BGHotbarInventory;
import lombok.Getter;

public class BGHotbarGUI {
	private @Getter Player player;
	private @Getter BGHotbarInventory hotbarInventory;
	private @Getter BGHotbarComponent[] components = new BGHotbarComponent[9];

	protected void show(Player player) {
		this.player = player;
		this.hotbarInventory = new BGHotbarInventory(player.getInventory());
		this.render();
	}

	public void add(BGHotbarComponent component) {
		if(component.getSlot() >= 0 && component.getSlot() <= 8) {
			this.components[component.getSlot()] = component;

			component.init(this);
		}
	}

	public void update() {
		for(int i = 0; i < this.components.length; i++) {
			BGHotbarComponent component = this.components[i];

			if(component != null) {
				component.update();
			}
		}
	}

	public boolean render() {
		for(int i = 0; i < this.components.length; i++) {
			BGHotbarComponent component = this.components[i];

			if(component != null) {
				component.render();
			}
		}
		
		return hotbarInventory.flush();
	}

	public void close() {
		BGUILib.removeHotbarGUI(this.player);
	}

	public void onClick(int slot) {
		if(this.components[slot] != null) {
			this.components[slot].onClick(this.player);
		}
	}
}