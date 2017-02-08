package de.syscy.kagegui.inventory.component;

import java.util.LinkedList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import de.syscy.kagegui.IInventoryWrapper;
import de.syscy.kagegui.icon.ItemIcon;
import de.syscy.kagegui.inventory.KGUI;
import de.syscy.kagegui.inventory.listener.ButtonClickListener;
import de.syscy.kagegui.util.LoreBuilder;
import lombok.Getter;
import lombok.Setter;

public class KList extends KComponent {
	protected LinkedList<KComponent> components = new LinkedList<>();
	protected LinkedList<KComponent> currentPageComponents = new LinkedList<>();

	protected KComponent[] navigationComponents = new KComponent[9];

	protected @Getter @Setter int listBottomMargin = 0;

	protected @Getter ItemIcon previousPageIcon = new ItemIcon(new ItemStack(Material.PAPER));
	protected @Getter ItemIcon nextPageIcon = new ItemIcon(new ItemStack(Material.PAPER));

	protected KButton previousPageButton;
	protected KButton nextPageButton;

	protected @Getter int currentPage = 0;
	protected int lastPage = -1;
	protected @Getter int totalPages = 0;
	protected @Getter int componentsPerPage = 0;

	public KList(int x, int y) {
		super(x, y);

		createPageSwitchButtons();
	}

	@Override
	public void update() {
		if(lastPage != currentPage) {
			onPageChanged();

			gui.markDirty();
		}

		for(KComponent component : currentPageComponents) {
			component.update();
		}

		for(int i = 0; i < width; i++) {
			if(navigationComponents[i] != null) {
				navigationComponents[i].update();
			}
		}
	}

	@Override
	public void render(IInventoryWrapper inventory) {
		for(KComponent component : currentPageComponents) {
			component.render(inventory);
		}

		for(int i = 0; i < width; i++) {
			if(navigationComponents[i] != null) {
				navigationComponents[i].render(inventory);
			}
		}
	}

	@Override
	public void onClick(InventoryClickEvent event, Player player, int localX, int localY) {
		if(localY == height - 1) {
			if(navigationComponents[localX] != null && navigationComponents[localX].isVisible()) {
				navigationComponents[localX].onClick(event, player, 0, 0);
			}
		} else {
			for(KComponent component : currentPageComponents) {
				if(component.isVisible() && component.getX() == x + localX && component.getY() == y + localY) {
					component.onClick(event, player, 0, 0);
				}
			}
		}
	}

	public void add(KComponent component) {
		if(!(component instanceof KButton || component instanceof KLabel || component instanceof KCheckButton || component instanceof KItemContainer)) {
			throw new IllegalArgumentException("Invalid component type for a list: " + component);
		}

		component.setGui(gui);

		component.setWidth(1);
		component.setHeight(1);

		components.add(component);

		recalculateValues();
	}

	public void addNavigationComponent(int x, KComponent component) {
		if(!(component instanceof KButton || component instanceof KLabel || component instanceof KCheckButton || component instanceof KItemContainer)) {
			throw new IllegalArgumentException("Invalid component type for a list navigation: " + component);
		}

		if((x == 0 || x == navigationComponents.length - 1) && navigationComponents[x] != null) {
			throw new IllegalAccessError("You can't replace the next/previous page buttons");
		}

		component.setGui(gui);

		component.setX(this.x + x);
		component.setY(y + height - 1);
		component.setWidth(1);
		component.setHeight(1);

		navigationComponents[x] = component;
	}

	public void setPreviousPageIcon(ItemIcon previousPageIcon) {
		this.previousPageIcon = previousPageIcon;

		createPageSwitchButtons();
	}

	public void setNextPageIcon(ItemIcon nextPageIcon) {
		this.nextPageIcon = nextPageIcon;

		createPageSwitchButtons();
	}

	@Override
	public void setGui(KGUI gui) {
		super.setGui(gui);

		for(KComponent component : components) {
			component.setGui(gui);
		}

		if(navigationComponents != null) {
			for(KComponent navigationComponent : navigationComponents) {
				if(navigationComponent != null) {
					navigationComponent.setGui(gui);
				}
			}
		}
	}

	private void onPageChanged() {
		currentPageComponents.clear();
		gui.getInventoryWrapper().clear();

		for(int i = currentPage * componentsPerPage; i < currentPage * componentsPerPage + componentsPerPage && i < components.size(); i++) {
			currentPageComponents.add(components.get(i));
		}

		int x = this.x;
		int y = this.y;

		for(int i = 0; i < currentPageComponents.size(); i++) {
			KComponent component = currentPageComponents.get(i);
			component.setX(x);
			component.setY(y);

			x++;

			if(x == this.x + width) {
				x = this.x;
				y++;
			}

			if(y == this.y + height - listBottomMargin) {
				break;
			}
		}

		lastPage = currentPage;
	}

	private void recalculateValues() {
		componentsPerPage = width * Math.max(1, height - 1 - listBottomMargin);

		totalPages = (int) Math.ceil((float) components.size() / (float) componentsPerPage);
	}

	private void createPageSwitchButtons() {
		previousPageButton = new KButton(0, 0);
		previousPageButton.setIcon(previousPageIcon);
		previousPageButton.setClickListener(new ButtonClickListener() {
			@Override
			public void onClick(KButton button, Player player) {
				if(currentPage > 0) {
					currentPage--;
				}

				updatePageSwitchButtons();
			}
		}, "");

		navigationComponents[0] = null;
		addNavigationComponent(0, previousPageButton);

		nextPageButton = new KButton(0, 0);
		nextPageButton.setIcon(nextPageIcon);
		nextPageButton.setClickListener(new ButtonClickListener() {
			@Override
			public void onClick(KButton button, Player player) {
				if(currentPage < totalPages - 1) {
					currentPage++;
				}

				updatePageSwitchButtons();
			}
		}, "");

		navigationComponents[width - 1] = null;
		addNavigationComponent(width - 1, nextPageButton);

		updatePageSwitchButtons();
	}

	private void updatePageSwitchButtons() {
		previousPageButton.setVisible(currentPage > 0);
		nextPageButton.setVisible(currentPage < totalPages - 1);

		previousPageButton.setTitle("§kgui.list." + (currentPage == 1 ? "firstPage" : "previousPage") + ";");
		nextPageButton.setTitle("§kgui.list." + (currentPage == totalPages - 2 ? "lastPage" : "nextPage") + ";");

		previousPageButton.getLoreBuilder().set(LoreBuilder.KCOMPONENT_LORE, "§kgui.list.currentPage;" + (currentPage + 1) + "i;");
		nextPageButton.getLoreBuilder().set(LoreBuilder.KCOMPONENT_LORE, "§kgui.list.currentPage;" + (currentPage + 1) + "i;");
	}
}