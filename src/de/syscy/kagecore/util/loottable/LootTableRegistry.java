package de.syscy.kagecore.util.loottable;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import de.syscy.kagecore.KageCore;
import de.syscy.kagecore.factory.IFactoryProviderPlugin;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class LootTableRegistry {
	private final @Getter IFactoryProviderPlugin plugin;
	private final File lootTableDirectory;
	private LoadingCache<String, LootTable> cache;

	public LootTableRegistry(IFactoryProviderPlugin plugin) {
		this.plugin = plugin;

		lootTableDirectory = new File(plugin.getDataFolder(), "lootTables");
		lootTableDirectory.mkdirs();

		cache = CacheBuilder.newBuilder().build(new LootTableLoader());
	}

	public LootTable getLootTable(String lootTableName) {
		try {
			return cache.get(lootTableName);
		} catch(ExecutionException ex) {
			ex.printStackTrace();
		}

		return LootTable.EMPTY;
	}

	public void reload() {
		cache.cleanUp();
	}

	public boolean doesLootTableExist(String lootTableName) {
		File lootTableFile = new File(lootTableDirectory, lootTableName + ".loot");

		return lootTableFile.exists();
	}

	private class LootTableLoader extends CacheLoader<String, LootTable> {
		@Override
		public LootTable load(String lootTableName) throws Exception {
			if(lootTableName.contains(".")) {
				KageCore.debugMessage("Invalid loot table name \"" + lootTableName + "\" (can't contain periods)");

				return LootTable.EMPTY;
			}

			File lootTableFile = new File(lootTableDirectory, lootTableName + ".loot");
			LootTable lootTable = null;

			if(lootTableFile.exists()) {
				if(lootTableFile.isFile()) {
					try {
						lootTable = LootTable.fromYaml(YamlConfiguration.loadConfiguration(lootTableFile));
					} catch(Exception ex) {
						ex.printStackTrace();
					}
				} else {
					KageCore.debugMessage("Expected to find loot table " + lootTableName + " at " + lootTableFile + " but it was a folder");

					return LootTable.EMPTY;
				}
			}

			return lootTable != null ? lootTable : LootTable.EMPTY;
		}
	}
}