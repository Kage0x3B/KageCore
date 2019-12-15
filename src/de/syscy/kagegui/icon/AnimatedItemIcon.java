package de.syscy.kagegui.icon;

import org.bukkit.inventory.ItemStack;

import de.syscy.kagegui.IInventoryGUI;

public class AnimatedItemIcon extends ItemIcon {
	private ItemStack[] frames;
	private int delay = 1;
	private int currentDelay = 0;
	private int index = 0;

	public AnimatedItemIcon(int delay, ItemStack... frames) {
		super(frames[0]);

		this.frames = frames;
		this.delay = 1;
		currentDelay = 0;
	}

	@Override
	public void update(IInventoryGUI<?> gui) {
		currentDelay++;

		if(currentDelay >= delay) {
			currentDelay = 0;
			index++;

			if(index >= frames.length) {
				index = 0;
			}

			itemStack = frames[index];
		}

		gui.markDirty();
	}
}