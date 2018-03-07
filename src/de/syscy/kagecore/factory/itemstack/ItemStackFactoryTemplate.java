package de.syscy.kagecore.factory.itemstack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.syscy.kagecore.KageCore;
import de.syscy.kagecore.factory.AdventureFactory;
import de.syscy.kagecore.factory.FactoryTemplate;
import de.syscy.kagecore.factory.IFactoryProviderPlugin;
import de.syscy.kagecore.factory.itemstack.ItemStackFactory.ItemStackTemplateModifier;
import de.syscy.kagecore.util.ItemAttributes;
import de.syscy.kagecore.util.ItemAttributes.Attribute;
import de.syscy.kagecore.util.ItemAttributes.Attribute.Builder;
import de.syscy.kagecore.util.ItemAttributes.AttributeType;
import de.syscy.kagecore.util.ItemAttributes.Slot;
import de.syscy.kagecore.versioncompat.reflect.Reflect;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ItemStackFactoryTemplate implements FactoryTemplate<ItemStack> {
	private static Map<String, CustomItemModifier<?>> customItemModifiers = new HashMap<>();

	@SuppressWarnings("unused")
	private final ItemFactoryNMS itemFactoryNMS;

	private ItemStackFactory itemStackFactory;
	private YamlConfiguration templateYaml;

	private Material material;
	private int data;
	//	private String nbt;

	private String displayName;
	private final List<String> lore = new ArrayList<>();
	private final List<EnchantmentTemplatePart> enchantments = new ArrayList<>();
	private ItemFlag[] itemFlags = new ItemFlag[0];
	private boolean unbreakable;

	private List<Attribute> itemAttributeList = new ArrayList<>();

	@Override
	public void load(AdventureFactory<ItemStack> factory, String templateName, YamlConfiguration templateYaml) throws Exception {
		itemStackFactory = (ItemStackFactory) factory;
		this.templateYaml = templateYaml;

		material = Material.matchMaterial(templateYaml.getString("material"));

		if(material == null) {
			KageCore.debugMessage("Invalid material: " + templateYaml.getString("material"));
		}

		data = templateYaml.getInt("data", 0);

		displayName = ChatColor.translateAlternateColorCodes('&', templateYaml.getString("displayName", ""));

		if(displayName != null && !displayName.trim().isEmpty()) {
			displayName = ChatColor.RESET + displayName;
		}

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
									modifierBuilder.slot(Slot.fromString(slotName));
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
	@SuppressWarnings("deprecation")
	public ItemStack create(Object... args) throws Exception {
		if(material == Material.AIR) {
			return new ItemStack(material);
		}

		CraftItemStack itemStack = CraftItemStack.asCraftCopy(new ItemStack(material, 1, material.getMaxDurability(), (byte) data)); //TODO: NBT support

		ItemMeta itemMeta = itemStack.getItemMeta();

		if(!displayName.isEmpty()) {
			itemMeta.setDisplayName(displayName);
		}

		itemMeta.setLore(lore);

		for(EnchantmentTemplatePart enchantment : enchantments) {
			itemMeta.addEnchant(enchantment.getEnchantment(), enchantment.getLevel(), true);
		}

		itemMeta.setUnbreakable(unbreakable);

		itemMeta.addItemFlags(itemFlags);

		ConfigurationSection customItemModifierSection = templateYaml.getConfigurationSection("customData");

		if(customItemModifierSection != null) {
			for(String key : customItemModifierSection.getKeys(false)) {
				String modifierName = key.toLowerCase();

				if(customItemModifiers.containsKey(modifierName)) {
					try {
						customItemModifiers.get(modifierName).modifyItem0(itemStackFactory.getPlugin(), itemStack, itemMeta, customItemModifierSection.getConfigurationSection(key));
					} catch(Exception ex) {
						throw new RuntimeException("Error in item modifier for \"" + key + "\"", ex);
					}
				}
			}
		}

		itemStack.setItemMeta(itemMeta);

		for(ItemStackTemplateModifier itemStackTemplateModifier : ((ItemStackFactory) itemStackFactory).getItemStackTemplateModifier()) {
			itemStackTemplateModifier.modify(itemStack, templateYaml);
		}

		if(!itemAttributeList.isEmpty()) {
			ItemAttributes itemAttributes = new ItemAttributes(itemStack);

			for(Attribute attribute : itemAttributeList) {
				itemAttributes.add(attribute);
			}

			itemStack = (CraftItemStack) itemAttributes.getStack();
		} else {
			Reflect.on(itemStack.getItemMeta()).set("internalTag", null);
			((Map<?, ?>) Reflect.on(itemStack.getItemMeta()).get("unhandledTags")).clear();
		}

		return itemStack;
	}

	@RequiredArgsConstructor
	private static class EnchantmentTemplatePart {
		private final @Getter Enchantment enchantment;
		private final @Getter int level;
	}

	private static abstract class CustomItemModifier<T> {
		protected abstract void modifyItem(final IFactoryProviderPlugin plugin, ItemStack itemStack, final T itemMeta, final ConfigurationSection specialMetaSection);

		public void modifyItem0(final IFactoryProviderPlugin plugin, ItemStack itemStack, final ItemMeta itemMeta, final ConfigurationSection specialMetaSection) {
			modifyItem(plugin, itemStack, (T) itemMeta, specialMetaSection);
		}

		protected Color parseColor(String value) {
			DyeColor dyeColor = null;

			try {
				dyeColor = DyeColor.valueOf(value.toUpperCase());
			} catch(Exception ex) {

			}

			return dyeColor == null ? null : dyeColor.getColor();
		}
	}

	public static void registerCustomItemModifier(String name, CustomItemModifier<?> itemModifier) {
		customItemModifiers.put(name.toLowerCase(), itemModifier);
	}

	static {
		customItemModifiers.put("potion", new CustomItemModifier<PotionMeta>() {
			@Override
			protected void modifyItem(IFactoryProviderPlugin plugin, ItemStack itemStack, PotionMeta potionMeta, ConfigurationSection specialMetaSection) {
				PotionType potionType = parsePotionType(specialMetaSection.getString("type"));
				boolean extended = specialMetaSection.getBoolean("extended", false);
				boolean upgraded = specialMetaSection.getBoolean("upgraded", false);

				PotionData potionData = new PotionData(potionType, extended, upgraded);
				potionMeta.setBasePotionData(potionData);

				Color color = parseColor(specialMetaSection.getString("color", ""));

				if(color != null) {
					potionMeta.setColor(color);
				}

				if(specialMetaSection.contains("customEffects")) {
					for(String customEffectKey : specialMetaSection.getConfigurationSection("customEffects").getKeys(false)) {
						ConfigurationSection customEffectSection = specialMetaSection.getConfigurationSection("customEffects." + customEffectKey);

						PotionEffectType customEffectType = PotionEffectType.getByName(customEffectSection.getString("type", ""));
						int duration = customEffectSection.getInt("duration", 20);
						int amplifier = customEffectSection.getInt("amplifier", 0);
						boolean ambient = customEffectSection.getBoolean("ambient", false);
						boolean particles = customEffectSection.getBoolean("particles", true);
						Color customEffectColor = parseColor(customEffectSection.getString("color", ""));
						potionMeta.addCustomEffect(new PotionEffect(customEffectType, duration, amplifier, ambient, particles, customEffectColor), true);
					}
				}
			}

			private PotionType parsePotionType(String value) {
				PotionType potionType = PotionType.UNCRAFTABLE;

				try {
					potionType = PotionType.valueOf(value.toUpperCase());
				} catch(Exception ex) {

				}

				return potionType;
			}
		});
	}
}