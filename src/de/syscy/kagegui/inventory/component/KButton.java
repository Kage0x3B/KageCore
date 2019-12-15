package de.syscy.kagegui.inventory.component;

import de.syscy.kagecore.translation.Translator;
import de.syscy.kagegui.IInventoryWrapper;
import de.syscy.kagegui.icon.ItemIcon;
import de.syscy.kagegui.inventory.listener.ButtonClickListener;
import de.syscy.kagegui.util.ClickType;
import de.syscy.kagegui.util.LoreBuilder;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

public class KButton extends KComponent {
	public static final int STAY_UNTIL_CLICK = Integer.MAX_VALUE;
	private static final LoreBuilder EMPTY_LORE = new LoreBuilder();

	protected @Getter @Setter String title = "Button";
	protected final @Getter LoreBuilder loreBuilder = new LoreBuilder();
	protected @Getter @Setter ItemIcon icon = new ItemIcon(Material.STONE);

	protected ButtonClickListener[] clickListener = new ButtonClickListener[ClickType.VALUES.length];

	private int statusTime = 0;
	private String statusMessage;
	private ItemIcon statusIcon;

	public KButton(int x, int y) {
		super(x, y);
	}

	@Override
	public void update() {
		icon.update(gui);

		if(statusIcon != null) {
			statusIcon.update(gui);
		}

		if(statusTime > 0) {
			statusTime--;

			if(statusTime == 0) {
				gui.markDirty();
			}
		}
	}

	@Override
	public void render(IInventoryWrapper inventory) {
		if(statusTime > 0) {
			this.renderItem(inventory, x, y, width, height, statusIcon, statusMessage, EMPTY_LORE);
		} else {
			this.renderItem(inventory, x, y, width, height, icon, title, loreBuilder);
		}
	}

	@Override
	public void onClick(InventoryClickEvent event, Player player, int localX, int localY) {
		if(statusTime > 0) {
			statusTime = 0;

			gui.markDirty();

			return;
		}

		ClickType clickType = ClickType.getType(event.getAction());

		if(clickListener[clickType.ordinal()] != null) {
			clickListener[clickType.ordinal()].onClick(this, player);

			gui.markDirty();
		} else if(clickListener[ClickType.GENERAL.ordinal()] != null) {
			clickListener[ClickType.GENERAL.ordinal()].onClick(this, player);

			gui.markDirty();
		}
	}

	public void setClickListener(ButtonClickListener clickListener, String actionDescription) {
		setClickListener(ClickType.GENERAL, clickListener, actionDescription);
	}

	public void setClickListener(ClickType clickType, ButtonClickListener clickListener, String actionDescription) {
		this.clickListener[clickType.ordinal()] = clickListener;

		if(actionDescription != null && !actionDescription.isEmpty()) {
			loreBuilder.set(LoreBuilder.CLICK_ACTIONS + clickType.ordinal(), clickType.getLoreButtonPrefix() + ": " + actionDescription);
		}
	}

	public void displayStatus(int time, Status status) {
		displayStatus(time, status.getMessageKey(), status.getItemIcon(), status.getSound(), 1.0f);
	}

	public void displayStatus(int time, String message, ItemIcon statusIcon) {
		displayStatus(time, message, statusIcon, null, 0.0f);
	}

	/**
	 * Time is not exactly measured in ticks, it is just decremented on every update whichs happens once every few ticks.
	 */
	public void displayStatus(int time, String message, ItemIcon statusIcon, Sound sound, float pitch) {
		statusTime = time;
		statusMessage = message;
		this.statusIcon = statusIcon;

		if(sound != null) {
			gui.getPlayer().playSound(gui.getPlayer().getLocation(), sound, SoundCategory.MASTER, 1.0f, pitch);
		}

		gui.markDirty();
	}

	@RequiredArgsConstructor
	@SuppressWarnings("deprecation")
	public static enum Status {
		SUCCESS(new ItemIcon(new ItemStack(Material.LIME_WOOL, 1, (short) 0, (byte) 5)), Sound.BLOCK_NOTE_BLOCK_BELL), FAILED(new ItemIcon(new ItemStack(Material.RED_WOOL)), Sound.ENTITY_ARMOR_STAND_BREAK);

		private final @Getter ItemIcon itemIcon;
		private final @Getter Sound sound;

		public String getMessageKey() {
			return Translator.SIGN + "kgui.buttonStatus." + name().toLowerCase() + ";";
		}
	}
}