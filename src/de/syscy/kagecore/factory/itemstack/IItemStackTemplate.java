package de.syscy.kagecore.factory.itemstack;

import de.syscy.kagecore.factory.IFactoryTemplate;
import de.syscy.kagecore.util.ItemAttributes;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface IItemStackTemplate extends IFactoryTemplate<ItemStack> {
	ItemStackFactory getItemStackFactory();
	YamlConfiguration getTemplateYaml();

	Material getMaterial();
	int getData();

	String getDisplayName();
	List<String> getLore();
	List<ItemStackTemplate.EnchantmentTemplatePart> getEnchantments();
	ItemFlag[] getItemFlags();

	boolean isUnbreakable();

	List<ItemAttributes.Attribute> getItemAttributeList();
}