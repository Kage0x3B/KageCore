package de.syscy.kagecore;

import org.bukkit.configuration.file.FileConfiguration;

import de.syscy.kagecore.util.config.KConfiguration;
import lombok.Getter;

public class KageCoreConfig extends KConfiguration {
	private @Getter boolean debug;
	private @Getter boolean enablePacketTranslating;

	public KageCoreConfig(FileConfiguration config) {
		super(config);
	}
}