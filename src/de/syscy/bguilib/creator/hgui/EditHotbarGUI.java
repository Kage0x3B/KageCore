package de.syscy.bguilib.creator.hgui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.syscy.bguilib.BGGUI;
import de.syscy.bguilib.BGUILib;
import de.syscy.bguilib.components.BGButton;
import de.syscy.bguilib.components.icon.ItemIcon;
import de.syscy.bguilib.components.listener.ButtonClickListener;
import de.syscy.bguilib.container.BGPanel;
import de.syscy.bguilib.creator.hotbarguidata.BGHotbarButtonData;
import de.syscy.bguilib.creator.hotbarguidata.BGHotbarComponentData;
import de.syscy.bguilib.creator.hotbarguidata.HotbarGUIData;

public class EditHotbarGUI extends BGGUI {
	private HotbarGUIData guiData;

	public HotbarGUIData getGuiData() {
		return this.guiData;
	}

	public EditHotbarGUI(HotbarGUIData guiData) {
		this.guiData = guiData;
		setHeight(1);
		setTitle("Edit Hotbar GUI");

		updateComponents();
	}

	public void update() {
		super.update();

		updateComponents();
	}

	private void updateComponents() {
		BGPanel panel = (BGPanel) this.container;

		panel.getComponents().clear();

		final EditHotbarGUI gui = this;
		for(int i = 0; i < 9; i++) {
			BGHotbarComponentData component = this.guiData.getComponents()[i];
			if(component != null) {
				if((component instanceof BGHotbarButtonData)) {
					final BGHotbarButtonData button = (BGHotbarButtonData) component;

					BGButton componentButton = new BGButton(i, 0, button.getTitle());
					componentButton.setButtonIcon(new ItemIcon(button.getButtonIcon()));
					componentButton.setLore(button.getLore());
					componentButton.addClickListener(new ButtonClickListener() {
						public void onClick(Player player) {
							EditHotbarGUI.this.hide();
							BGUILib.showGUI(new EditHotbarButtonGUI(gui, button, EditHotbarGUI.this.guiData), player);
						}
					});
					add(componentButton);
				}
			} else {
				final int slot = i;

				BGButton addButton = new BGButton(i, 0, "Add button");
				addButton.setButtonIcon(new ItemIcon(new ItemStack(Material.EMERALD)));
				addButton.addClickListener(new ButtonClickListener() {
					public void onClick(Player player) {
						BGHotbarButtonData hotbarButtonData = new BGHotbarButtonData();
						hotbarButtonData.setX(slot);
						EditHotbarGUI.this.guiData.getComponents()[slot] = hotbarButtonData;
					}
				});
				add(addButton);
			}
		}
	}
}