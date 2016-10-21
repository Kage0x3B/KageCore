package de.syscy.kagecore.util.loottable.condition;

import java.util.Map;
import java.util.Random;

import org.bukkit.configuration.ConfigurationSection;

import de.syscy.kagecore.util.loottable.LootTableInfo;
import de.syscy.kagecore.util.loottable.value.LootValue;

public class RandomChanceCondition extends LootItemCondition {
	public static final String TYPE = "randomchance";

	private LootValue chancePercentValue;

	private RandomChanceCondition(LootValue damagePercentValue) {
		super(TYPE);

		chancePercentValue = damagePercentValue;
	}

	@Override
	public boolean checkCondition(Random random, LootTableInfo lootTableInfo) {
		return random.nextFloat() < (float) chancePercentValue.getValue(random) / 100.0f;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = super.serialize();
		map.put("chancePercent", chancePercentValue.serialize());

		return map;
	}

	public static class RandomChanceConditionFactory implements LootItemConditionFactory<RandomChanceCondition> {
		@Override
		public String getType() {
			return TYPE;
		}

		@Override
		public RandomChanceCondition create(ConfigurationSection yaml) {
			return new RandomChanceCondition(LootValue.fromString(yaml.getString("chancePercent", "100")));
		}
	}
}