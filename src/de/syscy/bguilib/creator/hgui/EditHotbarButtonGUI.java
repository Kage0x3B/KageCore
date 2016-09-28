package de.syscy.bguilib.creator.hgui;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.syscy.bguilib.BGGUI;
import de.syscy.bguilib.components.BGButton;
import de.syscy.bguilib.components.BGCheckButton;
import de.syscy.bguilib.components.BGTextInput;
import de.syscy.bguilib.components.icon.ItemIcon;
import de.syscy.bguilib.components.listener.ButtonClickListener;
import de.syscy.bguilib.components.listener.CheckButtonToggleListener;
import de.syscy.bguilib.components.listener.TextInputListener;
import de.syscy.bguilib.creator.hotbarguidata.BGHotbarButtonData;
import de.syscy.bguilib.creator.hotbarguidata.HotbarGUIData;
import de.syscy.bguilib.util.Util;
import lombok.Getter;

public class EditHotbarButtonGUI extends BGGUI {
	private @Getter EditHotbarGUI guiBefore;
	private @Getter BGHotbarButtonData buttonData;
	private @Getter HotbarGUIData guiData;

	public EditHotbarButtonGUI(final EditHotbarGUI guiBefore, final BGHotbarButtonData buttonData, final HotbarGUIData guiData) {
		this.guiBefore = guiBefore;
		this.buttonData = buttonData;
		this.guiData = guiData;

		setHeight(6);
		setTitle("Edit Hotbar Button GUI");

		BGButton backButton = new BGButton(8, 0, "Back");
		backButton.setButtonIcon(new ItemIcon(new ItemStack(Material.SLIME_BALL)));
		backButton.addClickListener(new ButtonClickListener() {
			public void onClick(Player player) {
				EditHotbarButtonGUI.this.close();
				guiBefore.unhide();
			}
		});
		add(backButton);

		BGTextInput buttonTitleInput = new BGTextInput(0, 0, "Button title: ");
		buttonTitleInput.setText(buttonData.getTitle());
		buttonTitleInput.addTextInputListener(new TextInputListener() {
			public void onTextInput(Player player, String text) {
				buttonData.setTitle(text);
			}
		});
		add(buttonTitleInput);

		final BGTextInput buttonIconInput = new BGTextInput(1, 0, "Icon: ");
		buttonIconInput.setText(Util.itemStackToString(buttonData.getButtonIcon()));
		buttonIconInput.setButtonIcon(new ItemIcon(buttonData.getButtonIcon().clone()));
		buttonIconInput.addTextInputListener(new TextInputListener() {
			public void onTextInput(Player player, String text) {
				text = text.toUpperCase();
				try {
					buttonData.setButtonIcon(new ItemStack(Material.getMaterial(text)));
				} catch(Exception ex) {
					player.sendMessage(ChatColor.DARK_RED + "Error: Invalid material: " + text);
					buttonData.setButtonIcon(new ItemStack(Material.STONE));
				}
				buttonIconInput.setButtonIcon(new ItemIcon(buttonData.getButtonIcon().clone()));
			}
		});
		add(buttonIconInput);

		BGTextInput buttonOnClickCommandInput = new BGTextInput(0, 1, "On click command (Without \"/\", %s for player name): ");
		buttonOnClickCommandInput.setText(buttonData.getOnClickCommand());
		buttonOnClickCommandInput.addTextInputListener(new TextInputListener() {
			public void onTextInput(Player player, String text) {
				buttonData.setOnClickCommand(text);
			}
		});
		add(buttonOnClickCommandInput);

		BGCheckButton oCCExecutedByServerCheckButton = new BGCheckButton(1, 1, "On click command executed by server");
		oCCExecutedByServerCheckButton.setEnabled(buttonData.isOCCExecutedByServer());
		oCCExecutedByServerCheckButton.addToggleListener(new CheckButtonToggleListener() {
			public void onToggle(Player player, boolean enabled) {
				buttonData.setOCCExecutedByServer(enabled);
			}
		});
		add(oCCExecutedByServerCheckButton);

		BGButton removeButton = new BGButton(8, 5, "Remove button");
		removeButton.setButtonIcon(new ItemIcon(new ItemStack(Material.BARRIER)));
		removeButton.addClickListener(new ButtonClickListener() {
			public void onClick(Player player) {
				guiData.getComponents()[buttonData.getX()] = null;
				EditHotbarButtonGUI.this.close();
				guiBefore.unhide();
			}
		});
		add(removeButton);
	}
}