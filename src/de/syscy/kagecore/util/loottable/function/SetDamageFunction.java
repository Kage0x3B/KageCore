package de.syscy.kagecore.util.loottable.function;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import de.syscy.kagecore.util.loottable.LootTableInfo;
import de.syscy.kagecore.util.loottable.condition.LootItemCondition;
import de.syscy.kagecore.util.loottable.value.LootValue;

public class SetDamageFunction extends LootItemFunction {
	public static final String TYPE = "setdamage";

	private LootValue damagePercentValue;

	private SetDamageFunction(List<LootItemCondition> conditions, LootValue damagePercentValue) {
		super(TYPE, conditions);

		this.damagePercentValue = damagePercentValue;
	}

	@Override
	public ItemStack apply(ItemStack itemStack, Random random, LootTableInfo lootTableInfo) {
		short maxDurability = itemStack.getType().getMaxDurability();
		int damagePercent = damagePercentValue.getValue(random);

		itemStack.setDurability((short) ((float) maxDurability * ((float) damagePercent / 100.0f)));

		return itemStack;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = super.serialize();
		map.put("damagePercentValue", damagePercentValue.serialize());

		return map;
	}

	public static class SetDamageFunctionFactory implements LootItemFunctionFactory<SetDamageFunction> {
		@Override
		public String getType() {
			return TYPE;
		}

		@Override
		public SetDamageFunction create(ConfigurationSection yaml, List<LootItemCondition> conditions) {
			return new SetDamageFunction(conditions, LootValue.fromString(yaml.getString("damagePercent", "100")));
		}
	}
}