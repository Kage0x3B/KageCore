package de.syscy.kagecore.factory;

import java.util.Map;

public interface IFactory<T> {
	public void loadTemplates();

	public Map<String, IFactoryTemplate<T>> getTemplates();

	public void reload();
}