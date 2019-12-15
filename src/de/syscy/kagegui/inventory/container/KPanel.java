package de.syscy.kagegui.inventory.container;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.inventory.InventoryClickEvent;

import de.syscy.kagegui.inventory.IContainer;
import de.syscy.kagegui.inventory.KGUI;
import de.syscy.kagegui.inventory.component.KComponent;
import de.syscy.kagegui.util.Util;
import lombok.Getter;

public class KPanel implements IContainer {
	protected @Getter KGUI gui;

	private @Getter List<KComponent> components = new ArrayList<>();

	@Override
	public void init(KGUI gui) {
		this.gui = gui;
	}

	@Override
	public void add(KComponent component) {
		components.add(component);
	}

	@Override
	public void update() {
		for(KComponent component : components) {
			component.update();
		}
	}

	@Override
	public void render() {
		for(KComponent component : components) {
			component.render(gui.getInventoryWrapper());
		}
	}

	@Override
	public void onClick(InventoryClickEvent event, int x, int y) {
		for(KComponent component : components) {
			if(component.isVisible() && Util.pointInBoundingBox(x, y, component.getX(), component.getY(), component.getWidth(), component.getHeight())) {
				component.onClick(event, gui.getPlayer(), x - component.getX(), y - component.getY());
			}
		}
	}
}