package de.syscy.kagecore.util.loottable.value;

import java.util.Random;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RangeValue extends LootValue {
	private final int min;
	private final int max;

	@Override
	public int getValue(Random random) {
		return random.nextInt(max - min) + min;
	}

	@Override
	public boolean isZero() {
		return max == 0;
	}

	@Override
	public String serialize() {
		return min + "-" + max;
	}
}