package de.syscy.kagegui.inventory.listener;

import org.bukkit.entity.Player;

import de.syscy.kagegui.inventory.component.KItemSpinner;

public interface SpinnerFinishListener {
	public void onSpinnerFinish(KItemSpinner spinner, Player player);
}