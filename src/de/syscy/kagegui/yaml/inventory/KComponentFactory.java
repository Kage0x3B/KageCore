package de.syscy.kagegui.yaml.inventory;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import de.syscy.kagegui.inventory.component.KComponent;

public class KComponentFactory {
	private static Map<String, IKComponentFactory> componentFactories = new HashMap<>();

	public static void init() {
		componentFactories.put("button", new KButtonFactory());
		componentFactories.put("checkButton", new KCheckButtonFactory());
		componentFactories.put("itemContainer", new KItemContainerFactory());
		componentFactories.put("itemSpinner", new KItemSpinnerFactory());
		componentFactories.put("label", new KLabelFactory());
		componentFactories.put("list", new KListFactory());
		componentFactories.put("slider", new KSliderFactory());
		componentFactories.put("textInput", new KTextInputFactory());
	}

	public static KComponent createComponent(final YamlGUI gui, ConfigurationSection componentSection) {
		String type = componentSection.getString("type", "");

		IKComponentFactory componentFactory = componentFactories.get(type.toLowerCase());

		if(componentFactory != null) {
			return componentFactory.createComponent(gui, componentSection);
		} else {
			return null;
		}
	}
}