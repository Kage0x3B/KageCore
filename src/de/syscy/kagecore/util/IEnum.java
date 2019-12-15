package de.syscy.kagecore.util;

public interface IEnum {
	public String name();

	public int ordinal();

	default public String getPrettyName() {
		return Util.improveStringLook(name());
	}
}