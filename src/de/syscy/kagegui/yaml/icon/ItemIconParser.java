package de.syscy.kagegui.yaml.icon;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import de.syscy.kagegui.icon.ItemIcon;

public class ItemIconParser {
	public static ItemIcon parseIcon(ConfigurationSection iconSection) {
		if(iconSection.contains("icon")) {
			return parseIcon(iconSection.getConfigurationSection("icon"));
		} else if(iconSection.contains("material")) {
			Material material = Material.matchMaterial(iconSection.getString("material"));

			return new ItemIcon(material);
		} else if(iconSection.contains("itemStack")) {
			return new ItemIcon(iconSection.getItemStack("itemStack"));
		}

		return new ItemIcon(Material.AIR);
	}
}