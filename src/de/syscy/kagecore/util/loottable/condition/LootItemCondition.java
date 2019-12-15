package de.syscy.kagecore.util.loottable.condition;

import de.syscy.kagecore.util.loottable.LootTableInfo;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@ToString
@AllArgsConstructor
public abstract class LootItemCondition implements ConfigurationSerializable {
	private static Map<String, LootItemConditionFactory<?>> registeredConditions = new HashMap<>();

	static {
		registerCondition(new RandomChanceCondition.RandomChanceConditionFactory());
	}

	private final String conditionType;

	public abstract boolean checkCondition(Random random, LootTableInfo lootTableInfo);

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		map.put("type", conditionType);

		return map;
	}

	public static void registerCondition(LootItemConditionFactory<?> conditionFactory) {
		registeredConditions.put(conditionFactory.getType(), conditionFactory);
	}

	public static boolean checkConditions(List<LootItemCondition> lootItemConditions, Random random, LootTableInfo lootTableInfo) {
		if(lootItemConditions == null || lootItemConditions.isEmpty()) {
			return true;
		}

		for(LootItemCondition lootItemCondition : lootItemConditions) {
			if(!lootItemCondition.checkCondition(random, lootTableInfo)) {
				return false;
			}
		}

		return true;
	}

	public static LootItemCondition fromYaml(ConfigurationSection yaml) {
		String type = yaml.getString("type", "").toLowerCase().replaceAll("_", "");

		return registeredConditions.get(type).create(yaml);
	}

	public interface LootItemConditionFactory<T extends LootItemCondition> {
		String getType();

		T create(ConfigurationSection yaml);
	}
}