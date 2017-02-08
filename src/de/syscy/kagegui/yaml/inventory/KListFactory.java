package de.syscy.kagegui.yaml.inventory;

import org.bukkit.configuration.ConfigurationSection;

import de.syscy.kagecore.KageCore;
import de.syscy.kagecore.versioncompat.reflect.Reflect;
import de.syscy.kagegui.inventory.component.KComponent;
import de.syscy.kagegui.inventory.component.KList;
import de.syscy.kagegui.yaml.icon.ItemIconParser;

public class KListFactory implements IKComponentFactory {
	@Override
	public KComponent createComponent(final YamlGUI gui, ConfigurationSection componentSection) {
		KList list = new KList(componentSection.getInt("x", 0), componentSection.getInt("y", 0));
		list.setWidth(componentSection.getInt("width", 1));
		list.setHeight(componentSection.getInt("height", 1));
		list.setListBottomMargin(componentSection.getInt("listBottomMargin", 0));

		if(componentSection.contains("previousPageIcon")) {
			list.setPreviousPageIcon(ItemIconParser.parseIcon(componentSection.getConfigurationSection("previousPageIcon")));
		}

		if(componentSection.contains("nextPageIcon")) {
			list.setNextPageIcon(ItemIconParser.parseIcon(componentSection.getConfigurationSection("nextPageIcon")));
		}

		ConfigurationSection componentsSection = componentSection.getConfigurationSection("components");

		if(componentsSection.contains("componentProvider")) {
			String componentProviderClassPath = componentSection.getString("componentProvider");

			Object componentProvider = Reflect.on(componentProviderClassPath).create().get();

			if(componentProvider instanceof IComponentProvider) {
				for(KComponent component : ((IComponentProvider) componentProvider).provideComponents(componentsSection)) {
					list.add(component);
				}
			} else {
				KageCore.debugMessage("Invalid IComponentProvider: " + componentProvider);
			}
		} else {
			for(String componentKey : componentsSection.getKeys(false)) {
				KComponent component = KComponentFactory.createComponent(gui, componentsSection.getConfigurationSection(componentKey));

				if(component != null) {
					list.add(component);
				}
			}
		}

		ConfigurationSection navigationComponentsSection = componentSection.getConfigurationSection("navigationComponents");

		if(navigationComponentsSection.contains("componentProvider")) {
			String componentProviderClassPath = componentSection.getString("componentProvider");

			Object componentProvider = Reflect.on(componentProviderClassPath).create().get();

			if(componentProvider instanceof IComponentProvider) {
				for(KComponent component : ((IComponentProvider) componentProvider).provideComponents(navigationComponentsSection)) {
					list.addNavigationComponent(component.getX(), component);
				}
			} else {
				KageCore.debugMessage("Invalid IComponentProvider: " + componentProvider);
			}
		} else {
			for(String componentKey : navigationComponentsSection.getKeys(false)) {
				KComponent component = KComponentFactory.createComponent(gui, navigationComponentsSection.getConfigurationSection(componentKey));

				if(component != null) {
					list.addNavigationComponent(component.getX(), component);
				}
			}
		}

		return list;
	}
}