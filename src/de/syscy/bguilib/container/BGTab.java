package de.syscy.bguilib.container;

import java.util.ArrayList;
import java.util.List;

import de.syscy.bguilib.BGGUI;
import de.syscy.bguilib.components.BGComponent;
import de.syscy.bguilib.components.icon.ItemIcon;
import de.syscy.bguilib.util.Lore;
import de.syscy.bguilib.util.Util;
import lombok.Getter;
import lombok.Setter;

public class BGTab {
	protected List<BGComponent> components = new ArrayList<>();
	private @Getter BGGUI gui;
	private @Getter @Setter ItemIcon tabIcon;
	private @Getter @Setter ItemIcon activeTabIcon;
	private @Getter @Setter String title;
	private @Getter @Setter Lore lore;

	public BGTab(ItemIcon tabIcon, String title) {
		this(tabIcon, tabIcon, title);
	}

	public BGTab(ItemIcon tabIcon, ItemIcon activeTabIcon, String title) {
		this.title = "Tab";
		this.lore = new Lore("");
		this.tabIcon = tabIcon;
		this.activeTabIcon = activeTabIcon;
		this.title = title;
	}

	public void init(BGGUI gui) {
		this.gui = gui;
		
		for(BGComponent component : components) {
			component.init(gui);
		}
	}

	public void add(BGComponent component) {
		this.components.add(component);

		if(this.gui != null) {
			component.init(this.gui);
		}

		component.setOffsetY(1);
	}

	public void update() {
		this.tabIcon.update();
		this.activeTabIcon.update();

		for(BGComponent component : components) {
			component.update();
		}

	}

	public void render(BGGUI gui) {
		for(BGComponent component : components) {
			component.render(gui.getBgInventory());
		}
	}

	public void onClick(int x, int y) {
		for(BGComponent component : components) {
			if(Util.pointInBoundingBox(x, y - 1, component.getX(), component.getY(), component.getWidth() > 1 ? component.getWidth() + 1 : component.getWidth(), component.getHeight())) {
				component.onClick(this.gui.getPlayer(), x - component.getX(), y - component.getY() - 1);
			}
		}
	}
}