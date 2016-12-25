package de.syscy.kagegui.util;

import de.syscy.kagegui.icon.ItemIcon;
import de.syscy.kagegui.inventory.KGUI;
import de.syscy.kagegui.inventory.component.KComponent;
import de.syscy.kagegui.inventory.component.KLabel;

public class MessageGUI extends KGUI {
	public MessageGUI(String title, String message, ItemIcon messageIcon) {
		this(title, KLabel.builder().title(message).icon(messageIcon));
	}

	public MessageGUI(String title, KComponent.Builder<?> messageComponentBuilder) {
		super();

		setTitle(title);
		setSize(HOPPER_INVENTORY_SIZE);

		messageComponentBuilder.x(0).y(0).width(5).height(1);
		add(messageComponentBuilder.build());
	}
}