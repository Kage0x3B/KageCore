package de.syscy.kagecore.util.loottable.function;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import de.syscy.kagecore.util.loottable.LootTableInfo;
import de.syscy.kagecore.util.loottable.condition.LootItemCondition;
import de.syscy.kagecore.util.loottable.value.LootValue;

public class SetAmountFunction extends LootItemFunction {
	public static final String TYPE = "setamount";

	private LootValue amountValue;

	private SetAmountFunction(List<LootItemCondition> conditions, LootValue amountValue) {
		super(TYPE, conditions);

		this.amountValue = amountValue;
	}

	@Override
	public ItemStack apply(ItemStack itemStack, Random random, LootTableInfo lootTableInfo) {
		itemStack.setAmount(amountValue.getValue(random));

		return itemStack;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = super.serialize();
		map.put("amount", amountValue.serialize());

		return map;
	}

	public static class SetAmountFunctionFactory implements LootItemFunctionFactory<SetAmountFunction> {
		@Override
		public String getType() {
			return TYPE;
		}

		@Override
		public SetAmountFunction create(ConfigurationSection yaml, List<LootItemCondition> conditions) {
			return new SetAmountFunction(conditions, LootValue.fromString(yaml.getString("amount", "1")));
		}
	}
}