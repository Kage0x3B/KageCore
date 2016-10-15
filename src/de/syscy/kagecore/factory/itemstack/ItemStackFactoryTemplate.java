package de.syscy.kagecore.factory.itemstack;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.syscy.kagecore.factory.AdventureFactory;
import de.syscy.kagecore.factory.FactoryTemplate;
import de.syscy.kagecore.factory.itemstack.ItemStackFactory.ItemStackTemplateModifier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_10_R1.MojangsonParser;

public class ItemStackFactoryTemplate implements FactoryTemplate<ItemStack> {
	private AdventureFactory<ItemStack> factory;
	private YamlConfiguration templateYaml;

	private Material material;
	private int data;
	private String nbt;

	private String displayName;
	private final List<String> lore = new ArrayList<>();
	private final List<EnchantmentTemplatePart> enchantments = new ArrayList<>();
	private ItemFlag[] itemFlags = new ItemFlag[0];
	private boolean unbreakable;

	@Override
	public void load(AdventureFactory<ItemStack> factory, YamlConfiguration templateYaml) throws Exception {
		this.factory = factory;
		this.templateYaml = templateYaml;

		material = Material.matchMaterial(templateYaml.getString("material", "barrier"));
		data = templateYaml.getInt("data", 0);
		nbt = templateYaml.getString("nbt", "");

		displayName = ChatColor.translateAlternateColorCodes('$', templateYaml.getString("displayName", ""));

		int i = 1;

		while(templateYaml.contains("lore.line" + i)) {
			lore.add(ChatColor.translateAlternateColorCodes('$', templateYaml.getString("lore.line" + i)));

			i++;
		}

		if(templateYaml.contains("enchantments")) {
			for(String enchantmentName : templateYaml.getConfigurationSection("enchantments").getKeys(false)) {
				Enchantment enchantment = Enchantment.getByName(enchantmentName.toUpperCase());
				int level = templateYaml.getInt("enchantments." + enchantmentName, 1);

				enchantments.add(new EnchantmentTemplatePart(enchantment, level));
			}
		}

		if(templateYaml.isList("itemFlags")) {
			List<ItemFlag> itemFlagList = new ArrayList<>();

			for(String itemFlagName : templateYaml.getStringList("itemFlags")) {
				try {
					ItemFlag itemFlag = ItemFlag.valueOf(itemFlagName.toUpperCase());

					if(itemFlag != null) {
						itemFlagList.add(itemFlag);
					}
				} catch(Exception ex) {

				}
			}

			itemFlags = itemFlagList.toArray(new ItemFlag[itemFlagList.size()]);
		} else if(templateYaml.isString("itemFlags")) {
			if(templateYaml.getString("itemFlags").equalsIgnoreCase("all")) {
				itemFlags = ItemFlag.values();
			}
		}

		unbreakable = templateYaml.getBoolean("unbreakable", false);
	}

	@Override
	public ItemStack create(Object... args) throws Exception {
		net.minecraft.server.v1_10_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(new ItemStack(material));
		nmsItemStack.setData(data);

		if(nbt != null && !nbt.isEmpty()) {
			nmsItemStack.setTag(MojangsonParser.parse(nbt));
		}

		CraftItemStack itemStack = CraftItemStack.asCraftMirror(nmsItemStack);
		ItemMeta itemMeta = itemStack.getItemMeta();

		if(!displayName.isEmpty()) {
			itemMeta.setDisplayName(displayName);
		}

		itemMeta.setLore(lore);

		for(EnchantmentTemplatePart enchantment : enchantments) {
			itemMeta.addEnchant(enchantment.getEnchantment(), enchantment.getLevel(), true);
		}

		itemMeta.spigot().setUnbreakable(unbreakable);

		itemMeta.addItemFlags(itemFlags);
		itemStack.setItemMeta(itemMeta);

		for(ItemStackTemplateModifier itemStackTemplateModifier : ((ItemStackFactory) factory).getItemStackTemplateModifier()) {
			itemStackTemplateModifier.modify(itemStack, templateYaml);
		}

		return itemStack;
	}

	@RequiredArgsConstructor
	private static class EnchantmentTemplatePart {
		private final @Getter Enchantment enchantment;
		private final @Getter int level;
	}
}