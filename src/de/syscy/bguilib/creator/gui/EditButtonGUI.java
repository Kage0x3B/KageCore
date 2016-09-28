package de.syscy.bguilib.creator.gui;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.syscy.bguilib.BGGUI;
import de.syscy.bguilib.components.BGButton;
import de.syscy.bguilib.components.BGCheckButton;
import de.syscy.bguilib.components.BGSlider;
import de.syscy.bguilib.components.BGTextInput;
import de.syscy.bguilib.components.icon.ItemIcon;
import de.syscy.bguilib.components.listener.ButtonClickListener;
import de.syscy.bguilib.components.listener.CheckButtonToggleListener;
import de.syscy.bguilib.components.listener.SliderValueChangeListener;
import de.syscy.bguilib.components.listener.TextInputListener;
import de.syscy.bguilib.creator.guidata.BGButtonData;
import de.syscy.bguilib.creator.guidata.GUIData;
import de.syscy.bguilib.util.Util;
import lombok.Getter;

public class EditButtonGUI extends BGGUI {
	private @Getter BGGUI guiBefore;
	private @Getter BGButtonData buttonData;
	private @Getter GUIData guiData;

	public EditButtonGUI(final BGGUI guiBefore, final BGButtonData buttonData, final GUIData guiData) {
		this.guiBefore = guiBefore;
		this.buttonData = buttonData;
		this.guiData = guiData;

		setHeight(6);
		setTitle("Edit Button GUI");

		BGButton backButton = new BGButton(8, 0, "Back");
		backButton.setButtonIcon(new ItemIcon(new ItemStack(Material.SLIME_BALL)));
		backButton.addClickListener(new ButtonClickListener() {
			public void onClick(Player player) {
				EditButtonGUI.this.close();
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

		BGSlider buttonXSlider = new BGSlider(0, 3, 3, "Button X", 0, 8);
		buttonXSlider.setValue(buttonData.getX());
		buttonXSlider.addValueChangedListener(new SliderValueChangeListener() {
			public void onValueChange(BGSlider slider) {
				buttonData.setX(slider.getValue());
			}
		});
		add(buttonXSlider);

		BGSlider buttonYSlider = new BGSlider(4, 3, 3, "Button Y", 0, guiData.getHeight());
		buttonYSlider.setValue(buttonData.getY());
		buttonYSlider.addValueChangedListener(new SliderValueChangeListener() {
			public void onValueChange(BGSlider slider) {
				buttonData.setY(slider.getValue());
			}
		});
		add(buttonYSlider);

		BGSlider buttonWidthSlider = new BGSlider(0, 5, 3, "Button Width", 1, 9);
		buttonWidthSlider.setValue(buttonData.getWidth());
		buttonWidthSlider.addValueChangedListener(new SliderValueChangeListener() {
			public void onValueChange(BGSlider slider) {
				buttonData.setWidth(slider.getValue());
			}
		});
		add(buttonWidthSlider);

		BGSlider buttonHeightSlider = new BGSlider(4, 5, 3, "Button Height", 1, guiData.getHeight());
		buttonHeightSlider.setValue(buttonData.getHeight());
		buttonHeightSlider.addValueChangedListener(new SliderValueChangeListener() {
			public void onValueChange(BGSlider slider) {
				buttonData.setHeight(slider.getValue());
			}
		});
		add(buttonHeightSlider);

		BGButton removeButton = new BGButton(8, 5, "Remove button");
		removeButton.setButtonIcon(new ItemIcon(new ItemStack(Material.BARRIER)));
		removeButton.addClickListener(new ButtonClickListener() {
			public void onClick(Player player) {
				guiData.getComponents().remove(buttonData);
				EditButtonGUI.this.close();
				guiBefore.unhide();
			}
		});
		add(removeButton);
	}
}