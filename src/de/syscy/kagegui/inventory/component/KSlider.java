package de.syscy.kagegui.inventory.component;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import de.syscy.kagegui.IInventoryWrapper;
import de.syscy.kagegui.icon.ItemIcon;
import de.syscy.kagegui.inventory.listener.SliderValueChangeListener;
import de.syscy.kagegui.util.LoreBuilder;
import lombok.Getter;
import lombok.Setter;

public class KSlider extends KComponent {
	protected @Getter @Setter String title;
	protected final @Getter LoreBuilder loreBuilder = new LoreBuilder();

	protected @Getter @Setter ItemIcon arrowIcon = new ItemIcon(new ItemStack(Material.NETHER_STAR));
	protected @Getter @Setter ItemIcon lineIcon = new ItemIcon(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 2));
	protected @Getter @Setter ItemIcon knobIcon = new ItemIcon(new ItemStack(Material.STAINED_GLASS_PANE));

	protected @Getter int value = 0;
	protected @Getter @Setter int step = 1;
	protected @Getter int minValue = 0;
	protected @Getter int maxValue = 100;
	protected @Getter int knobX;

	protected @Setter SliderValueChangeListener valueChangeListener;

	public KSlider(int x, int y) {
		super(x, y);

		loreBuilder.set(LoreBuilder.KCOMPONENT_LORE, "Current value: " + value);

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
		this.renderItem(inventory, x, y, width, height, lineIcon, title, loreBuilder);
		this.renderItem(inventory, x + knobX, y, 1, height, knobIcon, title, loreBuilder);
		this.renderItem(inventory, x, y, 1, height, arrowIcon, title, loreBuilder);
		this.renderItem(inventory, x + width - 1, y, 1, height, arrowIcon, title, loreBuilder);
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
		if(valueChangeListener != null) {
			valueChangeListener.onValueChange(this);
		}

		float sections = (float) (width - 2);
		float valueRange = (float) (maxValue - minValue);

		if(width > 3) {
			knobX = (int) ((float) value / valueRange * sections) + 1;
		}

		loreBuilder.set(LoreBuilder.KCOMPONENT_LORE, "Current value: " + value);
		gui.markDirty();
	}

	public void setMinValue(int minValue) {
		this.minValue = minValue;

		if(value < minValue) {
			value = minValue;
		}
	}

	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;

		if(value > maxValue) {
			value = maxValue;
		}
	}
}