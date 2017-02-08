package de.syscy.kagegui.yaml.crafting;

import org.bukkit.configuration.ConfigurationSection;

import de.syscy.kagegui.crafting.KCraftingComponent;

public interface ICraftingComponentFactory {
	public KCraftingComponent createComponent(final YamlCraftingGUI gui, ConfigurationSection componentSection);
}