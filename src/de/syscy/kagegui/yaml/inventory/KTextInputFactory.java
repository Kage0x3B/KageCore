package de.syscy.kagegui.yaml.inventory;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import de.syscy.kagegui.inventory.component.KComponent;
import de.syscy.kagegui.inventory.component.KTextInput;
import de.syscy.kagegui.inventory.listener.TextInputListener;
import de.syscy.kagegui.util.LoreBuilder;
import de.syscy.kagegui.yaml.action.ActionParser;
import de.syscy.kagegui.yaml.action.IAction;
import de.syscy.kagegui.yaml.icon.ItemIconParser;

public class KTextInputFactory implements IKComponentFactory {
	@Override
	public KComponent createComponent(final YamlGUI gui, ConfigurationSection componentSection) {
		final KTextInput textInput = new KTextInput(componentSection.getInt("x", 0), componentSection.getInt("y", 0));
		textInput.setWidth(componentSection.getInt("width", 1));
		textInput.setHeight(componentSection.getInt("height", 1));
		textInput.setTitle(componentSection.getString("title", ""));
		textInput.setIcon(ItemIconParser.parseIcon(componentSection));
		textInput.setText(componentSection.getString("text", ""));

		if(componentSection.contains("lore")) {
			if(componentSection.isString("lore")) {
				textInput.getLoreBuilder().set(LoreBuilder.DEFAULT_DESCRIPTION, componentSection.getString("lore"));
			} else if(componentSection.isList("lore")) {
				textInput.getLoreBuilder().set(LoreBuilder.DEFAULT_DESCRIPTION, componentSection.getStringList("lore"));
			}
		}

		if(componentSection.contains("textChangeAction")) {
			ConfigurationSection actionSection = componentSection.getConfigurationSection("textChangeAction");
			final IAction action = ActionParser.parseAction(actionSection);

			textInput.setTextInputListener(new TextInputListener() {
				@Override
				public void onTextInput(Player player, String text) {
					action.onTrigger(gui, player, textInput, text);
				}
			});
		}

		return textInput;
	}
}