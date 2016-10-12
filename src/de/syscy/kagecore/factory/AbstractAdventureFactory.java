package de.syscy.kagecore.factory;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.file.YamlConfiguration;

public abstract class AbstractAdventureFactory<T> implements AdventureFactory<T> {
	private File folder;
	private String templateFileExtension;
	
	protected Map<String, FactoryTemplate<T>> templates = new HashMap<>();

	protected void loadTemplates(File folder, final String templateFileExtension) {
		this.folder = folder;
		this.templateFileExtension = templateFileExtension;
		
		if(!folder.exists()) {
			folder.mkdirs();
		}
		
		File[] templateFiles = folder.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.getName().endsWith("." + templateFileExtension);
			}
		});

		for(File templateFile : templateFiles) {
			YamlConfiguration templateYamlFile = YamlConfiguration.loadConfiguration(templateFile);
			String templateName = templateFile.getName().substring(0, templateFile.getName().indexOf('.')).toLowerCase();

			try {
				FactoryTemplate<T> template = createTemplate();
				template.load(this, templateYamlFile);

				templates.put(templateName, template);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	@Override
	public void reload() {
		templates.clear();
		
		loadTemplates(folder, templateFileExtension);
	}

	protected abstract FactoryTemplate<T> createTemplate();
}