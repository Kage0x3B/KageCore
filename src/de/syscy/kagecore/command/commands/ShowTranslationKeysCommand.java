package de.syscy.kagecore.command.commands;

import de.syscy.kagecore.KageCore;
import de.syscy.kagecore.command.PlayerCommandBase;
import de.syscy.kagecore.translation.Translator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ShowTranslationKeysCommand extends PlayerCommandBase<KageCore> {
	public ShowTranslationKeysCommand(KageCore plugin) {
		super(plugin, "showTranslationKeys");
	}

	@Override
	public void onPlayerCommand(@NotNull Player sender) {
		ItemStack itemInHand = sender.getInventory().getItemInMainHand();

		if(itemInHand == null || itemInHand.getType() == Material.AIR) {
			sender.sendMessage(ChatColor.RED + "You need to hold an item in your hand!");

			return;
		}

		if(itemInHand.hasItemMeta()) {
			ItemMeta itemMeta = itemInHand.getItemMeta();

			if(itemMeta.hasDisplayName()) {
				itemMeta.setDisplayName(removeTranslationSigns(itemMeta.getDisplayName()));
			}

			if(itemMeta.hasLore()) {
				List<String> lore = itemMeta.getLore();

				lore.replaceAll(this::removeTranslationSigns);

				itemMeta.setLore(lore);
			}

			itemInHand.setItemMeta(itemMeta);
		}

		sender.getInventory().setItemInMainHand(itemInHand);
	}

	private String removeTranslationSigns(String string) {
		return string.replace(Translator.SIGN, '*');
	}
}