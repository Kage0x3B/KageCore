package de.syscy.bguilib;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.syscy.bguilib.callbacks.YesNoCallback;
import de.syscy.bguilib.components.BGButton;
import de.syscy.bguilib.components.BGLabel;
import de.syscy.bguilib.components.icon.ItemIcon;
import de.syscy.bguilib.components.listener.ButtonClickListener;

public class YesNoGUI extends BGGUI {
	private BGGUI guiBefore = null;
	private YesNoCallback callback;
	private boolean result = false;

	public YesNoGUI(YesNoCallback callback, String message, String title) {
		this(null, callback, message, title);
	}

	public YesNoGUI(BGGUI guiBefore, YesNoCallback callback, String message, String title) {
		this.guiBefore = guiBefore;
		this.callback = callback;
		
		if(guiBefore != null) {
			guiBefore.hide();
		}
		
		setTitle(title);
		setHeight(5);

		BGLabel messageLabel = new BGLabel(0, 0, 9, 2, message);
		add(messageLabel);

		BGButton yesButton = new BGButton(0, 2, "Yes");
		yesButton.setButtonIcon(new ItemIcon(new ItemStack(Material.WOOL, 1, (short) 5)));
		yesButton.setSize(5, 3);
		yesButton.addClickListener(new ButtonClickListener() {
			public void onClick(Player player) {
				YesNoGUI.this.result = true;
				YesNoGUI.this.onResult();
			}
		});
		add(yesButton);

		BGButton noButton = new BGButton(5, 2, "No");
		noButton.setButtonIcon(new ItemIcon(new ItemStack(Material.WOOL, 1, (short) 14)));
		noButton.setSize(4, 3);
		noButton.addClickListener(new ButtonClickListener() {
			public void onClick(Player player) {
				YesNoGUI.this.result = false;
				YesNoGUI.this.onResult();
			}
		});
		add(noButton);
	}

	private void onResult() {
		this.callback.onResult(this.result);

		close();
		
		if(this.guiBefore != null) {
			this.guiBefore.unhide();
		}
	}
}