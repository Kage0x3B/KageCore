package de.syscy.kagegui.yaml.inventory;

import org.bukkit.configuration.ConfigurationSection;

import de.syscy.kagegui.inventory.component.KComponent;
import de.syscy.kagegui.inventory.component.KItemSpinner;

public class KItemSpinnerFactory implements IKComponentFactory {
	@Override
	public KComponent createComponent(final YamlGUI gui, ConfigurationSection componentSection) {
		KItemSpinner itemSpinner = new KItemSpinner(componentSection.getInt("x", 0), componentSection.getInt("y", 0));
		itemSpinner.setWidth(componentSection.getInt("width", 1));
		itemSpinner.setHeight(componentSection.getInt("height", 1));

		return null; //TODO
	}
}