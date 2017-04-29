package de.syscy.kagegui.inventory.listener;

import de.syscy.kagegui.inventory.component.KSlider;

public interface SliderValueChangeListener<T extends Number> {
	public void onValueChange(KSlider<T> slider);
}