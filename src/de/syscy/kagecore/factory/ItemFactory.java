package de.syscy.kagecore.factory;

import java.util.Map;

public interface ItemFactory<T> {
	public void loadTemplates();

	public Map<String, FactoryTemplate<T>> getTemplates();

	public void reload();
}