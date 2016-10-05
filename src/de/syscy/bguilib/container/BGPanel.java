package de.syscy.bguilib.container;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.inventory.InventoryClickEvent;

import de.syscy.bguilib.components.BGComponent;
import de.syscy.bguilib.util.Util;
import lombok.Getter;

public class BGPanel extends BGContainer {
	private @Getter List<BGComponent> components = new ArrayList<>();

	public void add(BGComponent component) {
		this.components.add(component);
	}

	public void update() {
		for(BGComponent component : components) {
			component.update();
		}
	}

	public void render() {
		for(BGComponent component : components) {
			component.render(this.gui.getBgInventory());
		}
	}

	public void onClick(InventoryClickEvent event, int x, int y) {
		for(BGComponent component : components) {
			if(Util.pointInBoundingBox(x, y, component.getX(), component.getY(), component.getWidth(), component.getHeight())) {
				component.onClick(event, this.gui.getPlayer(), x - component.getX(), y - component.getY());
			}
		}
	}
}