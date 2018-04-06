package de.syscy.kagecore.factory.itemstack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import de.syscy.kagecore.factory.AbstractFactory;
import de.syscy.kagecore.factory.IFactoryTemplate;
import de.syscy.kagecore.factory.IFactoryProviderPlugin;
import de.syscy.kagecore.factory.InvalidTemplateException;
import de.syscy.kagecore.versioncompat.VersionCompatClassLoader;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import lombok.Getter;

public class ItemStackFactory extends AbstractFactory<ItemStack> implements IItemStackFactory {
	private final @Getter IFactoryProviderPlugin plugin;
	private final ItemFactoryNMS itemFactoryNMS;

	private final LoadingCache<String, ItemStack> cache;

	private @Getter List<ItemStackModifier> fallbackItemStacksModifier = new ArrayList<>();
	private @Getter List<ItemStackTemplateModifier> itemStackTemplateModifier = new ArrayList<>();

	public ItemStackFactory(IFactoryProviderPlugin plugin) {
		this.plugin = plugin;

		itemFactoryNMS = VersionCompatClassLoader.loadClass(ItemFactoryNMS.class);

		cache = CacheBuilder.newBuilder().build(new CacheLoader<String, ItemStack>() {
			@Override
			public ItemStack load(String templateName) throws Exception {
				return loadTemplate(templateName);
			}
		});
	}

	@Override
	public void loadTemplates() {
		loadTemplates(new File(plugin.getDataFolder(), "itemTemplates"), "ait");
	}

	public ItemStack create(String templateName) {
		if(templateName == null || templateName.isEmpty()) {
			return null;
		}

		try {
			return cache.get(templateName).clone();
		} catch(ExecutionException ex) {
			ex.printStackTrace();
		}

		return null;
	}

	private ItemStack loadTemplate(String templateName) throws Exception {
		IFactoryTemplate<ItemStack> template = templates.get(templateName.toLowerCase());

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

		ItemStack itemStack = template.create();

		if(itemStack != null) {
			return itemStack;
		}

		throw new InvalidTemplateException("There is no template with the name \"" + templateName.toLowerCase() + "\"!");
	}

	@Override
	public void reload() {
		cache.cleanUp();

		super.reload();
	}

	@Override
	protected IFactoryTemplate<ItemStack> createTemplate() {
		return new ItemStackFactoryTemplate(itemFactoryNMS);
	}

	public static interface ItemStackModifier {
		public void modify(ItemStack itemStack);
	}

	public static interface ItemStackTemplateModifier {
		public void modify(ItemStack itemStack, YamlConfiguration templateYaml);
	}
}