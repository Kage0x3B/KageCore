package de.syscy.kagegui.yaml;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;

import de.syscy.kagegui.KageGUI;
import de.syscy.kagegui.inventory.KGUI;

public class YamlGUI extends KGUI {
	public YamlGUI(String guiName) {
		File guiYamlFile = new File(KageGUI.getGuiDirectory(), guiName + ".yml");

		if(!guiYamlFile.exists()) {
			throw new RuntimeException("Can not find gui file " + guiName + ".yml!");
		}

		YamlConfiguration guiYaml = YamlConfiguration.loadConfiguration(guiYamlFile);

	}
}