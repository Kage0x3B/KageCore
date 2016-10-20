package de.syscy.kagecore.util.specialblock;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public interface ISpecialBlockMarkListener {
	public boolean onMarkSpecialBlock(Location location, Block block);

	public static class ChestMarkListener implements ISpecialBlockMarkListener {
		@Override
		public boolean onMarkSpecialBlock(Location location, Block block) {
			return block != null && (block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST);
		}
	}
}