package de.syscy.kagecore.factory.itemstack;

import java.util.Arrays;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ItemType {
	TREASURE_ITEM("%treasureItem.name:"), ADVENTURE_ITEM("%adventureItem.name:");

	private final @Getter String identifier;
	
	public static ItemType byName(String name) {
		return Arrays.asList("treasureitem", "treasure_item", "treasure").contains(name.toLowerCase()) ? ItemType.TREASURE_ITEM : ADVENTURE_ITEM; 
	}

	public static ItemType getItemType(ItemStack itemStack) {
		ItemMeta itemMeta = itemStack.getItemMeta();

		if(itemMeta.hasLore() && itemMeta.getLore().size() > 0 && itemMeta.getLore().get(0).equals(TREASURE_ITEM.getIdentifier())) {
			return ItemType.TREASURE_ITEM;
		}

		return ADVENTURE_ITEM;
	}
}