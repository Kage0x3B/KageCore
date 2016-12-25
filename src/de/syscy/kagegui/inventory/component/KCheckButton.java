package de.syscy.kagegui.inventory.component;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import de.syscy.kagegui.IInventoryWrapper;
import de.syscy.kagegui.icon.ItemIcon;
import de.syscy.kagegui.inventory.listener.CheckButtonToggleListener;
import de.syscy.kagegui.util.Lore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

public class KCheckButton extends KComponent {
	private @Getter boolean enabled;

	private @Getter ItemIcon enabledIcon;
	private @Getter String enabledTitle;
	private @Getter Lore enabledLore;
	private @Getter ItemIcon disabledIcon;
	private @Getter String disabledTitle;
	private @Getter Lore disabledLore;

	private CheckButtonToggleListener toggleListener;

	private KCheckButton(Builder builder) {
		super(builder);

		enabled = builder.enabled;
		enabledIcon = builder.enabledIcon;
		enabledTitle = builder.enabledTitle;
		enabledLore = builder.enabledLore;
		disabledIcon = builder.disabledIcon;
		disabledTitle = builder.disabledTitle;
		disabledLore = builder.disabledLore;
		toggleListener = builder.toggleListener;
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
		this.renderItem(inventory, x, y, width, height, isEnabled() ? enabledIcon : disabledIcon, isEnabled() ? enabledTitle : disabledTitle, isEnabled() ? enabledLore : disabledLore);
	}

	public static Builder builder() {
		return new Builder();
	}

	@Accessors(fluent = true)
	public static class Builder extends KComponent.Builder<KCheckButton> {
		private @Setter boolean enabled = false;

		private @Setter String enabledTitle;
		private @Setter Lore enabledLore;
		private @Setter ItemIcon enabledIcon = new ItemIcon(new ItemStack(Material.WOOL, 1, (short) 5));

		private @Setter String disabledTitle;
		private @Setter Lore disabledLore;
		private @Setter ItemIcon disabledIcon = new ItemIcon(new ItemStack(Material.WOOL, 1, (short) 14));

		private @Setter CheckButtonToggleListener toggleListener;

		public Builder title(String title) {
			enabledTitle = title;
			disabledTitle = title;

			return this;
		}

		public Builder lore(Lore lore) {
			enabledLore = lore;
			disabledLore = lore;

			return this;
		}

		public Builder icon(ItemIcon icon) {
			enabledIcon = icon;
			disabledIcon = icon;

			return this;
		}

		@Override
		public KCheckButton build() {
			return new KCheckButton(this);
		}
	}
}