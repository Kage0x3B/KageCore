package de.syscy.kagegui.yaml;

import java.util.Map;

import de.syscy.kagecore.versioncompat.reflect.Reflect;

public interface IYamlGUI<T> {
	public Map<String, T> getComponents();

	public Reflect getControllerClass();
}