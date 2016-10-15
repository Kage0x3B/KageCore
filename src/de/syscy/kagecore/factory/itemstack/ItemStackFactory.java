package de.syscy.kagecore.factory.itemstack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import de.syscy.kagecore.factory.AbstractAdventureFactory;
import de.syscy.kagecore.factory.FactoryTemplate;
import de.syscy.kagecore.factory.IFactoryProviderPlugin;
import de.syscy.kagecore.factory.InvalidTemplateException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ItemStackFactory extends AbstractAdventureFactory<ItemStack> {
	private final IFactoryProviderPlugin plugin;

	private @Getter List<ItemStackModifier> fallbackItemStacksModifier = new ArrayList<>();
	private @Getter List<ItemStackTemplateModifier> itemStackTemplateModifier = new ArrayList<>();

	protected Map<String, ItemStack> cache = new HashMap<>();

	@Override
	public void loadTemplates() {
		loadTemplates(new File(plugin.getDataFolder(), "itemTemplates"), "ait");
	}

	public ItemStack create(String templateName) {
		if(templateName == null || templateName.isEmpty()) {
			return null;
		}

		if(cache.containsKey(templateName)) {
			return cache.get(templateName).clone();
		}

		FactoryTemplate<ItemStack> template = templates.get(templateName.toLowerCase());

		if(template == null) {
			Material material = Material.matchMaterial(templateName);

			if(material != null) {
				ItemStack itemStack = new ItemStack(material);

				for(ItemStackModifier itemStackModifier : fallbackItemStacksModifier) {
					itemStackModifier.modify(itemStack);
				}

				return itemStack;
			} else {
				throw new InvalidTemplateException("There is no template with the name \"" + templateName.toLowerCase() + "\"!");
			}
		}

		try {
			ItemStack itemStack = template.create();

			if(itemStack != null) {
				cache.put(templateName, itemStack);

				return itemStack;
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	@Override
	public void reload() {
		cache.clear();

		super.reload();
	}

	@Override
	protected FactoryTemplate<ItemStack> createTemplate() {
		return new ItemStackFactoryTemplate();
	}

	public static interface ItemStackModifier {
		public void modify(ItemStack itemStack);
	}

	public static interface ItemStackTemplateModifier {
		public void modify(ItemStack itemStack, YamlConfiguration templateYaml);
	}
}