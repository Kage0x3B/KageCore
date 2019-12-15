package de.syscy.kagecore.util.loottable;

import lombok.Getter;
import org.bukkit.World;

import java.util.LinkedHashSet;
import java.util.Set;

public class LootTableInfo {
	private final @Getter LootTableRegistry lootTableRegistry;
	private final @Getter World world;
	private final @Getter float luck;

	private final Set<LootTable> lootTables = new LinkedHashSet<>();

	private LootTableInfo(LootTableRegistry lootTableRegistry, World world, float luck) {
		this.lootTableRegistry = lootTableRegistry;
		this.world = world;
		this.luck = luck;
	}

	public boolean addLootTable(LootTable lootTable) {
		return lootTables.add(lootTable);
	}

	public void removeLootTable(LootTable lootTable) {
		lootTables.remove(lootTable);
	}

	public static Builder builder(LootTableRegistry lootTableRegistry, World world) {
		return new Builder(lootTableRegistry, world);
	}

	public static class Builder {
		private final LootTableRegistry lootTableRegistry;
		private final World world;
		private float luck = 0.0f;

		private Builder(LootTableRegistry lootTableRegistry, World world) {
			this.lootTableRegistry = lootTableRegistry;
			this.world = world;
		}

		public Builder luck(float luck) {
			this.luck = luck;

			return this;
		}

		public LootTableInfo build() {
			return new LootTableInfo(lootTableRegistry, world, luck);
		}
	}
}