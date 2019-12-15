package de.syscy.kagegui.yaml.inventory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.InventoryCloseEvent;

import de.syscy.kagecore.KageCore;
import de.syscy.kagecore.versioncompat.reflect.Reflect;
import de.syscy.kagegui.KageGUI;
import de.syscy.kagegui.inventory.KGUI;
import de.syscy.kagegui.inventory.component.KComponent;
import de.syscy.kagegui.yaml.IYamlGUI;
import de.syscy.kagegui.yaml.action.ActionParser;
import de.syscy.kagegui.yaml.action.IAction;
import lombok.Getter;

public class YamlGUI extends KGUI implements IYamlGUI<KComponent> {
	private @Getter Map<String, KComponent> components = new HashMap<>();

	private @Getter Reflect controllerClass;

	private IAction closeAction;

	public YamlGUI(String guiName) {
		super();

		File guiYamlFile = new File(KageGUI.getGuiDirectory(), guiName + ".yml");

		if(!guiYamlFile.exists()) {
			throw new RuntimeException("Can not find gui file " + guiName + ".yml!");
		}

		YamlConfiguration guiYaml = YamlConfiguration.loadConfiguration(guiYamlFile);

		setTitle(guiYaml.getString("title", "Invalid title in GUI YAML"));

		setSize(guiYaml.getInt("size", 9));

		if(guiYaml.contains("height")) {
			setHeight(guiYaml.getInt("height", 1));
		}

		if(guiYaml.contains("controllerClass")) {
			String controllerClassPath = guiYaml.getString("controllerClass");

			controllerClass = Reflect.on(controllerClassPath).create();
		}

		if(guiYaml.contains("closeAction")) {
			closeAction = ActionParser.parseAction(guiYaml.getConfigurationSection("closeAction"));
		}

		ConfigurationSection componentsSection = guiYaml.getConfigurationSection("components");

		if(componentsSection.contains("componentProvider")) {
			String componentProviderClassPath = guiYaml.getString("componentProvider");

			Object componentProvider = Reflect.on(componentProviderClassPath).create().get();

			if(componentProvider instanceof IComponentProvider) {
				for(KComponent component : ((IComponentProvider) componentProvider).provideComponents(componentsSection)) {
					add(component);
				}
			} else {
				KageCore.debugMessage("Invalid IComponentProvider: " + componentProvider);
			}
		}

		for(String componentKey : componentsSection.getKeys(false)) {
			KComponent component = KComponentFactory.createComponent(this, componentsSection.getConfigurationSection(componentKey));

			if(component != null) {
				add(component);
				components.put(componentKey, component);
			}
		}
	}

	@Override
	public void onClose(InventoryCloseEvent event) {
		super.onClose(event);

		closeAction.onTrigger(this, player);
	}
}