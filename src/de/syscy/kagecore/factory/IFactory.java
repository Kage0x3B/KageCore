package de.syscy.kagecore.factory;

import java.util.Map;

public interface IFactory<T> {
	void loadTemplates();

	Map<String, IFactoryTemplate<T>> getTemplates();

	void reload();
}