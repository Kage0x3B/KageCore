package de.syscy.kagecore.util.loottable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;

import de.syscy.kagecore.KageCore;
import de.syscy.kagecore.util.MathUtil;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LootTable implements ConfigurationSerializable {
	public static final LootTable EMPTY = new LootTable(new ArrayList<LootSelector>(0));

	private List<LootSelector> entries;

	public List<ItemStack> generateLoot(Random random, LootTableInfo lootTableInfo) {
		ArrayList<ItemStack> lootItemList = Lists.newArrayList();

		if(lootTableInfo.addLootTable(this)) {
			for(LootSelector lootSelector : entries) {
				lootSelector.generateLoot(lootItemList, random, lootTableInfo);
			}

			lootTableInfo.removeLootTable(this);
		} else {
			KageCore.debugMessage("Detected infinite loop in loot tables");
		}

		return lootItemList;
	}

	public void fillContainerWithLoot(Inventory inventory, Random random, LootTableInfo lootTableInfo) {
		List<ItemStack> lootItems = generateLoot(random, lootTableInfo);
		List<Integer> freeSlots = getFreeSlots(inventory, random);
		shuffleItems(lootItems, freeSlots.size(), random);

		for(ItemStack itemStack : lootItems) {
			if(freeSlots.isEmpty()) {
				KageCore.debugMessage("Tried to over-fill a container");

				return;
			}

			if(itemStack == null) {
				inventory.setItem(freeSlots.remove(freeSlots.size() - 1), null);

				continue;
			}

			inventory.setItem(freeSlots.remove(freeSlots.size() - 1), itemStack);
		}
	}

	private void shuffleItems(List<ItemStack> itemStackList, int freeSlotAmount, Random random) {
		ArrayList<ItemStack> bigItemStacks = Lists.newArrayList();
		Iterator<ItemStack> itemStackListIterator = itemStackList.iterator();

		while(itemStackListIterator.hasNext()) {
			ItemStack itemStack = itemStackListIterator.next();

			if(itemStack.getAmount() <= 0) { //Remove invalid items with an amount less than zero
				itemStackListIterator.remove();

				continue;
			}

			if(itemStack.getAmount() == 1) { //If the amount is 1, ignore
				continue;
			}

			bigItemStacks.add(itemStack); //Else, put it into another list
			itemStackListIterator.remove();
		}

		while((freeSlotAmount -= itemStackList.size()) > 0 && bigItemStacks.size() > 0) {
			ItemStack itemStack = bigItemStacks.remove(MathUtil.randomRangeInt(0, bigItemStacks.size() - 1)); //Remove a random itemstack from this list
			int randomAmount = MathUtil.randomRangeInt(1, itemStack.getAmount() / 2); //Generate random value between 1 and amount / 2; ex: amount=6 -> random value between 1-3 (both inclusive) -> ex: 2
			itemStack.setAmount(itemStack.getAmount() - randomAmount); //set the amount to amount - random value, ex: amount=6-2=4

			ItemStack itemStack2 = itemStack.clone();
			itemStack2.setAmount(randomAmount); //set amount to random value; ex: amount=2

			if(itemStack.getAmount() > 1 && random.nextBoolean()) {
				bigItemStacks.add(itemStack);
			} else {
				itemStackList.add(itemStack);
			}

			if(itemStack2.getAmount() > 1 && random.nextBoolean()) {
				bigItemStacks.add(itemStack2);

				continue;
			}

			itemStackList.add(itemStack2);
		}

		itemStackList.addAll(bigItemStacks);

		Collections.shuffle(itemStackList, random);
	}

	private List<Integer> getFreeSlots(Inventory inventory, Random random) {
		ArrayList<Integer> freeSlots = Lists.newArrayList();

		for(int i = 0; i < inventory.getSize(); i++) {
			if(inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR) {
				freeSlots.add(i);
			}
		}

		Collections.shuffle(freeSlots, random);

		return freeSlots;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();

		int i = 0;

		for(LootSelector lootSelector : entries) {
			map.put("entry" + i, lootSelector);

			i++;
		}

		return map;
	}

	public static LootTable fromYaml(ConfigurationSection lootTableYaml) {
		List<LootSelector> entries = new ArrayList<>();

		for(String key : lootTableYaml.getKeys(false)) {
			entries.add(LootSelector.fromYaml(lootTableYaml.getConfigurationSection(key)));
		}

		return new LootTable(entries);
	}
}