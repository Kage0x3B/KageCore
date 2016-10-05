package de.syscy.bguilib.components;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import de.syscy.bguilib.components.icon.ItemIcon;
import de.syscy.bguilib.components.listener.CheckButtonToggleListener;
import de.syscy.bguilib.util.Lore;
import lombok.Getter;
import lombok.Setter;

public class BGCheckButton extends BGComponent {
	private List<CheckButtonToggleListener> listeners = new ArrayList<>();
	private @Getter @Setter boolean enabled = true;

	private @Getter @Setter ItemIcon enabledIcon;
	private @Getter @Setter String enabledTitle;
	private @Getter @Setter Lore enabledLore = new Lore("");
	private @Getter @Setter ItemIcon disabledIcon;
	private @Getter @Setter String disabledTitle;
	private @Getter @Setter Lore disabledLore = new Lore("");

	public BGCheckButton(int x, int y, String title) {
		this(x, y, title, title);
	}

	public BGCheckButton(int x, int y, String enabledTitle, String disabledTitle) {
		super(x, y, 1, 1);

		this.enabled = true;
		this.enabledTitle = enabledTitle;
		this.disabledTitle = disabledTitle;
		this.setEnabledIcon(new ItemIcon(new ItemStack(Material.WOOL, 1, (short) 5)));
		this.setDisabledIcon(new ItemIcon(new ItemStack(Material.WOOL, 1, (short) 14)));
	}

	public void update() {
		this.enabledIcon.update();
		this.disabledIcon.update();
	}

	public void render(BGInventory inventory) {
		this.renderItem(inventory, this.x, this.y, this.width, this.height, this.isEnabled() ? this.enabledIcon : this.disabledIcon, this.isEnabled() ? this.enabledTitle : this.disabledTitle, this.isEnabled() ? this.enabledLore : this.disabledLore);
	}

	public void onClick(InventoryClickEvent event, Player player, int localX, int localY) {
		this.enabled = !this.isEnabled();
		this.getGui().render();

		for(CheckButtonToggleListener listener : listeners) {
			listener.onToggle(player, enabled);
		}
	}

	public void addToggleListener(CheckButtonToggleListener listener) {
		this.listeners.add(listener);
	}
}