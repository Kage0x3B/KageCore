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
import de.syscy.kagegui.util.Lore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

public class KList extends KComponent {
	private LinkedList<KComponent> components = new LinkedList<>();
	private LinkedList<KComponent> currentPageComponents = new LinkedList<>();

	private KComponent[] navigationComponents;

	private int listBottomMargin;

	private KButton previousPageButton;
	private KButton nextPageButton;

	private @Getter int currentPage = 0;
	private int lastPage = -1;
	private @Getter int totalPages = 0;
	private @Getter int componentsPerPage = 0;

	private KList(Builder builder) {
		super(builder);

		listBottomMargin = builder.listBottomMargin;

		if(builder.height > 1) {
			navigationComponents = new KComponent[builder.width];

			KButton.Builder previousPageButtonBuilder = KButton.builder();
			previousPageButtonBuilder.title("§kgui.list.previousPage;");
			previousPageButtonBuilder.lore(new Lore("§kgui.list.currentPage;" + (currentPage + 1) + "i;"));
			previousPageButtonBuilder.icon(builder.previousPageIcon);
			previousPageButtonBuilder.clickListener(new ButtonClickListener() {
				@Override
				public void onClick(KButton button, Player player) {
					if(currentPage > 0) {
						currentPage--;
					}

					previousPageButton.setLore(new Lore("§kgui.list.currentPage;" + (currentPage + 1) + "i;"));
					nextPageButton.setLore(new Lore("§kgui.list.currentPage;" + (currentPage + 1) + "i;"));
				}
			});

			addNavigationComponent(0, previousPageButton = previousPageButtonBuilder.build());

			KButton.Builder nextPageButtonBuilder = KButton.builder();
			nextPageButtonBuilder.title("§kgui.list.nextPage;");
			nextPageButtonBuilder.lore(new Lore("§kgui.list.currentPage;" + (currentPage + 1) + "i;"));
			nextPageButtonBuilder.icon(builder.nextPageIcon);
			nextPageButtonBuilder.clickListener(new ButtonClickListener() {
				@Override
				public void onClick(KButton button, Player player) {
					if(currentPage < totalPages - 1) {
						currentPage++;
					}

					previousPageButton.setLore(new Lore("§kgui.list.currentPage;" + (currentPage + 1) + "i;"));
					nextPageButton.setLore(new Lore("§kgui.list.currentPage;" + (currentPage + 1) + "i;"));
				}
			});

			addNavigationComponent(width - 1, nextPageButton = nextPageButtonBuilder.build());
		}
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

		if(navigationComponents != null) {
			for(KComponent navigationComponent : navigationComponents) {
				if(navigationComponent != null) {
					navigationComponent.update();
				}
			}
		}
	}

	@Override
	public void render(IInventoryWrapper inventory) {
		for(KComponent component : currentPageComponents) {
			component.render(inventory);
		}

		if(navigationComponents != null) {
			for(KComponent navigationComponent : navigationComponents) {
				if(navigationComponent != null) {
					navigationComponent.render(inventory);
				}
			}
		}
	}

	@Override
	public void onClick(InventoryClickEvent event, Player player, int localX, int localY) {
		if(navigationComponents != null && localY == height - 1) {
			if(navigationComponents[localX] != null) {
				navigationComponents[localX].onClick(event, player, 0, 0);
			}
		} else {
			for(KComponent component : currentPageComponents) {
				if(component.getX() == x + localX && component.getY() == y + localY) {
					component.onClick(event, player, 0, 0);
				}
			}
		}
	}

	public void addComponent(KComponent component) {
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

	public static Builder builder() {
		return new Builder();
	}

	@Accessors(fluent = true)
	public static class Builder extends KComponent.Builder<KList> {
		private @Setter ItemIcon previousPageIcon = new ItemIcon(new ItemStack(Material.NETHER_STAR));
		private @Setter ItemIcon nextPageIcon = new ItemIcon(new ItemStack(Material.NETHER_STAR));

		private @Setter int listBottomMargin = 0;

		@Override
		public KList build() {
			return new KList(this);
		}
	}
}