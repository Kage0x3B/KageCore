package de.syscy.kagecore.util.loottable.function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import de.syscy.kagecore.util.loottable.LootTableInfo;
import de.syscy.kagecore.util.loottable.condition.LootItemCondition;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public abstract class LootItemFunction implements ConfigurationSerializable {
	private static Map<String, LootItemFunctionFactory<?>> registeredFunctions = new HashMap<>();

	static {
		registerFunction(new SetAmountFunction.SetAmountFunctionFactory());
		registerFunction(new SetDamageFunction.SetDamageFunctionFactory());
	}

	private String functionType;
	private @Getter List<LootItemCondition> conditions;

	public abstract ItemStack apply(ItemStack itemStack, Random random, LootTableInfo lootTableInfo);

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		map.put("type", functionType);

		int i = 0;

		for(LootItemCondition condition : conditions) {
			map.put("condition" + i, condition);

			i++;
		}

		return map;
	}

	public static void registerFunction(LootItemFunctionFactory<?> functionFactory) {
		registeredFunctions.put(functionFactory.getType(), functionFactory);
	}

	public static LootItemFunction fromYaml(ConfigurationSection yaml) {
		String type = yaml.getString("type", "").toLowerCase().replaceAll("_", "");

		List<LootItemCondition> conditions = new ArrayList<>();

		for(String key : yaml.getKeys(false)) {
			if(key.startsWith("condition")) {
				conditions.add(LootItemCondition.fromYaml(yaml.getConfigurationSection(key)));
			}
		}

		return registeredFunctions.get(type).create(yaml, conditions);
	}

	public static interface LootItemFunctionFactory<T extends LootItemFunction> {
		public String getType();

		public T create(ConfigurationSection yaml, List<LootItemCondition> conditions);
	}
}