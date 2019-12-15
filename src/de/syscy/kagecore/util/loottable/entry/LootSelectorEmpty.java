package de.syscy.kagecore.util.loottable.entry;

import de.syscy.kagecore.util.loottable.LootTableInfo;
import de.syscy.kagecore.util.loottable.condition.LootItemCondition;
import de.syscy.kagecore.util.loottable.value.LootValue;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class LootSelectorEmpty extends LootSelectorEntry {
	private LootSelectorEmpty(LootValue weight, LootValue quality, List<LootItemCondition> conditions) {
		super(weight, quality, conditions);
	}

	@Override
	public void generateLoot(List<ItemStack> itemStacks, Random random, LootTableInfo lootTableInfo) {

	}

	public static LootSelectorEmpty fromYaml(ConfigurationSection lootSelectorYaml, LootValue weight, LootValue quality, List<LootItemCondition> conditions) {
		return new LootSelectorEmpty(weight, quality, conditions);
	}
}