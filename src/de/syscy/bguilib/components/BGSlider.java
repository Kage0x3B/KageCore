package de.syscy.bguilib.components;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import de.syscy.bguilib.components.icon.ItemIcon;
import de.syscy.bguilib.components.listener.SliderValueChangeListener;
import de.syscy.bguilib.util.Lore;
import lombok.Getter;
import lombok.Setter;

public class BGSlider extends BGComponent {
	private List<SliderValueChangeListener> listeners = new ArrayList<>();
	private @Getter @Setter ItemIcon arrowIcon;
	private @Getter @Setter ItemIcon lineIcon;
	private @Getter @Setter ItemIcon knobIcon;
	private @Getter @Setter String title = "Slider";
	private @Getter @Setter Lore lore = new Lore(new String[] { "" });
	private @Getter int value = 0;
	private @Getter @Setter int step = 1;
	private @Getter int minValue = 0;
	private @Getter int maxValue = 100;
	private @Getter int knobX = 0;

	public BGSlider(int x, int y, int width, String title, int minValue, int maxValue) {
		super(x, y, 1, 1);
		this.title = title;
		this.minValue = minValue;
		this.maxValue = maxValue;

		if(this.value < minValue) {
			this.value = minValue;
		} else if(this.value > maxValue) {
			this.value = maxValue;
		}

		if(x + width > 9) {
			width = 9 - x;
		} else if(width < 2) {
			width = 2;
		}

		this.width = width;
		this.knobX = width / 2;
		this.setArrowIcon(new ItemIcon(new ItemStack(Material.NETHER_STAR)));
		this.setLineIcon(new ItemIcon(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 2)));
		this.setKnobIcon(new ItemIcon(new ItemStack(Material.STAINED_GLASS_PANE)));
		this.onValueChange();
	}

	public void update() {
		this.arrowIcon.update();
		this.lineIcon.update();
		this.knobIcon.update();
	}

	public void render(BGInventory inventory) {
		this.renderItem(inventory, this.x, this.y, this.width, this.height, this.lineIcon, this.title, this.lore, true);
		this.renderItem(inventory, this.x + this.knobX, this.y, 1, this.height, this.knobIcon, this.title, this.lore, true);
		this.renderItem(inventory, this.x, this.y, 1, this.height, this.arrowIcon, this.title, this.lore, true);
		this.renderItem(inventory, this.x + this.width - 1, this.y, 1, this.height, this.arrowIcon, this.title, this.lore, true);
	}

	public void onClick(InventoryClickEvent event, Player player, int localX, int localY) {
		if(localX == 0) {
			this.decreaseValue();
		} else if(localX == this.width - 1) {
			this.increaseValue();
		} else if(localX > 0 && localX < this.width && localX != this.knobX) {
			this.knobX = localX;
			
			float sections = (float) (this.width - 2);
			float valueRange = (float) (this.maxValue - this.minValue);
			
			this.setValue((int) (valueRange / sections * (float) this.knobX) - (int) (valueRange / sections));
		}
	}

	public void addValueChangedListener(SliderValueChangeListener listener) {
		this.listeners.add(listener);
	}

	public void setValue(int value) {
		this.value = value;

		if(value > this.maxValue) {
			value = this.maxValue;
		}

		if(value < this.minValue) {
			value = this.minValue;
		}

		this.onValueChange();
	}

	public void increaseValue() {
		this.value += this.step;

		if(this.value > this.maxValue) {
			this.value = this.maxValue;
		}

		this.onValueChange();
	}

	public void decreaseValue() {
		this.value -= this.step;
		
		if(this.value < this.minValue) {
			this.value = this.minValue;
		}

		this.onValueChange();
	}

	public void onValueChange() {
		this.lore.setTemporaryFirstLine("Current value: " + this.value);

		for(SliderValueChangeListener listener : listeners) {
			listener.onValueChange(this);
		}

		float sections = (float) (this.width - 2);
		float valueRange = (float) (this.maxValue - this.minValue);

		if(this.width > 3) {
			this.knobX = (int) ((float) this.value / valueRange * sections) + 1;
		}
	}

	public void setMinValue(int minValue) {
		this.minValue = minValue;

		if(this.value < minValue) {
			this.value = minValue;
		}
	}

	public void setMaxValue(int maxValue) {
		this.minValue = maxValue;

		if(this.value > maxValue) {
			this.value = maxValue;
		}
	}
}