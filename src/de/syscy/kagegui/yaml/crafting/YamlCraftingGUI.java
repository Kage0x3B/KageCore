package de.syscy.kagegui.yaml.crafting;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import de.syscy.kagecore.KageCore;
import de.syscy.kagecore.versioncompat.reflect.Reflect;
import de.syscy.kagegui.KageGUI;
import de.syscy.kagegui.crafting.KCraftingComponent;
import de.syscy.kagegui.crafting.KCraftingGUI;
import de.syscy.kagegui.yaml.IYamlGUI;
import lombok.Getter;

public class YamlCraftingGUI extends KCraftingGUI implements IYamlGUI<KCraftingComponent> {
	private @Getter Map<String, KCraftingComponent> components = new HashMap<>();

	private @Getter Reflect controllerClass;

	public YamlCraftingGUI(String guiName) {
		super();

		File guiYamlFile = new File(KageGUI.getGuiDirectory(), guiName + ".yml");

		if(!guiYamlFile.exists()) {
			throw new RuntimeException("Can not find gui file " + guiName + ".yml!");
		}

		YamlConfiguration guiYaml = YamlConfiguration.loadConfiguration(guiYamlFile);

		if(guiYaml.contains("controllerClass")) {
			String controllerClassPath = guiYaml.getString("controllerClass");

			controllerClass = Reflect.on(controllerClassPath).create();
		}

		ConfigurationSection componentsSection = guiYaml.getConfigurationSection("components");

		if(componentsSection.contains("componentProvider")) {
			String componentProviderClassPath = guiYaml.getString("componentProvider");

			Object componentProvider = Reflect.on(componentProviderClassPath).create().get();

			if(componentProvider instanceof ICraftingComponentProvider) {
				for(KCraftingComponent component : ((ICraftingComponentProvider) componentProvider).provideComponents(componentsSection)) {
					add(component);
				}
			} else {
				KageCore.debugMessage("Invalid IComponentProvider: " + componentProvider);
			}
		}

		for(String componentKey : componentsSection.getKeys(false)) {
			KCraftingComponent component = KCraftingComponentFactory.createComponent(this, componentsSection.getConfigurationSection(componentKey));

			if(component != null) {
				add(component);
				components.put(componentKey, component);
			}
		}
	}
}