package de.syscy.kagecore.util.loottable.value;

import java.util.Random;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FixedValue extends LootValue {
	private final int value;

	@Override
	public int getValue(Random random) {
		return value;
	}

	@Override
	public boolean isZero() {
		return value == 0;
	}

	@Override
	public String serialize() {
		return Integer.toString(value);
	}
}