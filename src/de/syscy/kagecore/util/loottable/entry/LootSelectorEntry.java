package de.syscy.kagecore.util.loottable.entry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import de.syscy.kagecore.util.loottable.LootSelectorLootTable;
import de.syscy.kagecore.util.loottable.LootTableInfo;
import de.syscy.kagecore.util.loottable.condition.LootItemCondition;
import de.syscy.kagecore.util.loottable.value.LootValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public abstract class LootSelectorEntry implements ConfigurationSerializable {
	private @Getter LootValue weight;
	private @Getter LootValue quality;

	private @Getter List<LootItemCondition> conditions;

	public int getFinalWeight(Random random, float luck) {
		return Math.max(0, (int) Math.floor((float) weight.getValue(random) + (float) quality.getValue(random) * luck));
	}

	public abstract void generateLoot(Collection<ItemStack> itemStacks, Random random, LootTableInfo lootTableInfo);

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();

		if(!weight.isZero()) {
			map.put("weight", weight.serialize());
		}

		if(!quality.isZero()) {
			map.put("quality", quality.serialize());
		}

		int i = 0;

		for(LootItemCondition condition : conditions) {
			map.put("condition" + i, condition);

			i++;
		}

		return map;
	}

	public static LootSelectorEntry fromYaml(ConfigurationSection yaml) {
		String type = yaml.getString("type", "empty").toLowerCase().replaceAll("_", "");

		LootValue weight = LootValue.fromString(yaml.getString("weight", "0"));
		LootValue quality = LootValue.fromString(yaml.getString("quality", "0"));

		List<LootItemCondition> conditions = new ArrayList<>();

		for(String key : yaml.getKeys(false)) {
			if(key.startsWith("condition")) {
				conditions.add(LootItemCondition.fromYaml(yaml.getConfigurationSection(key)));
			}
		}

		switch(type) {
			case "item":
				return LootItem.fromYaml(yaml, weight, quality, conditions);
			case "loottable":
				return LootSelectorLootTable.fromYaml(yaml, weight, quality, conditions);
			case "empty":
			default:
				return LootSelectorEmpty.fromYaml(yaml, weight, quality, conditions);
		}
	}
}