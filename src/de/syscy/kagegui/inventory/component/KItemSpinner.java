package de.syscy.kagegui.inventory.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import de.syscy.kagecore.util.Util;
import de.syscy.kagegui.IInventoryWrapper;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

public class KItemSpinner extends KComponent {
	protected @Getter @Setter SpinnerMode spinnerMode = SpinnerMode.ITEM_IN_CENTER;

	protected @Getter List<SpinnerItem> spinnerItems = new ArrayList<>();

	protected @Getter int rotationsLeft;
	protected int currentOffset = 0;
	protected int waitingTimer = 1;

	protected @Getter boolean finished = false;

	public KItemSpinner(int x, int y) {
		super(x, y);

		rotationsLeft = Util.getRandom().nextInt(10) + 10;
	}

	@Override
	public void update() {
		for(SpinnerItem spinnerItem : spinnerItems) {
			spinnerItem.getDisplayLabel().update();
		}

		waitingTimer--;

		if(waitingTimer == 0) {
			if(rotationsLeft <= 0) {
				finished = true;

				if(spinnerMode == SpinnerMode.ITEM_IN_CENTER) {
					spinnerItems.get(currentOffset).getResultRunnable().run();
				} else if(spinnerMode == SpinnerMode.ALL_CURRENT_ITEMS) {
					for(int i = 0; i < width; i++) {
						int center = Math.max(1, width / 2);
						int spinnerItemIndex = clampSpinnerItemIndex(i - center + currentOffset);

						spinnerItems.get(spinnerItemIndex).getResultRunnable().run();
					}
				}

				gui.getPlayer().playSound(gui.getPlayer().getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
			} else {
				rotationsLeft--;
				currentOffset++;

				if(currentOffset >= spinnerItems.size()) {
					currentOffset = 0;
				}

				waitingTimer = rotationsLeft > 5 ? 1 : waitingTimer > 3 ? 2 : 3;

				gui.getPlayer().playSound(gui.getPlayer().getLocation(), Sound.ITEM_FLINTANDSTEEL_USE, 1, 1);
			}
		}

		if(waitingTimer >= -100) {
			gui.markDirty();
		}
	}

	@Override
	public void render(IInventoryWrapper inventory) {
		int center = Math.max(1, width / 2);

		if(finished) {
			int timer = Math.abs(waitingTimer);
			//TODO: Blinking doesn't work
			if(timer > 100 || timer % 4 == 0) {
				if(spinnerMode == SpinnerMode.ITEM_IN_CENTER) {
					renderSpinnerItem(inventory, spinnerItems.get(currentOffset), center);
				} else if(spinnerMode == SpinnerMode.ALL_CURRENT_ITEMS) {
					renderSpinnerItems(inventory, center);
				}
			}
		} else {
			renderSpinnerItems(inventory, center);
		}
	}

	private void renderSpinnerItems(IInventoryWrapper inventory, int center) {
		for(int i = 0; i < width; i++) {
			int spinnerItemIndex = clampSpinnerItemIndex(i - center + currentOffset);

			renderSpinnerItem(inventory, spinnerItems.get(spinnerItemIndex), i);
		}
	}

	private int clampSpinnerItemIndex(int spinnerItemIndex) {
		spinnerItemIndex = Math.abs(spinnerItemIndex);

		return spinnerItemIndex % spinnerItems.size();
	}

	private void renderSpinnerItem(IInventoryWrapper inventory, SpinnerItem spinnerItem, int position) {
		KLabel displayLabel = spinnerItem.getDisplayLabel();
		displayLabel.setX(x + position);
		displayLabel.setY(y);
		displayLabel.render(inventory);
	}

	@Override
	public void onClick(InventoryClickEvent event, Player player, int localX, int localY) {

	}

	public void finish() {
		if(finished) {
			return;
		}

		currentOffset = Util.nextIntRange(Util.getRandom(), 0, spinnerItems.size() - 1);

		for(int i = 0; i < width; i++) {
			int center = Math.max(1, width / 2);
			int spinnerItemIndex = clampSpinnerItemIndex(i - center + currentOffset);

			spinnerItems.get(spinnerItemIndex).getResultRunnable().run();
		}
	}

	public void setSpinnerItems(List<SpinnerItem> spinnerItems) {
		Collections.shuffle(spinnerItems);

		if(spinnerItems.isEmpty()) {
			throw new IllegalArgumentException("You need to have at least one spinner item!");
		}

		this.spinnerItems = spinnerItems;
	}

	@Data
	public static class SpinnerItem {
		private final KLabel displayLabel;
		private final Runnable resultRunnable;
	}

	public static enum SpinnerMode {
		ALL_CURRENT_ITEMS, ITEM_IN_CENTER;
	}
}