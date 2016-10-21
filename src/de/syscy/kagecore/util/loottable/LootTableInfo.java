package de.syscy.kagecore.util.loottable;

import java.util.Set;

import org.bukkit.World;

import com.google.common.collect.Sets;

import lombok.Getter;

public class LootTableInfo {
	private final @Getter LootTableRegistry lootTableRegistry;
	private final @Getter World world;
	private final @Getter float luck;

	private final Set<LootTable> lootTables = Sets.newLinkedHashSet();

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

	public static class Builder {
		private final LootTableRegistry lootTableRegistry;
		private final World world;
		private float luck = 0.0f;

		public Builder(LootTableRegistry lootTableRegistry, World world) {
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