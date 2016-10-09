package de.syscy.bguilib.util;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.syscy.bguilib.BGGUI;
import de.syscy.bguilib.callbacks.YesNoCallback;
import de.syscy.bguilib.components.BGButton;
import de.syscy.bguilib.components.icon.ItemIcon;
import de.syscy.bguilib.components.listener.ButtonClickListener;

public class YesNoGUI extends BGGUI {
	private BGGUI guiBefore = null;
	private YesNoCallback callback;

	public YesNoGUI(YesNoCallback callback, String message) {
		this(null, callback, message);
	}
	
	public YesNoGUI(BGGUI guiBefore, YesNoCallback callback, String message) {
		this.guiBefore = guiBefore;
		this.callback = callback;
		
		if(guiBefore != null) {
			guiBefore.hide();
		}
		
		setTitle(message);
		setSize(HOPPER_INVENTORY_SIZE);

		BGButton yesButton = new BGButton(0, 0, "Yes");
		yesButton.setButtonIcon(new ItemIcon(new ItemStack(Material.WOOL, 1, (short) 5)));
		yesButton.setSize(2, 1);
		yesButton.addClickListener(new ButtonClickListener() {
			public void onClick(Player player) {
				YesNoGUI.this.handleResult(true);
			}
		});
		add(yesButton);

		BGButton noButton = new BGButton(3, 0, "No");
		noButton.setButtonIcon(new ItemIcon(new ItemStack(Material.WOOL, 1, (short) 14)));
		noButton.setSize(2, 1);
		noButton.addClickListener(new ButtonClickListener() {
			public void onClick(Player player) {
				YesNoGUI.this.handleResult(false);
			}
		});
		add(noButton);
	}

	private void handleResult(boolean result) {
		this.callback.onResult(result);

		close();
		
		if(this.guiBefore != null) {
			this.guiBefore.unhide();
		}
	}
}