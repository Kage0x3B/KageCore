package de.syscy.kagecore;

import org.bukkit.configuration.file.FileConfiguration;

import de.syscy.kagecore.util.config.KConfiguration;
import lombok.Getter;

public class KageCoreConfig extends KConfiguration {
	private @Getter String packetTranslatorSign;
	
	public KageCoreConfig(FileConfiguration config) {
		super(config);
	}
}