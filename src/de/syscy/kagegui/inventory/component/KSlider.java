package de.syscy.kagegui.inventory.component;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import com.google.common.reflect.TypeToken;

import de.syscy.kagegui.IInventoryWrapper;
import de.syscy.kagegui.icon.ItemIcon;
import de.syscy.kagegui.inventory.listener.SliderValueChangeListener;
import de.syscy.kagegui.util.LoreBuilder;
import lombok.Getter;
import lombok.Setter;

public class KSlider<T extends Number> extends KComponent {
	private final TypeToken<T> typeToken = new TypeToken<T>(getClass()) {
		private static final long serialVersionUID = 1L;
	};

	private final Class<? super T> numberClass = typeToken.getRawType();

	protected @Getter @Setter String title;
	protected final @Getter LoreBuilder loreBuilder = new LoreBuilder();

	protected @Getter @Setter ItemIcon arrowIcon = new ItemIcon(new ItemStack(Material.NETHER_STAR));
	protected @Getter @Setter ItemIcon lineIcon = new ItemIcon(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 2));
	protected @Getter @Setter ItemIcon knobIcon = new ItemIcon(new ItemStack(Material.STAINED_GLASS_PANE));

	protected T value;
	protected T step;
	protected T minValue;
	protected T maxValue;
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

			int sections = width - 2;
			double valueRange = maxValue.doubleValue() - minValue.doubleValue();

			setValue(castNumber(valueRange / sections * (double) knobX - valueRange / sections));
		}
	}

	public void setValue(T value) {
		this.value = value;

		if(value.doubleValue() > maxValue.doubleValue()) {
			value = castNumber(maxValue);
		}

		if(value.doubleValue() < minValue.doubleValue()) {
			value = castNumber(minValue);
		}

		onValueChange();
	}

	public void increaseValue() {
		value = castNumber(value.doubleValue() + step.doubleValue());

		if(value.doubleValue() > maxValue.doubleValue()) {
			value = maxValue;
		}

		onValueChange();
	}

	public void decreaseValue() {
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

		int sections = width - 2;
		double valueRange = maxValue.doubleValue() - minValue.doubleValue();

		if(width > 3) {
			knobX = (int) (value.doubleValue() / valueRange * sections + 1);
		}

		loreBuilder.set(LoreBuilder.KCOMPONENT_LORE, "Current value: " + value);
		gui.markDirty();
	}

	public void setMinValue(T minValue) {
		this.minValue = minValue;

		if(value.doubleValue() < minValue.doubleValue()) {
			value = minValue;
		}
	}

	public void setMaxValue(T maxValue) {
		this.maxValue = maxValue;

		if(value.doubleValue() > maxValue.doubleValue()) {
			value = maxValue;
		}
	}

	public void setStep(T step) {
		this.step = step;
	}

	public T getValue() {
		return castNumber(value);
	}

	public T getMinValue() {
		return castNumber(minValue);
	}

	public T getMaxValue() {
		return castNumber(maxValue);
	}

	//A cool little trick I came up with *proud of myself* :D
	@SuppressWarnings("unchecked")
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