package de.syscy.kagecore.util.bungee;

import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import de.syscy.kagecore.KageCore;
import lombok.AccessLevel;
import lombok.Getter;

public class BungeeUtil {
	private static @Getter(value=AccessLevel.PROTECTED) ArrayListMultimap<String, ResponseListener<?>> responseListeners = ArrayListMultimap.create();
	
	/**
	 * Registers the listener for communicating with other servers in the network via Forward/ForwardToPlayer
	 * @param subChannel
	 * @param bungeeMessageListener
	 */
	public static void registerBungeeMessageListener(String subChannel, BungeeMessageListener bungeeMessageListener) {
		BungeePluginMessageListener.getBungeeMessageListeners().put(subChannel.toLowerCase(), bungeeMessageListener);
	}
	
	/**
	 * Registers the listener for communicating with the BungeeCord server
	 * @param subChannel
	 * @param kageMessageListener
	 */
	public static void registerKageMessageListener(String subChannel, KageMessageListener kageMessageListener) {
		KagePluginMessageListener.getKageMessageListeners().put(subChannel.toLowerCase(), kageMessageListener);
	}

	public static void sendPlayerToServer(Player player, String serverName) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF(serverName);
		
		sendPluginMessage("BungeeCord", out, player);
	}

	public static void sendPlayerToServer(String playerName, String serverName) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("ConnectOther");
		out.writeUTF(playerName);
		out.writeUTF(serverName);
		
		sendPluginMessage("BungeeCord", out);
	}

	public static void getRealIP(Player player, final ResponseListener<InetSocketAddress> listener) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("IP");

		listener.setPlayer(player);
		listener.setPlayerSpecific(true);
		listener.setResponseHandler(new ResponseHandler() {
			public void handleResponse(ByteArrayDataInput in) {
				String ip = in.readUTF();
				int port = in.readInt();

				listener.onResponse(new InetSocketAddress(ip, port));
			}
		});
		responseListeners.put("IP", listener);
		
		sendPluginMessage("BungeeCord", out, player);
	}

	public static void getPlayerCount(String serverName, final ResponseListener<Integer> listener) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("PlayerCount");
		out.writeUTF(serverName);

		listener.setResponseHandler(new ResponseHandler() {
			public void handleResponse(ByteArrayDataInput in) {
				in.readUTF(); //Reads server name
				int playerCount = in.readInt();

				listener.onResponse(playerCount);
			}
		});
		responseListeners.put("PlayerCount", listener);
		
		sendPluginMessage("BungeeCord", out);
	}

	public static void getPlayerList(String serverName, final ResponseListener<String[]> listener) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("PlayerList");
		out.writeUTF(serverName);

		listener.setResponseHandler(new ResponseHandler() {
			public void handleResponse(ByteArrayDataInput in) {
				in.readUTF(); //Reads server name
				String[] playerList = in.readUTF().split(", ");

				listener.onResponse(playerList);
			}
		});
		responseListeners.put("PlayerList", listener);
		
		sendPluginMessage("BungeeCord", out);
	}

	public static void getServers(final ResponseListener<String[]> listener) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("GetServers");

		listener.setResponseHandler(new ResponseHandler() {
			public void handleResponse(ByteArrayDataInput in) {
				String[] servers = in.readUTF().split(", ");

				listener.onResponse(servers);
			}
		});
		responseListeners.put("GetServers", listener);
		
		sendPluginMessage("BungeeCord", out);
	}

	public static void sendMessage(String playerName, String message) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Message");
		out.writeUTF(playerName);
		out.writeUTF(message);
		
		sendPluginMessage("BungeeCord", out);
	}

	public static void getCurrentServerName(final ResponseListener<String> listener) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("GetServer");

		listener.setResponseHandler(new ResponseHandler() {
			public void handleResponse(ByteArrayDataInput in) {
				String serverName = in.readUTF();

				listener.onResponse(serverName);
			}
		});
		responseListeners.put("GetServer", listener);
		
		sendPluginMessage("BungeeCord", out);
	}

	/**
	 * Forwards a message to all other servers on the network, a specific server or all servers which are online
	 * @param receiver ALL, ONLINE or a server name
	 * @param subChannel A subchannel for this message
	 * @param data The data
	 */
	public static void forwardServerMessage(String receiver, String subChannel, ByteArrayOutputStream data) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Forward");
		out.writeUTF(receiver);
		out.writeUTF(subChannel.toLowerCase());

		byte[] dataByteArray = data.toByteArray();
		out.writeShort(dataByteArray.length);
		out.write(dataByteArray);
		
		sendPluginMessage("BungeeCord", out);
	}
	
	/**
	 * Forwards a message to the server the specified player plays on.
	 * @param playerName A player name
	 * @param subChannel A subchannel for this message
	 * @param data The data
	 */
	public static void forwardServerMessageThroughPlayer(String playerName, String subChannel, ByteArrayOutputStream data) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("ForwardToPlayer");
		out.writeUTF(playerName);
		out.writeUTF(subChannel.toLowerCase());

		byte[] dataByteArray = data.toByteArray();
		out.writeShort(dataByteArray.length);
		out.write(dataByteArray);
		
		sendPluginMessage("BungeeCord", out);
	}

	/**
	 * Gets the UUID of the specified player. But using {@link Player#getUniqueId()} should give the same result when the IP/UUID is forwarded.
	 * @param player
	 * @param listener
	 */
	public static void getUUID(Player player, final ResponseListener<UUID> listener) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("UUID");

		listener.setResponseHandler(new ResponseHandler() {
			public void handleResponse(ByteArrayDataInput in) {
				String uuid = in.readUTF();

				listener.onResponse(UUID.fromString(uuid));
			}
		});
		responseListeners.put("UUID", listener);
		
		sendPluginMessage("BungeeCord", out, player);
	}

	/**
	 * Gets the UUID of any player on the server network
	 * @param playerName
	 * @param listener
	 */
	public static void getUUID(String playerName, final ResponseListener<UUID> listener) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("UUIDOther");
		out.writeUTF(playerName);

		listener.setResponseHandler(new ResponseHandler() {
			public void handleResponse(ByteArrayDataInput in) {
				in.readUTF(); //Read player name
				String uuid = in.readUTF();

				listener.onResponse(UUID.fromString(uuid));
			}
		});
		responseListeners.put("UUIDOther", listener);
		
		sendPluginMessage("BungeeCord", out);
	}

	/**
	 * Gets the address of a server in the network
	 * @param serverName
	 * @param listener
	 */
	public static void getServerIP(String serverName, final ResponseListener<InetSocketAddress> listener) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("ServerIP");

		listener.setResponseHandler(new ResponseHandler() {
			public void handleResponse(ByteArrayDataInput in) {
				in.readUTF(); //Read server name
				String ip = in.readUTF();
				short port = in.readShort();

				listener.onResponse(new InetSocketAddress(ip, port));
			}
		});
		responseListeners.put("ServerIP", listener);
		
		sendPluginMessage("BungeeCord", out);
	}

	/**
	 * Kicks a player from the server network
	 * @param playerName
	 * @param reason
	 */
	public static void kickPlayer(String playerName, String reason) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("KickPlayer");
		out.writeUTF(playerName);
		out.writeUTF(reason);
		
		sendPluginMessage("BungeeCord", out);
	}
	
	public static void sendMessageToBungeeCord(String subChannel, ByteArrayDataOutput out) {
		ByteArrayDataOutput finalOut = ByteStreams.newDataOutput();
		finalOut.writeUTF(subChannel);
		finalOut.write(out.toByteArray());
		
		sendPluginMessage("KageCore", finalOut);
	}

	public static void sendPluginMessage(String channel, ByteArrayDataOutput out) {
		sendPluginMessage(channel, out, null);
	}
	
	public static void sendPluginMessage(String channel, ByteArrayDataOutput out, Player player) {
		if(player == null) {
			player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
		}
		
		if(player != null) {
			player.sendPluginMessage(KageCore.getInstance(), channel, out.toByteArray());
		}
	}
}