package de.syscy.bguilib.creator.hotbarguidata;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.w3c.dom.Element;

import de.syscy.bguilib.components.BGHotbarButton;
import de.syscy.bguilib.components.BGHotbarComponent;
import de.syscy.bguilib.components.icon.ItemIcon;
import de.syscy.bguilib.components.listener.ButtonClickListener;
import de.syscy.bguilib.util.Lore;
import de.syscy.bguilib.util.Util;
import lombok.Getter;
import lombok.Setter;

public class BGHotbarButtonData extends BGHotbarComponentData {
	public static String componentName = "hbutton";

	private @Getter @Setter ItemStack buttonIcon = new ItemStack(Material.STONE);

	private @Getter @Setter String title = "Hotbar Button";

	private @Getter @Setter Lore lore = new Lore(new String[] { "" });

	private @Getter @Setter String onClickCommand = "";

	private @Getter @Setter boolean oCCExecutedByServer = true;

	public BGHotbarComponent toBGHotbarComponent() {
		BGHotbarButton button = new BGHotbarButton(this.x, this.title);
		button.setLore(this.lore);
		button.setButtonIcon(new ItemIcon(this.buttonIcon));
		
		if(!this.onClickCommand.isEmpty()) {
			button.addClickListener(new ButtonClickListener() {
				public void onClick(Player player) {
					String command = String.format(BGHotbarButtonData.this.onClickCommand, new Object[] { player.getName() });
					if(BGHotbarButtonData.this.oCCExecutedByServer) {
						Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
					} else {
						Bukkit.getServer().dispatchCommand(player, command);
					}
				}
			});
		}
		
		return button;
	}

	public BGHotbarComponentData parseXML(Element element) {
		BGHotbarButtonData buttonData = new BGHotbarButtonData();
		buttonData.parseComponentValuesXML(element);
		buttonData.setButtonIcon(Util.itemStackFromString(element.getAttribute("buttonIcon")));
		buttonData.setTitle(element.getAttribute("title"));
		buttonData.setLore(Lore.fromString(element.getAttribute("lore")));
		buttonData.setOnClickCommand(element.getAttribute("onClickCommand"));
		buttonData.setOCCExecutedByServer(Boolean.parseBoolean(element.getAttribute("oCCExecutedByServer")));

		return buttonData;
	}

	public void saveToXML(Element element) {
		saveComponentValuesToXML(element);

		element.setAttribute("buttonIcon", Util.itemStackToString(this.buttonIcon));
		element.setAttribute("title", this.title);
		element.setAttribute("lore", this.lore.toString());
		element.setAttribute("onClickCommand", this.onClickCommand);
		element.setAttribute("oCCExecutedByServer", Boolean.toString(isOCCExecutedByServer()));
	}

	public String getComponentName() {
		return componentName;
	}
}