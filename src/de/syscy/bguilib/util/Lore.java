package de.syscy.bguilib.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Lore {
	private String temporaryFirstLine = null;

	private LinkedList<String> loreList;

	public static Lore fromString(String string) {
		return new Lore(string.split(","));
	}

	public Lore(String... lore) {
		this.loreList = new LinkedList<>(Arrays.asList(lore));
	}

	public void setTemporaryFirstLine(String firstLine) {
		this.temporaryFirstLine = firstLine;
	}

	public List<String> getAsList() {
		@SuppressWarnings("unchecked")
		LinkedList<String> loreList = (LinkedList<String>) this.loreList.clone();
		
		if(temporaryFirstLine != null && !temporaryFirstLine.isEmpty()) {
			loreList.addFirst(temporaryFirstLine);
		}

		return loreList;
	}

	public String toString() {
		String result = "";

		for(int i = 0; i < this.loreList.size(); i++) {
			result = result + this.loreList.get(i) + (i == this.loreList.size() - 1 ? "" : ",");
		}

		return result;
	}
}