package de.syscy.bguilib.components.icon;

import org.bukkit.inventory.ItemStack;

public class AnimatedItemIcon extends ItemIcon {
	private ItemStack[] frames;
	private int delay = 1;
	private int currentDelay = 0;
	private int index = 0;

	public AnimatedItemIcon(int delay, ItemStack... frames) {
		super(frames[0]);
		
		this.frames = frames;
		this.delay = 1;
		this.currentDelay = 0;
	}

	public void update() {
		this.currentDelay++;
		
		if(this.currentDelay >= this.delay) {
			this.currentDelay = 0;
			this.index++;

			if(this.index >= this.frames.length) {
				this.index = 0;
			}

			this.itemStack = this.frames[this.index];
		}
	}
}