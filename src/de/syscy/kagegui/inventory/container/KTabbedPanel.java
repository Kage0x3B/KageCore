package de.syscy.kagegui.inventory.container;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.inventory.InventoryClickEvent;

import de.syscy.kagegui.IInventoryWrapper;
import de.syscy.kagegui.icon.ItemIcon;
import de.syscy.kagegui.inventory.IContainer;
import de.syscy.kagegui.inventory.KGUI;
import de.syscy.kagegui.inventory.component.KComponent;
import de.syscy.kagegui.util.Lore;
import de.syscy.kagegui.util.Util;
import lombok.Getter;
import lombok.Setter;

public class KTabbedPanel implements IContainer {
	private @Getter KGUI gui;

	private @Getter List<KTab> tabs = new ArrayList<>();
	private @Getter @Setter int currentTab = 0;

	@Override
	public void init(KGUI gui) {
		for(KTab tab : tabs) {
			tab.init(gui);
		}
	}

	public void add(KTab tab) {
		tabs.add(tab);
	}

	@Override
	public void add(KComponent component) {
		throw new UnsupportedOperationException("You can't add anything besides tabs directly to a tabbed panel.");
	}

	@Override
	public void update() {
		tabs.get(currentTab).update();
	}

	@Override
	public void render() {
		for(int i = 0; i < tabs.size(); ++i) {
			renderItem(gui.getInventoryWrapper(), i, 0, 1, 1, i == currentTab ? tabs.get(i).getActiveTabIcon() : tabs.get(i).getTabIcon(), tabs.get(i).getTitle(), tabs.get(i).getLore());
		}

		tabs.get(currentTab).render(getGui());
	}

	public void renderItem(IInventoryWrapper inventory, int x, int y, int width, int height, ItemIcon item, String title, Lore lore) {
		inventory.setItem(Util.toSlotCoordinate(x, y), item.getItem(title, lore));
	}

	@Override
	public void onClick(InventoryClickEvent event, int x, int y) {
		if(y == 0) {
			if(x < tabs.size()) {
				setCurrentTab(x);
			}
		} else {
			tabs.get(currentTab).onClick(event, x, y);
		}
	}
}