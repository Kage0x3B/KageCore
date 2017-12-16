package de.syscy.kagecore.util;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public interface IChance extends Comparable<IChance> {
	/**
	 * @return the chance to get this item, usually a value between 0 and 1
	 */
	public double getChance();

	@Override
	default int compareTo(IChance other) {
		return -Double.compare(getChance(), other.getChance());
	}

	/**
	 * Get an item from the list with a specific chance. The chance is automatically generated from the {@link ThreadLocalRandom} generator
	 * @param items the list with items which are available
	 * @return the item selected
	 */
	public static <T extends IChance> T getItemWithChance(List<T> items) {
		return getItemWithChance(items, ThreadLocalRandom.current().nextDouble());
	}

	/**
	 * Get an item from the list with a specific chance.
	 * @param items the list with items which are available
	 * @param chance the chance (a double between 0 and 1 obtained from a random generator
	 * @return the item selected
	 */
	public static <T extends IChance> T getItemWithChance(List<T> items, double chance) {
		double currentChance = 0.0;

		Collections.sort(items);

		for(T item : items) {
			if(item == null) {
				continue;
			}

			currentChance += item.getChance();

			if(chance <= currentChance) {
				return item;
			}
		}

		return null;
	}
}