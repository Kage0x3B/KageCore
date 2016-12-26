package de.syscy.kagegui.util;

import de.syscy.kagegui.icon.ItemIcon;
import de.syscy.kagegui.inventory.KGUI;
import de.syscy.kagegui.inventory.component.KComponent;
import de.syscy.kagegui.inventory.component.KLabel;

public class MessageGUI extends KGUI {
	public MessageGUI(String title, String message, ItemIcon messageIcon) {
		this(title, createMessageLabel(message, messageIcon));
	}

	public MessageGUI(String title, KComponent messageComponent) {
		super();

		setTitle(title);
		setSize(HOPPER_INVENTORY_SIZE);

		messageComponent.setX(0);
		messageComponent.setY(0);
		messageComponent.setWidth(5);
		messageComponent.setHeight(1);

		add(messageComponent);
	}

	private static KLabel createMessageLabel(String message, ItemIcon messageIcon) {
		KLabel messageLabel = new KLabel(0, 0);
		messageLabel.setTitle(message);
		messageLabel.setIcon(messageIcon);

		return messageLabel;
	}
}