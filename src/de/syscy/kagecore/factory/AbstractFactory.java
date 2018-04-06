package de.syscy.kagecore.factory;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.file.YamlConfiguration;

import lombok.Getter;

public abstract class AbstractFactory<T> implements IFactory<T> {
	private File folder;
	private String templateFileExtension;

	protected @Getter Map<String, IFactoryTemplate<T>> templates = new HashMap<>();

	protected void loadTemplates(File folder, final String templateFileExtension) {
		this.folder = folder;
		this.templateFileExtension = templateFileExtension;

		if(!folder.exists()) {
			folder.mkdirs();
		}

		File[] files = folder.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.isDirectory() || file.getName().endsWith("." + templateFileExtension);
			}
		});

		for(File file : files) {
			if(file.isDirectory()) {
				loadTemplates(file, templateFileExtension);
			} else {
				YamlConfiguration templateYamlFile = YamlConfiguration.loadConfiguration(file);
				String templateName = file.getName().substring(0, file.getName().indexOf('.')).toLowerCase();

				try {
					IFactoryTemplate<T> template = createTemplate();
					template.load(this, templateName, templateYamlFile);

					templates.put(templateName, template);
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	@Override
	public void reload() {
		templates.clear();

		loadTemplates(folder, templateFileExtension);
	}

	protected abstract IFactoryTemplate<T> createTemplate();
}