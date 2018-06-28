package de.syscy.kagecore.command.commands;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import de.syscy.kagecore.KageCore;
import de.syscy.kagecore.command.CommandBase;
import de.syscy.kagecore.command.PlayerCommandBase;
import de.syscy.kagecore.command.argument.StringListArgument;
import de.syscy.kagecore.translation.Translator;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.*;

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