package de.syscy.kagegui.yaml.inventory;

import org.bukkit.configuration.ConfigurationSection;

import de.syscy.kagegui.inventory.component.KComponent;

public class KSliderFactory implements IKComponentFactory {
	@Override
	public KComponent createComponent(final YamlGUI gui, ConfigurationSection componentSection) {
		//		KSlider slider = new KSlider(componentSection.getInt("x", 0), componentSection.getInt("y", 0));
		//		slider.setWidth(componentSection.getInt("width", 1));
		//		slider.setHeight(componentSection.getInt("height", 1));
		//		slider.setTitle(componentSection.getString("title", ""));
		//
		//		slider.setValue(componentSection.getInt("value", 0));
		//		slider.setStep(componentSection.getInt("step", 1));
		//		slider.setMinValue(componentSection.getInt("minValue", 0));
		//		slider.setMaxValue(componentSection.getInt("maxValue", 100));
		//
		//		if(componentSection.contains("arrowIcon")) {
		//			slider.setArrowIcon(ItemIconParser.parseIcon(componentSection.getConfigurationSection("arrowIcon")));
		//		}
		//
		//		if(componentSection.contains("lineIcon")) {
		//			slider.setLineIcon(ItemIconParser.parseIcon(componentSection.getConfigurationSection("lineIcon")));
		//		}
		//
		//		if(componentSection.contains("knobIcon")) {
		//			slider.setKnobIcon(ItemIconParser.parseIcon(componentSection.getConfigurationSection("knobIcon")));
		//		}
		//
		//		if(componentSection.contains("lore")) {
		//			if(componentSection.isString("lore")) {
		//				slider.getLoreBuilder().set(LoreBuilder.DEFAULT_DESCRIPTION, componentSection.getString("lore"));
		//			} else if(componentSection.isList("lore")) {
		//				slider.getLoreBuilder().set(LoreBuilder.DEFAULT_DESCRIPTION, componentSection.getStringList("lore"));
		//			}
		//		}
		//
		//		if(componentSection.contains("valueChangeAction")) {
		//			ConfigurationSection actionSection = componentSection.getConfigurationSection("valueChangeAction");
		//			final IAction action = ActionParser.parseAction(actionSection);
		//
		//			slider.setValueChangeListener(new SliderValueChangeListener() {
		//				@Override
		//				public void onValueChange(KSlider<?> slider) {
		//					action.onTrigger(gui, gui.getPlayer(), slider, slider.getValue());
		//				}
		//			});
		//		}
		//
		//		return slider;
		return null;
	}
}