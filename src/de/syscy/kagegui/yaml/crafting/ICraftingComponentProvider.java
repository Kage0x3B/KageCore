package de.syscy.kagegui.yaml.crafting;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import de.syscy.kagegui.crafting.KCraftingComponent;

public interface ICraftingComponentProvider {
	public List<KCraftingComponent> provideComponents(ConfigurationSection componentsSection);
}