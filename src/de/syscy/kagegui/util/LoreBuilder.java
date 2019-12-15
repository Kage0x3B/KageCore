package de.syscy.kagegui.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class LoreBuilder {
	public static final int FIRST = Integer.MIN_VALUE;
	public static final int KCOMPONENT_LORE = 500;
	public static final int CLICK_ACTIONS = 1000;
	public static final int DEFAULT_DESCRIPTION = 5000;
	public static final int LAST = Integer.MAX_VALUE;

	private Map<Integer, List<String>> loreParts = new LinkedHashMap<>();

	private LinkedList<String> loreList;
	private boolean dirty = true;

	public void set(int priority, String... lorePart) {
		set(priority, Arrays.asList(lorePart));
	}

	public void set(int priority, LoreBuilder lorePart) {
		set(priority, lorePart.getLore());
	}

	public void set(int priority, List<String> lorePart) {
		if(!lorePart.isEmpty()) {
			loreParts.put(priority, lorePart);
		}

		dirty = true;
	}

	public LinkedList<String> getLore() {
		if(dirty) {
			loreList = build();

			dirty = false;
		}

		return loreList;
	}

	public boolean isEmpty() {
		return loreParts.isEmpty();
	}

	public LinkedList<String> build() {
		LinkedList<String> buildLoreList = new LinkedList<>();

		List<Entry<Integer, List<String>>> entries = new LinkedList<>(loreParts.entrySet());

		Collections.sort(entries, new Comparator<Entry<Integer, List<String>>>() {
			@Override
			public int compare(Entry<Integer, List<String>> o1, Entry<Integer, List<String>> o2) {
				return o1.getKey().compareTo(o2.getKey());
			}
		});

		for(Entry<Integer, List<String>> entry : entries) {
			buildLoreList.addAll(entry.getValue());
		}

		return new LinkedList<>(buildLoreList);
	}
}