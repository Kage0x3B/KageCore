package de.syscy.bguilib.components;

import java.util.LinkedList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import de.syscy.bguilib.components.icon.ItemIcon;
import de.syscy.bguilib.util.Lore;
import lombok.Getter;

public class BGList extends BGComponent {
	private LinkedList<BGComponent> components = new LinkedList<>();
	private LinkedList<BGComponent> currentPageComponents = new LinkedList<>();

	private @Getter ItemIcon previousPageIcon;
	private @Getter ItemIcon nextPageIcon;

	private @Getter int currentPage = 0;
	private int lastPage = -1;
	private @Getter int totalPages = 0;
	private @Getter int componentsPerPage = 0;

	public BGList(int x, int y, int width, int height) {
		super(x, y, Math.max(width, 2), Math.max(height, 1));

		previousPageIcon = new ItemIcon(new ItemStack(Material.NETHER_STAR));
		nextPageIcon = new ItemIcon(new ItemStack(Material.NETHER_STAR));
	}

	public void update() {
		if(lastPage != currentPage) {
			onPageChanged();
		}

		for(BGComponent component : currentPageComponents) {
			component.update();
		}
	}

	public void render(BGInventory inventory) {
		for(BGComponent component : currentPageComponents) {
			component.render(inventory);
		}

		if(totalPages > 1) {
			renderItem(inventory, this.x, this.y + this.height - 1, 1, 1, previousPageIcon, "Previous page", new Lore("Current page: " + (currentPage + 1)), true);
			renderItem(inventory, this.x + this.width - 1, this.y + this.height - 1, 1, 1, nextPageIcon, "Next page", new Lore("Current page: " + (currentPage + 1)), true);
		}
	}

	public void onClick(InventoryClickEvent event, Player player, int localX, int localY) {
		if(totalPages > 1 && localX == 0 && localY == height - 1) { //Clicked on the previous page button
			if(currentPage > 0) {
				currentPage--;
			}
		} else if(totalPages > 1 && localX == width - 1 && localY == height - 1) { //Clicked on the next page button
			if(currentPage < totalPages - 1) {
				currentPage++;
			}
		} else if(localY < height) {
			for(BGComponent component : currentPageComponents) {
				if(component.getX() == this.x + localX && component.getY() == this.y + localY) {
					component.onClick(event, player, 0, 0);
				}
			}
		}
	}

	public void addComponent(BGComponent component) {
		if(!(component instanceof BGButton || component instanceof BGLabel || component instanceof BGCheckButton || component instanceof BGItemContainer)) {
			throw new IllegalArgumentException("Invalid component for a list: " + component);
		}

		component.setWidth(1);
		component.setHeight(1);

		this.components.add(component);

		recalculateValues();
	}

	private void onPageChanged() {
		currentPageComponents.clear();

		for(int i = currentPage * componentsPerPage; i < currentPage * componentsPerPage + componentsPerPage && i < components.size(); i++) {
			currentPageComponents.add(components.get(i));
		}
		
		//Reposition components
		
		int x = this.x;
		int y = this.y;

		for(int i = 0; i < currentPageComponents.size(); i++) {
			BGComponent component = currentPageComponents.get(i);
			component.setX(x);
			component.setY(y);

			x++;

			if(x == this.x + width) {
				x = this.x;
				y++;
			}

			if(y == this.y + height) {
				break;
			}
		}

		lastPage = currentPage;
	}

	private void recalculateValues() {
		componentsPerPage = this.width * Math.max(1, this.height - 1);
		totalPages = (int) Math.ceil((float) components.size() / (float) componentsPerPage);
	}
}