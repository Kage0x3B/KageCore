package de.syscy.kagegui.yaml.inventory;

import org.bukkit.configuration.ConfigurationSection;

import de.syscy.kagegui.inventory.component.KComponent;

public interface IKComponentFactory {
	public KComponent createComponent(final YamlGUI gui, ConfigurationSection componentSection);
}