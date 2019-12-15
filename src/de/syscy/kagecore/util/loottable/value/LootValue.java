package de.syscy.kagecore.util.loottable.value;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.syscy.kagecore.util.Util;

import com.google.common.base.Splitter;

public abstract class LootValue {
	public abstract int getValue(Random random);

	public abstract boolean isZero();

	public String serialize() {
		return "0";
	}

	@Override
	public String toString() {
		return serialize();
	}

	public static LootValue fromString(String string) {
		try {
			if(Util.isNumber(string)) {
				return new FixedValue(Integer.parseInt(string));
			} else if(string.contains("-")) {
				String[] split = string.split("-", 2);

				if(split.length < 2 || !Util.isNumber(split[0]) || !Util.isNumber(split[1])) {
					throw new IllegalArgumentException(string + " is not a valid range!");
				}

				int min = Integer.parseInt(split[0]);
				int max = Integer.parseInt(split[1]);

				if(min == max) {
					return new FixedValue(min);
				} else if(min > max) {
					return new RangeValue(max, min);
				} else {
					return new RangeValue(min, max);
				}
			} else if(string.contains(",")) {
				;
				String[] split = string.split(",");

				if(split.length < 2) {
					throw new IllegalArgumentException(string + " is not a valid list of numbers!");
				}

				List<Integer> numberList = new ArrayList<>();

				for(String numberString : Splitter.on(',').omitEmptyStrings().trimResults().split(string)) {
					if(!Util.isNumber(numberString)) {
						throw new IllegalArgumentException(string + " is not a valid list of numbers!");
					}

					numberList.add(Integer.parseInt(numberString));
				}

				return new ListValue(numberList);
			}
		} catch(Exception ex) {

		}

		throw new IllegalArgumentException(string + " is not a valid number/range/list of numbers!");
	}
}