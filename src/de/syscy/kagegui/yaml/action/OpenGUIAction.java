package de.syscy.kagegui.yaml.action;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import de.syscy.kagecore.versioncompat.reflect.Reflect;
import de.syscy.kagegui.KageGUI;
import de.syscy.kagegui.inventory.KGUI;
import de.syscy.kagegui.yaml.IYamlGUI;
import de.syscy.kagegui.yaml.inventory.YamlGUI;

public class OpenGUIAction implements IAction {
	private final String guiName;
	private final String guiClass;

	public OpenGUIAction(ConfigurationSection actionSection) {
		guiName = actionSection.getString("guiName", "");
		guiClass = actionSection.getString("guiClass", "");
	}

	@Override
	public void onTrigger(IYamlGUI<?> gui, Player player, Object... args) {
		if(!guiName.isEmpty()) {
			KageGUI.showGUI(new YamlGUI(guiName), player);
		} else if(!guiClass.isEmpty()) {
			KageGUI.showGUI(Reflect.on(guiClass).create().<KGUI> get(), player);
		}
	}
}