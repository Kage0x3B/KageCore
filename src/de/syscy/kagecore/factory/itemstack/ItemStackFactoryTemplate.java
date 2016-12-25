package de.syscy.kagecore.factory.itemstack;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.syscy.kagecore.factory.AdventureFactory;
import de.syscy.kagecore.factory.FactoryTemplate;
import de.syscy.kagecore.factory.itemstack.ItemStackFactory.ItemStackTemplateModifier;
import de.syscy.kagecore.util.ItemAttributes;
import de.syscy.kagecore.util.ItemAttributes.Attribute;
import de.syscy.kagecore.util.ItemAttributes.Attribute.Builder;
import de.syscy.kagecore.util.ItemAttributes.AttributeType;
import de.syscy.kagecore.util.ItemAttributes.Slot;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_11_R1.MojangsonParser;

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

	private List<Attribute> itemAttributeList = new ArrayList<>();

	@Override
	public void load(AdventureFactory<ItemStack> factory, String templateName, YamlConfiguration templateYaml) throws Exception {
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

		if(templateYaml.contains("attributes")) {
			ConfigurationSection attributesSection = templateYaml.getConfigurationSection("attributes");

			for(AttributeType attributeType : AttributeType.values()) {
				String attributeName = attributeType.getMinecraftID();

				if(!attributesSection.contains(attributeName)) {
					attributeName = attributeType.getMinecraftID().toLowerCase();
				}

				if(!attributesSection.contains(attributeName)) {
					attributeName = attributeType.getMinecraftID().toUpperCase();
				}

				if(attributesSection.contains(attributeName)) {
					ConfigurationSection currentAttributeSection = attributesSection.getConfigurationSection(attributeName);

					for(String modifierName : currentAttributeSection.getKeys(false)) {
						try {
							String operationName = currentAttributeSection.getString(modifierName + ".operation", "ADD_NUMBER");
							String slotName = currentAttributeSection.getString(modifierName + ".slot", "");
							double amount = currentAttributeSection.getDouble(modifierName + ".amount");
							Operation operation = Operation.valueOf(operationName.toUpperCase());

							Builder modifierBuilder = Attribute.newBuilder();
							modifierBuilder.name(modifierName);
							modifierBuilder.type(attributeType);
							modifierBuilder.operation(operation);

							if(!slotName.isEmpty()) {
								try {
									modifierBuilder.slot(Slot.valueOf(slotName.toUpperCase()));
								} catch(Exception ex) {
									ex.printStackTrace();
								}
							}

							modifierBuilder.amount(amount);
							itemAttributeList.add(modifierBuilder.build());
						} catch(Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}
		}
	}

	@Override
	public ItemStack create(Object... args) throws Exception {
		net.minecraft.server.v1_11_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(new ItemStack(material));

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

		ItemAttributes itemAttributes = new ItemAttributes(itemStack);

		for(Attribute attribute : itemAttributeList) {
			itemAttributes.add(attribute);
		}

		return itemAttributes.getStack();
	}

	@RequiredArgsConstructor
	private static class EnchantmentTemplatePart {
		private final @Getter Enchantment enchantment;
		private final @Getter int level;
	}
}