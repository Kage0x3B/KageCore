package de.syscy.kagecore.command.argument;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.base.Function;

import de.syscy.kagecore.command.exception.InvalidCommandArgumentException;
import lombok.Setter;
import lombok.experimental.Accessors;

@SuppressWarnings("deprecation")
public class PlayerArgument extends CommandArgument<OfflinePlayer> {
	public static Function<OfflinePlayer, Boolean> ONLINE_REQUIREMENT = new Function<OfflinePlayer, Boolean>() {
		@Override
		public Boolean apply(OfflinePlayer player) {
			return player.isOnline();
		}
	};

	public static Function<OfflinePlayer, Boolean> IS_OP_REQUIREMENT = new Function<OfflinePlayer, Boolean>() {
		@Override
		public Boolean apply(OfflinePlayer player) {
			return player.isOp();
		}
	};

	public static Function<OfflinePlayer, Boolean> BANNED_REQUIREMENT = new Function<OfflinePlayer, Boolean>() {
		@Override
		public Boolean apply(OfflinePlayer player) {
			return player.isBanned();
		}
	};

	protected Function<OfflinePlayer, Boolean> playerIncludedFunction;

	public PlayerArgument(PlayerArgumentBuilder builder) {
		super(builder);

		playerIncludedFunction = builder.playerIncludedFunction;
	}

	@Override
	public OfflinePlayer getValue(CommandSender sender, String[] args) {
		return args.length <= index ? defaultValue : Bukkit.getOfflinePlayer(args[index]);
	}

	@Override
	public void checkArg(CommandSender sender, String[] args) {
		if(args.length <= index) {
			return;
		}

		OfflinePlayer player = Bukkit.getOfflinePlayer(args[index]);

		if(player == null) {
			throw new InvalidCommandArgumentException(name);
		}

		if(playerIncludedFunction != null) {
			if(!playerIncludedFunction.apply(player)) {
				throw new InvalidCommandArgumentException(name);
			}
		}

		if(allowedValuesFunction != null) {
			List<String> allowedValues = allowedValuesFunction.apply(sender);

			if(!allowedValues.isEmpty() && !allowedValues.contains(args[index])) {
				throw new InvalidCommandArgumentException(name);
			}
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args) {
		List<String> suggestions = new ArrayList<>();

		for(OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
			Player player = offlinePlayer.getPlayer();

			String name = player != null ? player.getDisplayName() : offlinePlayer.getName();

			if(name.toLowerCase().startsWith(args[index].toLowerCase()) && playerIncludedFunction.apply(offlinePlayer)) {
				suggestions.add(name);
			}
		}

		return suggestions;
	}

	public static PlayerArgumentBuilder create(String name) {
		return new PlayerArgumentBuilder(name);
	}

	@Accessors(fluent = true)
	public static class PlayerArgumentBuilder extends CommandArgumentBuilder<OfflinePlayer> {
		protected @Setter Function<OfflinePlayer, Boolean> playerIncludedFunction;

		private PlayerArgumentBuilder(String name) {
			super(name);
		}

		@Override
		public CommandArgument<OfflinePlayer> build() {
			return new PlayerArgument(this);
		}
	}
}