package de.syscy.kagegui.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Joiner;

import lombok.Setter;

public class Lore {
	private @Setter String temporaryFirstLine = null;

	private LinkedList<String> loreList;

	public static Lore fromString(String string) {
		return new Lore(string.split(","));
	}

	public Lore(String... lore) {
		loreList = new LinkedList<>(Arrays.asList(lore));
	}

	public Lore(List<String> lore) {
		loreList = new LinkedList<>(lore);
	}

	public List<String> getAsList() {
		LinkedList<String> loreList = new LinkedList<>(this.loreList);

		if(temporaryFirstLine != null && !temporaryFirstLine.isEmpty()) {
			loreList.addFirst(temporaryFirstLine);
		}

		return loreList;
	}

	@Override
	public String toString() {
		return Joiner.on(',').join(loreList);
	}
}