package de.syscy.kagegui.yaml.crafting;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import de.syscy.kagegui.crafting.KCraftingComponent;

public class KCraftingComponentFactory {
	private static Map<String, ICraftingComponentFactory> componentFactories = new HashMap<>();

	public static void init() {
		componentFactories.put("button", new KCraftingButtonFactory());
	}

	public static KCraftingComponent createComponent(final YamlCraftingGUI gui, ConfigurationSection componentSection) {
		String type = componentSection.getString("type", "");

		ICraftingComponentFactory componentFactory = componentFactories.get(type.toLowerCase());

		if(componentFactory != null) {
			return componentFactory.createComponent(gui, componentSection);
		} else {
			return null;
		}
	}
}