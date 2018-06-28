package de.syscy.kagecore.command.commands;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.syscy.kagecore.KageCore;
import de.syscy.kagecore.command.CommandBase;
import de.syscy.kagecore.command.argument.StringArgument;
import de.syscy.kagecore.command.exception.PlayerRequiredException;
import io.chazza.advancementapi.AdvancementAPI;
import io.chazza.advancementapi.AdvancementAPI.AdvancementAPIBuilder;
import io.chazza.advancementapi.FrameType;

public class TestNotificationCommand extends CommandBase<KageCore> {
	public TestNotificationCommand(KageCore plugin) {
		super(plugin, "testNotification", StringArgument.create("title").build(), StringArgument.create("description").build(), StringArgument.create("icon").build(), StringArgument.create("frame").build());
	}

	@Override
	public void onCommand(CommandSender sender) {
		if(!(sender instanceof Player)) {
			throw new PlayerRequiredException();
		}

		AdvancementAPIBuilder notificationBuilder = AdvancementAPI.builder(new NamespacedKey(plugin, "KageCore/Notification/" + UUID.randomUUID().toString()));
		notificationBuilder.title(ChatColor.translateAlternateColorCodes('&', getArguments().getString("title")));
		notificationBuilder.description(ChatColor.translateAlternateColorCodes('&', getArguments().getString("description").replaceAll("_", " ").replaceAll(";", "\n")));
		notificationBuilder.icon(getArguments().getString("icon"));
		notificationBuilder.frame(FrameType.getFromString(getArguments().getString("frame")));
		notificationBuilder.hidden(false);
		notificationBuilder.toast(true);

		notificationBuilder.build().show(plugin, (Player) sender);
	}
}