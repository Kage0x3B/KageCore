package de.syscy.kagegui.yaml.inventory;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import de.syscy.kagegui.inventory.component.KCheckButton;
import de.syscy.kagegui.inventory.component.KComponent;
import de.syscy.kagegui.inventory.listener.CheckButtonToggleListener;
import de.syscy.kagegui.util.LoreBuilder;
import de.syscy.kagegui.yaml.action.ActionParser;
import de.syscy.kagegui.yaml.action.IAction;
import de.syscy.kagegui.yaml.icon.ItemIconParser;

public class KCheckButtonFactory implements IKComponentFactory {
	@Override
	public KComponent createComponent(final YamlGUI gui, ConfigurationSection componentSection) {
		final KCheckButton checkButton = new KCheckButton(componentSection.getInt("x", 0), componentSection.getInt("y", 0));
		checkButton.setWidth(componentSection.getInt("width", 1));
		checkButton.setHeight(componentSection.getInt("height", 1));
		checkButton.setTitle(componentSection.getString("title", ""));
		checkButton.setIcon(ItemIconParser.parseIcon(componentSection));

		if(componentSection.contains("lore")) {
			if(componentSection.isString("lore")) {
				checkButton.getEnabledLoreBuilder().set(LoreBuilder.DEFAULT_DESCRIPTION, componentSection.getString("lore"));
				checkButton.getDisabledLoreBuilder().set(LoreBuilder.DEFAULT_DESCRIPTION, componentSection.getString("lore"));
			} else if(componentSection.isList("lore")) {
				checkButton.getEnabledLoreBuilder().set(LoreBuilder.DEFAULT_DESCRIPTION, componentSection.getStringList("lore"));
				checkButton.getDisabledLoreBuilder().set(LoreBuilder.DEFAULT_DESCRIPTION, componentSection.getStringList("lore"));
			}
		}

		if(componentSection.contains("enabledTitle")) {
			checkButton.setEnabledTitle(componentSection.getString("enabledTitle"));
		}

		if(componentSection.contains("disabledTitle")) {
			checkButton.setEnabledTitle(componentSection.getString("disabledTitle"));
		}

		if(componentSection.contains("enabledIcon")) {
			ConfigurationSection iconSection = componentSection.getConfigurationSection("enabledIcon");
			checkButton.setEnabledIcon(ItemIconParser.parseIcon(iconSection));
		}

		if(componentSection.contains("disabledIcon")) {
			ConfigurationSection iconSection = componentSection.getConfigurationSection("disabledIcon");
			checkButton.setDisabledIcon(ItemIconParser.parseIcon(iconSection));
		}

		if(componentSection.contains("enabledLore")) {
			if(componentSection.isString("enabledLore")) {
				checkButton.getEnabledLoreBuilder().set(LoreBuilder.DEFAULT_DESCRIPTION, componentSection.getString("enabledLore"));
			} else if(componentSection.isList("enabledLore")) {
				checkButton.getEnabledLoreBuilder().set(LoreBuilder.DEFAULT_DESCRIPTION, componentSection.getStringList("enabledLore"));
			}
		}

		if(componentSection.contains("disabledLore")) {
			if(componentSection.isString("disabledLore")) {
				checkButton.getDisabledLoreBuilder().set(LoreBuilder.DEFAULT_DESCRIPTION, componentSection.getString("disabledLore"));
			} else if(componentSection.isList("disabledLore")) {
				checkButton.getDisabledLoreBuilder().set(LoreBuilder.DEFAULT_DESCRIPTION, componentSection.getStringList("disabledLore"));
			}
		}

		if(componentSection.contains("toggleAction")) {
			ConfigurationSection actionSection = componentSection.getConfigurationSection("toggleAction");
			final IAction action = ActionParser.parseAction(actionSection);

			checkButton.setToggleListener(new CheckButtonToggleListener() {
				@Override
				public void onToggle(Player player, boolean enabled) {
					action.onTrigger(gui, player, checkButton, enabled);
				}
			});
		}

		return checkButton;
	}
}