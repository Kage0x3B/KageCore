package de.syscy.kagegui.inventory.component;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import de.syscy.kagegui.IInventoryWrapper;
import de.syscy.kagegui.icon.ItemIcon;
import de.syscy.kagegui.inventory.listener.SliderValueChangeListener;
import de.syscy.kagegui.util.Lore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

public class KSlider extends KComponent {
	private @Getter String title;
	private @Getter Lore lore;

	private @Getter ItemIcon arrowIcon;
	private @Getter ItemIcon lineIcon;
	private @Getter ItemIcon knobIcon;

	private @Getter int value;
	private @Getter int step;
	private @Getter int minValue;
	private @Getter int maxValue;
	private @Getter int knobX;

	private SliderValueChangeListener valueChangeListener;

	private KSlider(Builder builder) {
		super(builder);

		title = builder.title;
		lore = builder.lore;
		arrowIcon = builder.arrowIcon;
		lineIcon = builder.lineIcon;
		knobIcon = builder.knobIcon;

		value = builder.value;
		step = builder.step;
		minValue = builder.minValue;
		maxValue = builder.maxValue;

		valueChangeListener = builder.valueChangeListener;

		if(value < minValue) {
			value = minValue;
		} else if(value > maxValue) {
			value = maxValue;
		}

		if(x + width > 9) {
			width = 9 - x;
		} else if(width < 2) {
			width = 2;
		}

		width = builder.width;

		onValueChange();
	}

	@Override
	public void update() {
		arrowIcon.update(gui);
		lineIcon.update(gui);
		knobIcon.update(gui);
	}

	@Override
	public void render(IInventoryWrapper inventory) {
		this.renderItem(inventory, x, y, width, height, lineIcon, title, lore);
		this.renderItem(inventory, x + knobX, y, 1, height, knobIcon, title, lore);
		this.renderItem(inventory, x, y, 1, height, arrowIcon, title, lore);
		this.renderItem(inventory, x + width - 1, y, 1, height, arrowIcon, title, lore);
	}

	@Override
	public void onClick(InventoryClickEvent event, Player player, int localX, int localY) {
		if(localX == 0) {
			decreaseValue();
		} else if(localX == width - 1) {
			increaseValue();
		} else if(localX > 0 && localX < width && localX != knobX) {
			knobX = localX;

			float sections = (float) (width - 2);
			float valueRange = (float) (maxValue - minValue);

			setValue((int) (valueRange / sections * (float) knobX) - (int) (valueRange / sections));
		}
	}

	public void setValue(int value) {
		this.value = value;

		if(value > maxValue) {
			value = maxValue;
		}

		if(value < minValue) {
			value = minValue;
		}

		onValueChange();
	}

	public void increaseValue() {
		value += step;

		if(value > maxValue) {
			value = maxValue;
		}

		onValueChange();
	}

	public void decreaseValue() {
		value -= step;

		if(value < minValue) {
			value = minValue;
		}

		onValueChange();
	}

	public void onValueChange() {
		lore.setTemporaryFirstLine("Current value: " + value);

		if(valueChangeListener != null) {
			valueChangeListener.onValueChange(this);
		}

		float sections = (float) (width - 2);
		float valueRange = (float) (maxValue - minValue);

		if(width > 3) {
			knobX = (int) ((float) value / valueRange * sections) + 1;
		}

		gui.markDirty();
	}

	public void setMinValue(int minValue) {
		this.minValue = minValue;

		if(value < minValue) {
			value = minValue;
		}
	}

	public void setMaxValue(int maxValue) {
		minValue = maxValue;

		if(value > maxValue) {
			value = maxValue;
		}
	}

	public static Builder builder() {
		return new Builder();
	}

	@Accessors(fluent = true)
	public static class Builder extends KComponent.Builder<KSlider> {
		private @Setter String title;
		private @Setter Lore lore;

		private @Setter ItemIcon arrowIcon = new ItemIcon(new ItemStack(Material.NETHER_STAR));
		private @Setter ItemIcon lineIcon = new ItemIcon(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 2));
		private @Setter ItemIcon knobIcon = new ItemIcon(new ItemStack(Material.STAINED_GLASS_PANE));

		private @Setter int value = 0;
		private @Setter int step = 1;
		private @Setter int minValue = 0;
		private @Setter int maxValue = 100;

		private @Setter SliderValueChangeListener valueChangeListener;

		@Override
		public KSlider build() {
			return new KSlider(this);
		}
	}
}