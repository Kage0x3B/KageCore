package de.syscy.kagegui.yaml.crafting;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import de.syscy.kagegui.crafting.CraftingButtonClickListener;
import de.syscy.kagegui.crafting.KCraftingButton;
import de.syscy.kagegui.crafting.KCraftingComponent;
import de.syscy.kagegui.util.ClickType;
import de.syscy.kagegui.util.LoreBuilder;
import de.syscy.kagegui.yaml.action.ActionParser;
import de.syscy.kagegui.yaml.action.IAction;
import de.syscy.kagegui.yaml.icon.ItemIconParser;

public class KCraftingButtonFactory implements ICraftingComponentFactory {
	@Override
	public KCraftingComponent createComponent(final YamlCraftingGUI gui, ConfigurationSection componentSection) {
		int craftingSlot = Math.max(componentSection.getInt("slot", 0), componentSection.getInt("craftingSlot", 0));
		KCraftingButton button = new KCraftingButton(craftingSlot);
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

					button.setClickListener(clickType, new CraftingButtonClickListener() {
						@Override
						public void onClick(KCraftingButton button, Player player) {
							action.onTrigger(gui, player, button);
						}
					}, actionSection.getString("actionDescription", ""));
				}
			}
		}

		return button;
	}
}