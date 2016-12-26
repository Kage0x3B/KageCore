package de.syscy.kagegui.inventory.component;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import de.syscy.kagegui.IInventoryWrapper;
import de.syscy.kagegui.icon.ItemIcon;
import de.syscy.kagegui.inventory.listener.CheckButtonToggleListener;
import de.syscy.kagegui.util.LoreBuilder;
import lombok.Getter;
import lombok.Setter;

public class KCheckButton extends KComponent {
	protected @Getter @Setter boolean enabled = false;

	protected @Getter @Setter ItemIcon enabledIcon = new ItemIcon(new ItemStack(Material.WOOL, 1, (short) 5));
	protected @Getter @Setter String enabledTitle;
	protected final @Getter LoreBuilder enabledLoreBuilder = new LoreBuilder();
	protected @Getter @Setter ItemIcon disabledIcon = new ItemIcon(new ItemStack(Material.WOOL, 1, (short) 14));
	protected @Getter @Setter String disabledTitle;
	protected final @Getter LoreBuilder disabledLoreBuilder = new LoreBuilder();

	protected @Setter CheckButtonToggleListener toggleListener;

	public KCheckButton(int x, int y) {
		super(x, y);
	}

	@Override
	public void update() {
		enabledIcon.update(gui);
		disabledIcon.update(gui);
	}

	@Override
	public void onClick(InventoryClickEvent event, Player player, int localX, int localY) {
		enabled = !isEnabled();

		if(toggleListener != null) {
			toggleListener.onToggle(player, enabled);
		}

		gui.markDirty();
	}

	@Override
	public void render(IInventoryWrapper inventory) {
		this.renderItem(inventory, x, y, width, height, isEnabled() ? enabledIcon : disabledIcon, isEnabled() ? enabledTitle : disabledTitle, isEnabled() ? enabledLoreBuilder : disabledLoreBuilder);
	}
}