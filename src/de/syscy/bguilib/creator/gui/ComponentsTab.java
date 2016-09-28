package de.syscy.bguilib.creator.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.syscy.bguilib.BGUILib;
import de.syscy.bguilib.components.BGButton;
import de.syscy.bguilib.components.icon.ItemIcon;
import de.syscy.bguilib.components.listener.ButtonClickListener;
import de.syscy.bguilib.container.BGTab;
import de.syscy.bguilib.creator.guidata.BGButtonData;
import de.syscy.bguilib.creator.guidata.BGComponentData;
import de.syscy.bguilib.creator.guidata.GUIData;
import de.syscy.bguilib.util.Util;

public class ComponentsTab extends BGTab {
	private GUIData guiData;
	private BGButton addButtonButton;

	public ComponentsTab(final GUIData guiData) {
		super(new ItemIcon(new ItemStack(Material.BOOK)), new ItemIcon(new ItemStack(Material.PAPER)), "Components");

		this.guiData = guiData;

		this.addButtonButton = new BGButton(0, 0, "Add new button");
		this.addButtonButton.setButtonIcon(new ItemIcon(new ItemStack(Material.EMERALD)));
		this.addButtonButton.addClickListener(new ButtonClickListener() {
			public void onClick(Player player) {
				guiData.getComponents().add(new BGButtonData());
			}
		});
		updateComponents();
	}

	public void update() {
		super.update();

		updateComponents();
	}

	private void updateComponents() {
		this.components.clear();

		add(this.addButtonButton);

		int slot = 9;
		for(BGComponentData component : this.guiData.getComponents()) {
			if((component instanceof BGButtonData)) {
				final BGButtonData button = (BGButtonData) component;

				int[] buttonPosition = Util.toXYCoordinate(slot);

				BGButton componentButton = new BGButton(buttonPosition[0], buttonPosition[1], button.getTitle());
				componentButton.setButtonIcon(new ItemIcon(button.getButtonIcon()));
				componentButton.setLore(button.getLore());
				componentButton.addClickListener(new ButtonClickListener() {
					public void onClick(Player player) {
						ComponentsTab.this.getGui().hide();
						BGUILib.showGUI(new EditButtonGUI(ComponentsTab.this.getGui(), button, ComponentsTab.this.guiData), player);
					}
				});
				add(componentButton);

				slot++;
			}
		}
	}
}