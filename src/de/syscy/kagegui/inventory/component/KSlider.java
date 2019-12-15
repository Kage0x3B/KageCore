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

public class KSlider<T extends Number> extends KComponent {
	private final Class<T> numberClass;

	protected @Getter @Setter String title;
	protected final @Getter LoreBuilder loreBuilder = new LoreBuilder();

	protected @Getter @Setter ItemIcon arrowIcon = new ItemIcon(new ItemStack(Material.NETHER_STAR));
	protected @Getter @Setter ItemIcon lineIcon = new ItemIcon(new ItemStack(Material.WHITE_STAINED_GLASS_PANE));
	protected @Getter @Setter ItemIcon knobIcon = new ItemIcon(new ItemStack(Material.PURPLE_STAINED_GLASS_PANE));

	protected T value;
	protected T step;
	protected T minValue;
	protected T maxValue;
	protected @Getter int knobX;

	protected @Setter SliderValueChangeListener<T> valueChangeListener;

	public KSlider(Class<T> numberClass, int x, int y) {
		super(x, y);

		this.numberClass = numberClass;

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

			checkValues();

			int sections = width - 2;
			double valueRange = maxValue.doubleValue() - minValue.doubleValue();

			setValue(castNumber(valueRange / sections * (double) knobX - valueRange / sections));
		}
	}

	public void setValue(T value) {
		this.value = value;

		checkValues();

		if(value.doubleValue() > maxValue.doubleValue()) {
			value = castNumber(maxValue);
		}

		if(value.doubleValue() < minValue.doubleValue()) {
			value = castNumber(minValue);
		}

		onValueChange();
	}

	public void increaseValue() {
		checkValues();

		value = castNumber(value.doubleValue() + step.doubleValue());

		if(value.doubleValue() > maxValue.doubleValue()) {
			value = maxValue;
		}

		onValueChange();
	}

	public void decreaseValue() {
		checkValues();

		value = castNumber(value.doubleValue() - step.doubleValue());

		if(value.doubleValue() < minValue.doubleValue()) {
			value = minValue;
		}

		onValueChange();
	}

	public void onValueChange() {
		if(valueChangeListener != null) {
			valueChangeListener.onValueChange(this);
		}

		checkValues();

		int sections = width - 2;
		double valueRange = maxValue.doubleValue() - minValue.doubleValue();

		if(width > 3) {
			knobX = (int) (value.doubleValue() / valueRange * sections + 1);
			knobX = Math.min(knobX, x + width - 1);
		}

		loreBuilder.set(LoreBuilder.KCOMPONENT_LORE, "Current value: " + value);

		if(gui != null) {
			gui.markDirty();
		}
	}

	public void setMinValue(T minValue) {
		this.minValue = minValue;

		checkValues();

		if(value.doubleValue() < minValue.doubleValue()) {
			value = minValue;
		}
	}

	public void setMaxValue(T maxValue) {
		this.maxValue = maxValue;

		checkValues();

		if(value.doubleValue() > maxValue.doubleValue()) {
			value = maxValue;
		}
	}

	public void setStep(T step) {
		this.step = step;
	}

	public T getValue() {
		checkValues();

		return castNumber(value);
	}

	public T getMinValue() {
		checkValues();

		return castNumber(minValue);
	}

	public T getMaxValue() {
		checkValues();

		return castNumber(maxValue);
	}

	private void checkValues() {
		if(value == null) {
			value = castNumber(1);
		}

		if(step == null) {
			step = castNumber(1);
		}

		if(minValue == null) {
			minValue = castNumber(1);
		}

		if(maxValue == null) {
			maxValue = castNumber(100);
		}
	}

	//A cool little trick I came up with *proud of myself* :D
	private T castNumber(Number number) {
		if(numberClass.equals(Integer.class)) {
			return (T) numberClass.cast(number.intValue());
		} else if(numberClass.equals(Double.class)) {
			return (T) numberClass.cast(number.doubleValue());
		} else if(numberClass.equals(Float.class)) {
			return (T) numberClass.cast(number.floatValue());
		} else if(numberClass.equals(Double.class)) {
			return (T) numberClass.cast(number.doubleValue());
		} else if(numberClass.equals(Long.class)) {
			return (T) numberClass.cast(number.longValue());
		} else if(numberClass.equals(Short.class)) {
			return (T) numberClass.cast(number.shortValue());
		} else if(numberClass.equals(Byte.class)) {
			return (T) numberClass.cast(number.byteValue());
		} else {
			return null;
		}
	}
}