package de.syscy.bguilib.container;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import de.syscy.bguilib.BGGUI;
import de.syscy.bguilib.components.BGComponent;
import de.syscy.bguilib.components.icon.ItemIcon;
import de.syscy.bguilib.util.Lore;
import de.syscy.bguilib.util.Util;
import lombok.Getter;
import lombok.Setter;

public class BGTabbedPanel extends BGContainer {
	private @Getter List<BGTab> tabs = new ArrayList<>();
	private @Getter @Setter int currentTab = 0;

	public void init(BGGUI gui) {
		super.init(gui);

		for(BGTab tab : tabs) {
			tab.init(gui);
		}
	}

	public void add(BGTab tab) {
		tabs.add(tab);
	}

	public void add(BGComponent component) {
		throw new UnsupportedOperationException("You can't add anything besides tabs directly to a tabbed panel.");
	}

	public void update() {
		this.tabs.get(this.currentTab).update();
	}

	public void render() {
		for(int i = 0; i < this.tabs.size(); ++i) {
			this.renderItem(this.gui.getInventory(), i, 0, 1, 1, i == this.currentTab ? this.tabs.get(i).getActiveTabIcon() : this.tabs.get(i).getTabIcon(), this.tabs.get(i).getTitle(), this.tabs.get(i).getLore());
		}

		this.tabs.get(this.currentTab).render(this.getGui());
	}

	public void renderItem(Inventory inventory, int x, int y, int width, int height, ItemIcon item, String title, Lore lore) {
		inventory.setItem(Util.toSlotCoordinate(x, y), item.getItem(title, lore));
	}

	public void onClick(InventoryClickEvent event, int x, int y) {
		if(y == 0) {
			if(x < this.tabs.size()) {
				this.setCurrentTab(x);
			}
		} else {
			this.tabs.get(this.currentTab).onClick(event, x, y);
		}

	}
}