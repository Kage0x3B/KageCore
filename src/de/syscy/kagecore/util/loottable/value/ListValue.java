package de.syscy.kagecore.util.loottable.value;

import java.util.List;
import java.util.Random;

import com.google.common.base.Joiner;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ListValue extends LootValue {
	private final List<Integer> values;

	@Override
	public int getValue(Random random) {
		return values.get(random.nextInt(values.size()));
	}

	@Override
	public boolean isZero() {
		if(values.isEmpty()) {
			return true;
		}

		for(int value : values) {
			if(value > 0) {
				return false;
			}
		}

		return true;
	}

	@Override
	public String serialize() {
		return Joiner.on(',').join(values);
	}
}