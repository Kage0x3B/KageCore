package de.syscy.kagegui.yaml.inventory;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import de.syscy.kagegui.inventory.component.KButton;
import de.syscy.kagegui.inventory.component.KComponent;
import de.syscy.kagegui.inventory.listener.ButtonClickListener;
import de.syscy.kagegui.util.ClickType;
import de.syscy.kagegui.util.LoreBuilder;
import de.syscy.kagegui.yaml.action.ActionParser;
import de.syscy.kagegui.yaml.action.IAction;
import de.syscy.kagegui.yaml.icon.ItemIconParser;

public class KButtonFactory implements IKComponentFactory {
	@Override
	public KComponent createComponent(final YamlGUI gui, ConfigurationSection componentSection) {
		KButton button = new KButton(componentSection.getInt("x", 0), componentSection.getInt("y", 0));
		button.setWidth(componentSection.getInt("width", 1));
		button.setHeight(componentSection.getInt("height", 1));
		button.setTitle(componentSection.getString("title", ""));
		button.setIcon(ItemIconParser.parseIcon(componentSection));

		if(componentSection.contains("lore")) {
			if(componentSection.isString("lore")) {
				button.getLoreBuilder().set(LoreBuilder.DEFAULT_DESCRIPTION, componentSection.getString("lore"));
			} else if(componentSection.isList("lore")) {
				button.getLoreBuilder().set(LoreBuilder.DEFAULT_DESCRIPTION, componentSection.getStringList("lore"));
			}
		}

		for(String key : componentSection.getKeys(false)) {
			for(ClickType clickType : ClickType.VALUES) {
				if(key.equalsIgnoreCase(clickType.name().replaceAll("_", "") + "action")) {
					ConfigurationSection actionSection = componentSection.getConfigurationSection(key);
					final IAction action = ActionParser.parseAction(actionSection);

					button.setClickListener(clickType, new ButtonClickListener() {
						@Override
						public void onClick(KButton button, Player player) {
							action.onTrigger(gui, player, button);
						}
					}, actionSection.getString("actionDescription", ""));
				}
			}
		}

		return button;
	}
}