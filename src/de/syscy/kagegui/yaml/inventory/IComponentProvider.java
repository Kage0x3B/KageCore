package de.syscy.kagegui.yaml.inventory;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import de.syscy.kagegui.inventory.component.KComponent;

public interface IComponentProvider {
	public List<KComponent> provideComponents(ConfigurationSection componentsSection);
}