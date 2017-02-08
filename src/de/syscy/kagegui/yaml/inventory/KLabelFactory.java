package de.syscy.kagegui.yaml.inventory;

import org.bukkit.configuration.ConfigurationSection;

import de.syscy.kagegui.inventory.component.KComponent;
import de.syscy.kagegui.inventory.component.KLabel;
import de.syscy.kagegui.util.LoreBuilder;
import de.syscy.kagegui.yaml.icon.ItemIconParser;

public class KLabelFactory implements IKComponentFactory {
	@Override
	public KComponent createComponent(final YamlGUI gui, ConfigurationSection componentSection) {
		KLabel label = new KLabel(componentSection.getInt("x", 0), componentSection.getInt("y", 0));
		label.setWidth(componentSection.getInt("width", 1));
		label.setHeight(componentSection.getInt("height", 1));
		label.setTitle(componentSection.getString("title", ""));
		label.setIcon(ItemIconParser.parseIcon(componentSection));

		if(componentSection.contains("lore")) {
			if(componentSection.isString("lore")) {
				label.getLoreBuilder().set(LoreBuilder.DEFAULT_DESCRIPTION, componentSection.getString("lore"));
			} else if(componentSection.isList("lore")) {
				label.getLoreBuilder().set(LoreBuilder.DEFAULT_DESCRIPTION, componentSection.getStringList("lore"));
			}
		}

		return label;
	}
}