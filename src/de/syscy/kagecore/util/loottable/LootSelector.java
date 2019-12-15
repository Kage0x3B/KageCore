package de.syscy.kagecore.util.loottable;

import de.syscy.kagecore.util.loottable.condition.LootItemCondition;
import de.syscy.kagecore.util.loottable.entry.LootSelectorEntry;
import de.syscy.kagecore.util.loottable.value.LootValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@AllArgsConstructor
public class LootSelector implements ConfigurationSerializable {
	private @Getter LootValue rolls;
	private @Getter LootValue bonusRolls;

	private @Getter List<LootSelectorEntry> entries;
	private @Getter List<LootItemCondition> conditions;

	protected void generateLootRoll(List<ItemStack> itemStacks, Random random, LootTableInfo lootTableInfo) {
		List<WeightedLootSelectorEntry> activeLootSelectorEntries = new ArrayList<>(entries.size());
		int totalWeight = 0;

		for(LootSelectorEntry lootSelectorEntry : entries) {
			int totalEntryWeight = lootSelectorEntry.getFinalWeight(random, lootTableInfo.getLuck());
			WeightedLootSelectorEntry weightedLootSelectorEntry = new WeightedLootSelectorEntry(lootSelectorEntry, totalEntryWeight);

			if(!LootItemCondition.checkConditions(lootSelectorEntry.getConditions(), random, lootTableInfo) || totalEntryWeight <= 0) {
				continue;
			}

			activeLootSelectorEntries.add(weightedLootSelectorEntry);

			totalWeight += totalEntryWeight;
		}

		if(totalWeight == 0 || activeLootSelectorEntries.isEmpty()) {
			return;
		}

		int randomChance = random.nextInt(totalWeight);

		for(WeightedLootSelectorEntry lootSelectorEntry : activeLootSelectorEntries) {
			randomChance -= lootSelectorEntry.getEntryWeight();

			if(randomChance >= 0) {
				continue;
			}

			lootSelectorEntry.getEntry().generateLoot(itemStacks, random, lootTableInfo);

			return;
		}
	}

	@Data
	private static class WeightedLootSelectorEntry {
		private final LootSelectorEntry entry;
		private final int entryWeight;
	}

	public void generateLoot(List<ItemStack> collection, Random random, LootTableInfo lootTableInfo) {
		if(!LootItemCondition.checkConditions(conditions, random, lootTableInfo)) {
			return;
		}

		int finalRolls = rolls.getValue(random) + (int) Math.floor((float) bonusRolls.getValue(random) * lootTableInfo.getLuck());

		for(int i = 0; i < finalRolls; i++) {
			generateLootRoll(collection, random, lootTableInfo);
		}
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();

		if(!rolls.isZero()) {
			map.put("rolls", rolls.serialize());
		}

		if(!bonusRolls.isZero()) {
			map.put("bonusRolls", bonusRolls.serialize());
		}

		int i = 0;

		for(LootSelectorEntry lootSelector : entries) {
			map.put("entry" + i, lootSelector);

			i++;
		}

		i = 0;

		for(LootItemCondition condition : conditions) {
			map.put("condition" + i, condition);

			i++;
		}

		return map;
	}

	public static LootSelector fromYaml(ConfigurationSection lootSelectorYaml) {
		LootValue rolls = LootValue.fromString(lootSelectorYaml.getString("rolls", "0"));
		LootValue bonusRolls = LootValue.fromString(lootSelectorYaml.getString("bonusRolls", "0"));

		List<LootSelectorEntry> entries = new ArrayList<>();
		List<LootItemCondition> conditions = new ArrayList<>();

		for(String key : lootSelectorYaml.getKeys(false)) {
			if(key.startsWith("entry")) {
				entries.add(LootSelectorEntry.fromYaml(lootSelectorYaml.getConfigurationSection(key)));
			} else if(key.startsWith("condition")) {
				conditions.add(LootItemCondition.fromYaml(lootSelectorYaml.getConfigurationSection(key)));
			}
		}

		return new LootSelector(rolls, bonusRolls, entries, conditions);
	}
}