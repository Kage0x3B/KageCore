package de.syscy.kagegui.util;

import org.bukkit.event.inventory.InventoryAction;

import de.syscy.kagecore.translation.Translator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ClickType {
	//@formatter:off
	GENERAL(Translator.SIGN + "kgui.loreButtonPrefix.click;", null),
	LEFT_CLICK(Translator.SIGN + "kgui.loreButtonPrefix.leftClick;", InventoryAction.PICKUP_ALL),
	RIGHT_CLICK(Translator.SIGN + "kgui.loreButtonPrefix.rightClick;", InventoryAction.PICKUP_HALF),
	SHIFT_CLICK(Translator.SIGN + "kgui.loreButtonPrefix.shiftClick;", InventoryAction.MOVE_TO_OTHER_INVENTORY),
	DROP(Translator.SIGN + "kgui.loreButtonPrefix.drop;", InventoryAction.DROP_ONE_SLOT),
	CTRL_DROP(Translator.SIGN + "kgui.loreButtonPrefix.ctrlDrop;", InventoryAction.DROP_ALL_SLOT);
	//@formatter:on

	public static final ClickType[] VALUES = values();

	private final @Getter String loreButtonPrefix;
	private final @Getter InventoryAction inventoryAction;

	public static ClickType getType(InventoryAction inventoryAction) {
		for(ClickType clickType : VALUES) {
			if(inventoryAction.equals(clickType.getInventoryAction())) {
				return clickType;
			}
		}

		return ClickType.GENERAL;
	}
}