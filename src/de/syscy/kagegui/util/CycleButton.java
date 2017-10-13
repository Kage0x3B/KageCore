package de.syscy.kagegui.util;

import java.security.InvalidParameterException;

import de.syscy.kagecore.translation.Translator;
import de.syscy.kagegui.IInventoryWrapper;
import de.syscy.kagegui.icon.ItemIcon;
import de.syscy.kagegui.inventory.component.KButton;
import de.syscy.kagegui.inventory.listener.ButtonClickListener;

import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class CycleButton extends KButton {
	private @Getter String titleKey;
	private @Getter CycleItem[] items;

	private @Getter int currentIndex = 0;

	/**
	 * Use this constructor with an enum implementing the {@link ICycleItemEnum} interface. You can then get back the enum value of the currently selected item with {@link CycleButton#getCurrentEnumValue()}
	 */
	public CycleButton(int x, int y, String titleKey, ICycleItemEnum[] itemEnumValues) {
		this(x, y, titleKey, fromEnumValues(itemEnumValues));
	}

	public CycleButton(int x, int y, String titleKey, CycleItem[] items) {
		super(x, y);

		if(items == null || items.length <= 0) {
			throw new InvalidParameterException("items can't be null or empty!");
		}

		this.titleKey = titleKey;
		this.items = items;

		setClickListener(ClickType.GENERAL, new CycleClickListener(1), null);
		setClickListener(ClickType.LEFT_CLICK, new CycleClickListener(1), Translator.SIGN + "kgui.cycleButton.nextItem;");
		setClickListener(ClickType.RIGHT_CLICK, new CycleClickListener(-1), Translator.SIGN + "kgui.cycleButton.previousItem;");
	}

	@Override
	public void render(IInventoryWrapper inventory) {
		CycleItem currentItem = items[currentIndex];

		loreBuilder.set(LoreBuilder.KCOMPONENT_LORE, Translator.SIGN + currentItem.getItemNameKey() + ";");

		this.renderItem(inventory, x, y, width, height, currentItem.getItemIcon(), Translator.SIGN + titleKey + ";", loreBuilder);
	}

	public ICycleItemEnum getCurrentEnumValue() {
		if(!(items[currentIndex] instanceof EnumCycleItem)) {
			throw new IllegalStateException("The underlaying items don't belong to an enum!");
		}

		return ((EnumCycleItem) items[currentIndex]).getEnumValue();
	}

	@Override
	public void displayStatus(int time, String message, ItemIcon statusIcon, Sound sound, float pitch) {
		throw new NotImplementedException();
	}

	private static EnumCycleItem[] fromEnumValues(ICycleItemEnum[] itemEnumValues) {
		if(itemEnumValues == null || itemEnumValues.length <= 0) {
			throw new InvalidParameterException("items can't be null or empty!");
		}

		EnumCycleItem[] cycleItems = new EnumCycleItem[itemEnumValues.length];

		for(int i = 0; i < itemEnumValues.length; i++) {
			cycleItems[i] = new EnumCycleItem(itemEnumValues[i]);
		}

		return cycleItems;
	}

	public static interface ICycleItemEnum {
		default public String getItemNameKey() {
			return name();
		}

		public ItemIcon getItemIcon();

		public String name();
	}

	@RequiredArgsConstructor
	public static class CycleItem {
		private final @Getter String itemNameKey;
		private final @Getter ItemIcon itemIcon;
	}

	public static class EnumCycleItem extends CycleItem {
		private final @Getter ICycleItemEnum enumValue;

		public EnumCycleItem(ICycleItemEnum enumValue) {
			super(enumValue.getItemNameKey(), enumValue.getItemIcon());

			this.enumValue = enumValue;
		}
	}

	@RequiredArgsConstructor
	private class CycleClickListener implements ButtonClickListener {
		private final int indexChange;

		@Override
		public void onClick(KButton button, Player player) {
			currentIndex += indexChange;

			if(currentIndex < 0) {
				currentIndex = items.length - 1;
			} else if(currentIndex >= items.length) {
				currentIndex = 0;
			}
		}
	}
}