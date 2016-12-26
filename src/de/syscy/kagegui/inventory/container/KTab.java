package de.syscy.kagegui.inventory.container;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.inventory.InventoryClickEvent;

import de.syscy.kagegui.icon.ItemIcon;
import de.syscy.kagegui.inventory.KGUI;
import de.syscy.kagegui.inventory.component.KComponent;
import de.syscy.kagegui.util.LoreBuilder;
import de.syscy.kagegui.util.Util;
import lombok.Getter;
import lombok.Setter;

public class KTab {
	private @Getter KGUI gui;

	private @Getter @Setter String title;
	private final @Getter LoreBuilder loreBuilder = new LoreBuilder();

	private @Getter @Setter ItemIcon tabIcon;
	private @Getter @Setter ItemIcon activeTabIcon;

	protected final List<KComponent> components = new ArrayList<>();

	public KTab(ItemIcon tabIcon, String title) {
		this(tabIcon, tabIcon, title);
	}

	public KTab(ItemIcon tabIcon, ItemIcon activeTabIcon, String title) {
		this.title = "Tab";
		this.tabIcon = tabIcon;
		this.activeTabIcon = activeTabIcon;
		this.title = title;
	}

	public void init(KGUI gui) {
		this.gui = gui;

		for(KComponent component : components) {
			component.setGui(gui);
		}
	}

	public void add(KComponent component) {
		components.add(component);

		if(gui != null) {
			component.setGui(gui);
		}

		component.setOffsetY(1);
	}

	public void update() {
		tabIcon.update(gui);
		activeTabIcon.update(gui);

		for(KComponent component : components) {
			component.update();
		}

	}

	public void render(KGUI gui) {
		for(KComponent component : components) {
			component.render(gui.getInventoryWrapper());
		}
	}

	public void onClick(InventoryClickEvent event, int x, int y) {
		for(KComponent component : components) {
			if(Util.pointInBoundingBox(x, y - 1, component.getX(), component.getY(), component.getWidth() > 1 ? component.getWidth() + 1 : component.getWidth(), component.getHeight())) {
				component.onClick(event, gui.getPlayer(), x - component.getX(), y - component.getY() - 1);
			}
		}
	}
}