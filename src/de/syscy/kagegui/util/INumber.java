package de.syscy.kagegui.util;

public interface INumber {
	/**
     * Returns the value of the specified number as an {@code int},
     * which may involve rounding or truncation.
     *
     * @return  the numeric value represented by this object after conversion
     *          to type {@code int}.
     */
    public int intValue();

	/**
     * Returns the value of the specified number as a {@code long},
     * which may involve rounding or truncation.
     *
     * @return  the numeric value represented by this object after conversion
     *          to type {@code long}.
     */
    public long longValue();

	/**
     * Returns the value of the specified number as a {@code float},
     * which may involve rounding.
     *
     * @return  the numeric value represented by this object after conversion
     *          to type {@code float}.
     */
    public float floatValue();

	/**
     * Returns the value of the specified number as a {@code double},
     * which may involve rounding.
     *
     * @return  the numeric value represented by this object after conversion
     *          to type {@code double}.
     */
    public double doubleValue();

	/**
	 * Returns the value of the specified number as a {@code byte},
	 * which may involve rounding or truncation.
	 *
	 * <p>This implementation returns the result of {@link #intValue} cast
	 * to a {@code byte}.
	 *
	 * @return  the numeric value represented by this object after conversion
	 *          to type {@code byte}.
	 * @since   JDK1.1
	 */
	default public byte byteValue() {
		return (byte) intValue();
	}

	/**
	 * Returns the value of the specified number as a {@code short},
	 * which may involve rounding or truncation.
	 *
	 * <p>This implementation returns the result of {@link #intValue} cast
	 * to a {@code short}.
	 *
	 * @return  the numeric value represented by this object after conversion
	 *          to type {@code short}.
	 * @since   JDK1.1
	 */
	default public short shortValue() {
		return (short) intValue();
	}
}