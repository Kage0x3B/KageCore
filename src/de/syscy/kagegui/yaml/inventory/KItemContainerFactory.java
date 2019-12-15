package de.syscy.kagegui.yaml.inventory;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import de.syscy.kagegui.inventory.component.KComponent;
import de.syscy.kagegui.inventory.component.KItemContainer;

public class KItemContainerFactory implements IKComponentFactory {
	@Override
	public KComponent createComponent(final YamlGUI gui, ConfigurationSection componentSection) {
		KItemContainer itemContainer = new KItemContainer(componentSection.getInt("x", 0), componentSection.getInt("y", 0), componentSection.getItemStack("item", new ItemStack(Material.AIR)));
		itemContainer.setWidth(componentSection.getInt("width", 1));
		itemContainer.setHeight(componentSection.getInt("height", 1));

		itemContainer.setRefill(componentSection.getBoolean("refill", true));
		itemContainer.setEmpty(componentSection.getBoolean("empty", false));

		return itemContainer;
	}
}