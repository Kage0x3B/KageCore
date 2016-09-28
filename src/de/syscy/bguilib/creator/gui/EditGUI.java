package de.syscy.bguilib.creator.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.syscy.bguilib.BGGUI;
import de.syscy.bguilib.components.BGSlider;
import de.syscy.bguilib.components.BGTextInput;
import de.syscy.bguilib.components.icon.ItemIcon;
import de.syscy.bguilib.components.listener.SliderValueChangeListener;
import de.syscy.bguilib.components.listener.TextInputListener;
import de.syscy.bguilib.container.BGTab;
import de.syscy.bguilib.container.BGTabbedPanel;
import de.syscy.bguilib.creator.guidata.GUIData;
import lombok.Getter;

public class EditGUI extends BGGUI {
	private @Getter GUIData guiData;

	public EditGUI(final GUIData guiData) {
		this.guiData = guiData;
		setHeight(6);
		setTitle("Edit GUI");

		BGTab mainTab = new BGTab(new ItemIcon(new ItemStack(Material.COAL_BLOCK)), new ItemIcon(new ItemStack(Material.COMMAND)), "Main");

		BGTextInput titleInput = new BGTextInput(0, 0, "Gui title: ");
		titleInput.setText(guiData.getTitle());
		titleInput.addTextInputListener(new TextInputListener() {
			public void onTextInput(Player player, String text) {
				guiData.setTitle(text);
			}
		});
		mainTab.add(titleInput);

		BGSlider sizeSlider = new BGSlider(0, 1, 3, "Gui height", 1, 6);
		sizeSlider.setValue(guiData.getHeight());
		sizeSlider.addValueChangedListener(new SliderValueChangeListener() {
			public void onValueChange(BGSlider slider) {
				guiData.setHeight(slider.getValue());
			}
		});
		mainTab.add(sizeSlider);

		BGTab componentsTab = new ComponentsTab(guiData);

		BGTabbedPanel tabbedPanel = new BGTabbedPanel();
		tabbedPanel.add(mainTab);
		tabbedPanel.add(componentsTab);

		setContainer(tabbedPanel);
	}
}