package de.syscy.kagecore.util.loottable;

import de.syscy.kagecore.util.loottable.condition.LootItemCondition;
import de.syscy.kagecore.util.loottable.entry.LootSelectorEntry;
import de.syscy.kagecore.util.loottable.value.LootValue;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class LootSelectorLootTable extends LootSelectorEntry {
	private String lootTableName;

	private LootSelectorLootTable(LootValue weight, LootValue quality, List<LootItemCondition> conditions, String lootTableName) {
		super(weight, quality, conditions);

		this.lootTableName = lootTableName;
	}

	@Override
	public void generateLoot(List<ItemStack> itemStacks, Random random, LootTableInfo lootTableInfo) {
		LootTable lootTable = lootTableInfo.getLootTableRegistry().getLootTable(lootTableName);
		lootTable.generateLoot(random, lootTableInfo, itemStacks);
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = super.serialize();
		map.put("lootTable", lootTableName);

		return map;
	}

	public static LootSelectorLootTable fromYaml(ConfigurationSection lootSelectorYaml, LootValue weight, LootValue quality, List<LootItemCondition> conditions) {
		String lootTableName = lootSelectorYaml.getString("lootTable", "");

		return new LootSelectorLootTable(weight, quality, conditions, lootTableName);
	}
}