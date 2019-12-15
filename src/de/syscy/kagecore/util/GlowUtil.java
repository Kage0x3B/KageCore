package de.syscy.kagecore.util;

import com.comphenix.packetwrapper.WrapperPlayServerEntityMetadata;
import com.comphenix.packetwrapper.WrapperPlayServerScoreboardTeam;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import de.syscy.kagecore.KageCore;
import de.syscy.kagecore.protocol.ProtocolUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class GlowUtil implements Listener {
	private static Map<UUID, GlowData> dataMap = new HashMap<>();

	//Options
	/**
	 * Default name-tag visibility (always, hideForOtherTeams, hideForOwnTeam, never)
	 */
	public static String TEAM_TAG_VISIBILITY = "always";
	/**
	 * Default push behaviour (always, pushOtherTeams, pushOwnTeam, never)
	 */
	public static String TEAM_PUSH = "always";

	/**
	 * Set the glowing-color of an entity
	 *
	 * @param entity        {@link Entity} to update
	 * @param color         {@link de.syscy.kagecore.util.GlowUtil.Color} of the glow, or <code>null</code> to stop glowing
	 * @param tagVisibility visibility of the name-tag (always, hideForOtherTeams, hideForOwnTeam, never)
	 * @param push          push behaviour (always, pushOtherTeams, pushOwnTeam, never)
	 * @param receiver      {@link Player} that will see the update
	 */
	public static void setGlowing(Entity entity, Color color, String tagVisibility, String push, Player receiver) {
		if(receiver == null) {
			return;
		}

		boolean glowing = color != null;

		if(entity == null) {
			glowing = false;
		}

		if(entity instanceof OfflinePlayer) {
			if(!((OfflinePlayer) entity).isOnline()) {
				glowing = false;
			}
		}

		boolean wasGlowing = dataMap.containsKey(entity != null ? entity.getUniqueId() : null);
		GlowData glowData;

		if(wasGlowing && entity != null) {
			glowData = dataMap.get(entity.getUniqueId());
		} else {
			glowData = new GlowData();
		}

		Color oldColor = wasGlowing ? glowData.colorMap.get(receiver.getUniqueId()) : null;

		if(glowing) {
			glowData.colorMap.put(receiver.getUniqueId(), color);
		} else {
			glowData.colorMap.remove(receiver.getUniqueId());
		}

		if(glowData.colorMap.isEmpty()) {
			dataMap.remove(entity != null ? entity.getUniqueId() : null);
		} else {
			if(entity != null) {
				dataMap.put(entity.getUniqueId(), glowData);
			}
		}

		if(color != null && oldColor == color) {
			return;
		}

		if(entity == null) {
			return;
		}

		if(entity instanceof OfflinePlayer) {
			if(!((OfflinePlayer) entity).isOnline()) {
				return;
			}
		}

		if(!receiver.isOnline()) {
			return;
		}

		//		sendGlowPacket(entity, wasGlowing, glowing, receiver);

		if(oldColor != null && oldColor != Color.NONE/*We never add to NONE, so no need to remove*/) {
			//			sendTeamPacket(entity, oldColor/*use the old color to remove the player from its team*/, false, false, tagVisibility, push, receiver);
		}

		if(glowing) {
			//			sendTeamPacket(entity, color, false, color != Color.NONE, tagVisibility, push, receiver);
		}

		entity.setGlowing(glowing);
	}

	/**
	 * Set the glowing-color of an entity
	 *
	 * @param entity   {@link Entity} to update
	 * @param color    {@link de.syscy.kagecore.util.GlowUtil.Color} of the glow, or <code>null</code> to stop glowing
	 * @param receiver {@link Player} that will see the update
	 */
	public static void setGlowing(Entity entity, Color color, Player receiver) {
		setGlowing(entity, color, "always", "always", receiver);
	}

	/**
	 * Set the glowing-color of an entity
	 *
	 * @param entity   {@link Entity} to update
	 * @param glowing  whether the entity is glowing or not
	 * @param receiver {@link Player} that will see the update
	 * @see #setGlowing(Entity, Color, Player)
	 */
	public static void setGlowing(Entity entity, boolean glowing, Player receiver) {
		setGlowing(entity, glowing ? Color.NONE : null, receiver);
	}

	/**
	 * Set the glowing-color of an entity
	 *
	 * @param entity    {@link Entity} to update
	 * @param glowing   whether the entity is glowing or not
	 * @param receivers Collection of {@link Player}s that will see the update
	 * @see #setGlowing(Entity, Color, Player)
	 */
	public static void setGlowing(Entity entity, boolean glowing, Collection<? extends Player> receivers) {
		for(Player receiver : receivers) {
			setGlowing(entity, glowing, receiver);
		}
	}

	/**
	 * Set the glowing-color of an entity
	 *
	 * @param entity    {@link Entity} to update
	 * @param color     {@link de.syscy.kagecore.util.GlowUtil.Color} of the glow, or <code>null</code> to stop glowing
	 * @param receivers Collection of {@link Player}s that will see the update
	 */
	public static void setGlowing(Entity entity, Color color, Collection<? extends Player> receivers) {
		for(Player receiver : receivers) {
			setGlowing(entity, color, receiver);
		}
	}

	/**
	 * Set the glowing-color of an entity
	 *
	 * @param entities Collection of {@link Entity} to update
	 * @param color    {@link de.syscy.kagecore.util.GlowUtil.Color} of the glow, or <code>null</code> to stop glowing
	 * @param receiver {@link Player} that will see the update
	 */
	public static void setGlowing(Collection<? extends Entity> entities, Color color, Player receiver) {
		for(Entity entity : entities) {
			setGlowing(entity, color, receiver);
		}
	}

	/**
	 * Set the glowing-color of an entity
	 *
	 * @param entities  Collection of {@link Entity} to update
	 * @param color     {@link de.syscy.kagecore.util.GlowUtil.Color} of the glow, or <code>null</code> to stop glowing
	 * @param receivers Collection of {@link Player}s that will see the update
	 */
	public static void setGlowing(Collection<? extends Entity> entities, Color color, Collection<? extends Player> receivers) {
		for(Entity entity : entities) {
			setGlowing(entity, color, receivers);
		}
	}

	/**
	 * Check if an entity is glowing
	 *
	 * @param entity   {@link Entity} to check
	 * @param receiver {@link Player} receiver to check (as used in the setGlowing methods)
	 * @return <code>true</code> if the entity appears glowing to the player
	 */
	public static boolean isGlowing(Entity entity, Player receiver) {
		return getGlowColor(entity, receiver) != null;
	}

	/**
	 * Checks if an entity is glowing
	 *
	 * @param entity    {@link Entity} to check
	 * @param receivers Collection of {@link Player} receivers to check
	 * @param checkAll  if <code>true</code>, this only returns <code>true</code> if the entity is glowing for all receivers; if <code>false</code> this returns <code>true</code> if the entity is glowing for any of the receivers
	 * @return <code>true</code> if the entity appears glowing to the players
	 */
	public static boolean isGlowing(Entity entity, Collection<? extends Player> receivers, boolean checkAll) {
		if(checkAll) {
			boolean glowing = true;

			for(Player receiver : receivers) {
				if(!isGlowing(entity, receiver)) {
					glowing = false;
				}
			}

			return glowing;
		} else {
			for(Player receiver : receivers) {
				if(isGlowing(entity, receiver)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Get the glow-color of an entity
	 *
	 * @param entity   {@link Entity} to get the color for
	 * @param receiver {@link Player} receiver of the color (as used in the setGlowing methods)
	 * @return the {@link de.syscy.kagecore.util.GlowUtil.Color}, or <code>null</code> if the entity doesn't appear glowing to the player
	 */
	public static Color getGlowColor(Entity entity, Player receiver) {
		if(!dataMap.containsKey(entity.getUniqueId())) {
			return null;
		}

		GlowData data = dataMap.get(entity.getUniqueId());

		return data.colorMap.get(receiver.getUniqueId());
	}

	protected static void sendGlowPacket(Entity entity, boolean wasGlowing, boolean glowing, Player receiver) {
		WrappedDataWatcher dataWatcher = WrappedDataWatcher.getEntityWatcher(entity);
		byte entityFlags = dataWatcher.getByte(0);
		byte newEntityFlags = (byte) (glowing ? (entityFlags | 1 << 6) : (entityFlags & ~(1 << 6))); //6 = glowing index
		dataWatcher.setObject(0, newEntityFlags);

		WrapperPlayServerEntityMetadata metadataPacket = new WrapperPlayServerEntityMetadata();
		metadataPacket.setEntityID(-entity.getEntityId());
		metadataPacket.setMetadata(dataWatcher.getWatchableObjects());

		sendPacket(metadataPacket.getHandle(), receiver);
		KageCore.debugMessage("send glow packet with entityFlags " + entityFlags + "");
	}

	/**
	 * Initializes the teams for a player
	 *
	 * @param receiver      {@link Player} receiver
	 * @param tagVisibility visibility of the name-tag (always, hideForOtherTeams, hideForOwnTeam, never)
	 * @param push          push behaviour (always, pushOtherTeams, pushOwnTeam, never)
	 */
	public static void initTeam(Player receiver, String tagVisibility, String push) {
		for(Color color : Color.values()) {
			GlowUtil.sendTeamPacket(null, color, true, false, tagVisibility, push, receiver);
		}
	}

	/**
	 * Initializes the teams for a player
	 *
	 * @param receiver {@link Player} receiver
	 */
	public static void initTeam(Player receiver) {
		initTeam(receiver, TEAM_TAG_VISIBILITY, TEAM_PUSH);
	}

	protected static void sendTeamPacket(Entity entity, Color color, boolean createNewTeam/*If true, we don't add any entities*/, boolean addEntity/*true->add the entity, false->remove the entity*/, String tagVisibility, String push, Player receiver) {
		WrapperPlayServerScoreboardTeam teamPacket = new WrapperPlayServerScoreboardTeam();
		teamPacket.setMode(createNewTeam ? WrapperPlayServerScoreboardTeam.Mode.TEAM_CREATED : addEntity ? WrapperPlayServerScoreboardTeam.Mode.PLAYERS_ADDED : WrapperPlayServerScoreboardTeam.Mode.PLAYERS_REMOVED);
		teamPacket.setName(color.getTeamName());
		teamPacket.setNameTagVisibility(tagVisibility);
		teamPacket.setCollisionRule(push);

		if(createNewTeam) {
			//teamPacket.setColor(color.getPacketValue()); //Color -> this is what we care about //TODO: Translate color
			teamPacket.setDisplayName(WrappedChatComponent.fromText(color.getTeamName())); //prefix - for some reason this controls the color, even though there's the extra color value...
			teamPacket.setSuffix(WrappedChatComponent.fromText(""));
			teamPacket.setPackOptionData(0); //Options - let's just ignore them for now
		}

		if(!createNewTeam) {
			//Add/remove players
			List<String> entityList = new ArrayList<>();

			if(entity instanceof OfflinePlayer) { //Players still use the name...
				entityList.add(entity.getName());
			} else {
				entityList.add(entity.getUniqueId().toString());
			}

			teamPacket.setPlayers(entityList);
		}

		sendPacket(teamPacket.getHandle(), receiver);
	}

	protected static void sendPacket(PacketContainer packet, Player player) {
		try {
			ProtocolUtil.getProtocolManager().sendServerPacket(player, packet);
		} catch(InvocationTargetException ex) {
			ex.printStackTrace();
		}
	}

	public static void initPacketListener(KageCore plugin) {
		ProtocolUtil.getProtocolManager().addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.ENTITY_METADATA) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
				WrapperPlayServerEntityMetadata metadataPacket = new WrapperPlayServerEntityMetadata(event.getPacket());

				if(metadataPacket.getEntityID() < 0) {
					metadataPacket.setEntityID(-metadataPacket.getEntityID());

					if(metadataPacket.getMetadata() == null || metadataPacket.getMetadata().isEmpty()) {
						return;
					}

					Entity entity = metadataPacket.getEntity(event);

					if(isGlowing(entity, event.getPlayer())) {
						WrappedWatchableObject watchableObject = metadataPacket.getMetadata().get(0);
						byte entityFlags = (byte) watchableObject.getValue();
						byte newEntityFlags = (byte) (entityFlags | 1 << 6); //6 = glowing index
						watchableObject.setValue(newEntityFlags);
					}
				}

				event.setPacket(metadataPacket.getHandle());
			}
		});
	}

	/**
	 * Team Colors
	 */
	@RequiredArgsConstructor
	public enum Color {
		//@-
		BLACK("0"),
		DARK_BLUE("1"),
		DARK_GREEN("2"),
		DARK_AQUA("3"),
		DARK_RED("4"),
		DARK_PURPLE("5"),
		GOLD("6"),
		GRAY("7"),
		DARK_GRAY("8"),
		BLUE("9"),
		GREEN("a"),
		AQUA("b"),
		RED("c"),
		PURPLE("d"),
		YELLOW("e"),
		WHITE("f"),
		NONE("");
		//@+

		private final @Getter String colorCode;

		public int getPacketValue() {
			return this == NONE ? -1 : ordinal();
		}

		public String getTeamName() {
			String name = "GU#" + name();

			if(name.length() > 16) {
				name = name.substring(0, 16);
			}

			return name;
		}
	}

	@EventHandler
	public void onJoin(final PlayerJoinEvent event) {
		//Initialize the teams
		GlowUtil.initTeam(event.getPlayer());
	}

	@EventHandler
	public void onQuit(final PlayerQuitEvent event) {
		for(Player receiver : Bukkit.getOnlinePlayers()) {
			if(GlowUtil.isGlowing(event.getPlayer(), receiver)) {
				GlowUtil.setGlowing(event.getPlayer(), null, receiver);
			}
		}
	}

	public static Entity getEntityById(World world, int entityId) {
		return ((CraftWorld) world).getHandle().getEntity(entityId).getBukkitEntity();
	}

	public static class GlowData {
		private @Getter Map<UUID, Color> colorMap = new HashMap<>();
	}
}