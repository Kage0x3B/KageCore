package de.syscy.kagegui.util;

public enum Symbol {
	//@-
	HAPPY_FACE(0x263A),
	SAD_FACE(0x2639),
	WARNING(0x26A0),
	BLACK_SCISSORS(0x2702),
	WHITE_SCISSORS(0x2704),
	WHITE_HEAVY_CHECK_MARK(0x2705),
	ENVELOPE(0x2709),
	WRITING_HAND(0x270D),
	PENCIL(0x270E),
	CHECK_MARK(0x2713, 0x2714),
	MULTIPLICATION_X(0x2715, 0x2716),
	BALLOT_X(0x2717, 0x2718),
	GREEK_CROSS(0x2719, 0x271A),
	OPEN_CENTRE_CROSS(0x271B, 0x271C),
	CROSS(0x271D),
	WHITE_CROSS(0x271E),
	OUTLINED_CROSS(0x271F),
	ASTERISK_1(0x2722),
	ASTERISK_2(0x2723, 0x2724),
	ASTERISK_3(0x2725),
	FILLED_STAR(0x2726),
	STAR(0x2727),
	SPARKLES(0x2728),
	COMPASS_STAR(0x272F),
	CROSS_MARK(0x274C),
	HEART(0x2665, 0x2764);
	//@+

	private final int code;
	private final int heavyCode;

	private Symbol(int code) {
		this(code, -1);
	}

	private Symbol(int code, int heavyCode) {
		this.code = code;
		this.heavyCode = heavyCode;
	}

	/**
	 * Get the heavy variant of a symbol.
	 * @return The heavy variant if available
	 * @throws UnsupportedOperationException If this symbol has no heavy variant.
	 */
	public String heavy() throws UnsupportedOperationException {
		if(heavyCode == -1) {
			throw new UnsupportedOperationException("The " + name() + " symbol has no heavy variant");
		}

		return String.valueOf(heavyCode);
	}

	@Override
	public String toString() {
		return String.valueOf(code);
	}

	public static String CIRCLE_DIGIT(int digit) {
		if(digit < 1 || digit > 10) {
			throw new UnsupportedOperationException("There is no circled digit symbol for the digit " + digit);
		}

		return String.valueOf(0x2780 + (digit - 1));
	}

	public static String FILLED_CIRCLE_DIGIT(int digit) {
		if(digit < 1 || digit > 10) {
			throw new UnsupportedOperationException("There is no circled digit symbol for the digit " + digit);
		}

		return String.valueOf(0x2776 + (digit - 1));
	}
}