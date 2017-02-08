package de.syscy.kagegui.util;

import org.bukkit.event.inventory.InventoryAction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ClickType {
	//@formatter:off
	GENERAL("§kgui.loreButtonPrefix.click;", null),
	LEFT_CLICK("§kgui.loreButtonPrefix.leftClick;", InventoryAction.PICKUP_ALL),
	RIGHT_CLICK("§kgui.loreButtonPrefix.rightClick;", InventoryAction.PICKUP_HALF),
	SHIFT_CLICK("§kgui.loreButtonPrefix.shiftClick;", InventoryAction.MOVE_TO_OTHER_INVENTORY),
	DROP("§kgui.loreButtonPrefix.drop;", InventoryAction.DROP_ONE_SLOT),
	CTRL_DROP("§kgui.loreButtonPrefix.ctrlDrop;", InventoryAction.DROP_ALL_SLOT);
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