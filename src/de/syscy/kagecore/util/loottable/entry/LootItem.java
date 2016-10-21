package de.syscy.kagecore.util.loottable.entry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.wrappers.nbt.NbtBase;

import de.syscy.kagecore.util.ItemAttributes;
import de.syscy.kagecore.util.loottable.LootTableInfo;
import de.syscy.kagecore.util.loottable.condition.LootItemCondition;
import de.syscy.kagecore.util.loottable.function.LootItemFunction;
import de.syscy.kagecore.util.loottable.value.LootValue;

public class LootItem extends LootSelectorEntry {
	private List<LootItemFunction> functions;
	private String templateName;

	private LootItem(LootValue weight, LootValue quality, List<LootItemCondition> conditions, String templateName, List<LootItemFunction> functions) {
		super(weight, quality, conditions);

		this.templateName = templateName;
		this.functions = functions;
	}

	@Override
	public void generateLoot(Collection<ItemStack> itemStacks, Random random, LootTableInfo lootTableInfo) {
		ItemStack itemStack = lootTableInfo.getLootTableRegistry().getPlugin().getItemStackFactory().create(templateName);
		NbtBase<?> attributesBackup = ItemAttributes.backup(itemStack);

		for(LootItemFunction lootItemFunction : functions) {
			if(!LootItemCondition.checkConditions(lootItemFunction.getConditions(), random, lootTableInfo)) {
				continue;
			}

			itemStack = lootItemFunction.apply(itemStack, random, lootTableInfo);
		}

		itemStack = ItemAttributes.restore(itemStack, attributesBackup);

		if(itemStack.getAmount() > 0) {
			if(itemStack.getAmount() < itemStack.getType().getMaxStackSize()) {
				itemStacks.add(itemStack);
			} else {
				ItemStack itemStack2 = itemStack.clone();

				for(int i = itemStack.getAmount(); i > 0; i -= itemStack2.getAmount()) {
					itemStack2 = itemStack.clone();
					itemStack2.setAmount(Math.min(itemStack.getMaxStackSize(), i));

					itemStacks.add(itemStack2);
				}
			}
		}
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = super.serialize();
		map.put("template", templateName);

		int i = 0;

		for(LootItemFunction function : functions) {
			map.put("function" + i, function);

			i++;
		}

		return map;
	}

	public static LootItem fromYaml(ConfigurationSection yaml, LootValue weight, LootValue quality, List<LootItemCondition> conditions) {
		String templateName = yaml.getString("template", "");

		List<LootItemFunction> functions = new ArrayList<>();

		for(String key : yaml.getKeys(false)) {
			if(key.startsWith("function")) {
				functions.add(LootItemFunction.fromYaml(yaml.getConfigurationSection(key)));
			}
		}

		return new LootItem(weight, quality, conditions, templateName, functions);
	}
}