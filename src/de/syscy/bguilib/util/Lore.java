package de.syscy.bguilib.util;

import java.util.Arrays;
import java.util.List;

public class Lore {
	private String[] lore;

	public static Lore fromString(String string) {
		return new Lore(string.split(","));
	}

	public Lore(String... lore) {
		this.lore = lore;
	}

	public List<String> toArray() {
		return Arrays.asList(lore);
	}
	
	public void setLine(int line, String text) {
		lore[line] = text;
	}

	public String toString() {
		String result = "";

		for(int i = 0; i < this.lore.length; i++) {
			result = result + this.lore[i] + (i == this.lore.length - 1 ? "" : ",");
		}

		return result;
	}
}