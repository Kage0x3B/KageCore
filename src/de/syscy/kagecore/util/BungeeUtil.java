package de.syscy.kagecore.util;

import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import de.syscy.kagecore.KageCore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class BungeeUtil {
	private static ArrayListMultimap<String, ResponseListener<?>> responseListeners = ArrayListMultimap.create();

	public static void sendPlayerToServer(Player player, String serverName) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF(serverName);

		player.sendPluginMessage(KageCore.getInstance(), "BungeeCord", out.toByteArray());
	}

	public static void sendPlayerToServer(String playerName, String serverName) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("ConnectOther");
		out.writeUTF(playerName);
		out.writeUTF(serverName);

		Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);

		if(player != null) {
			player.sendPluginMessage(KageCore.getInstance(), "BungeeCord", out.toByteArray());
		}
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

		player.sendPluginMessage(KageCore.getInstance(), "BungeeCord", out.toByteArray());
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

		Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);

		if(player != null) {
			player.sendPluginMessage(KageCore.getInstance(), "BungeeCord", out.toByteArray());
		}
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

		Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);

		if(player != null) {
			player.sendPluginMessage(KageCore.getInstance(), "BungeeCord", out.toByteArray());
		}
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

		Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);

		if(player != null) {
			player.sendPluginMessage(KageCore.getInstance(), "BungeeCord", out.toByteArray());
		}
	}
	
	public static void sendMessage(String playerName, String message) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Message");
		out.writeUTF(playerName);
		out.writeUTF(message);

		Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);

		if(player != null) {
			player.sendPluginMessage(KageCore.getInstance(), "BungeeCord", out.toByteArray());
		}
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

		Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);

		if(player != null) {
			player.sendPluginMessage(KageCore.getInstance(), "BungeeCord", out.toByteArray());
		}
	}

	public static void sendServerMessage(String receiver, String channel, ByteArrayOutputStream data) {
		sendServerMessage(receiver, channel, data, null);
	}
	
	public static void sendServerMessage(String receiver, String subChannel, ByteArrayOutputStream data, final ResponseListener<String> listener) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Forward");
		out.writeUTF(receiver);
		out.writeUTF(subChannel);
		
		byte[] dataByteArray = data.toByteArray();
		out.writeShort(dataByteArray.length);
		out.write(dataByteArray);
		
		if(listener != null) {
			listener.setResponseHandler(new ResponseHandler() {
				public void handleResponse(ByteArrayDataInput in) {
					String serverName = in.readUTF();

					listener.onResponse(serverName);
				}
			});
			listener.setSubChannel(subChannel);
			responseListeners.put("Forward", listener);
		}

		Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);

		if(player != null) {
			player.sendPluginMessage(KageCore.getInstance(), "BungeeCord", out.toByteArray());
		}
	}

	public static abstract class ResponseListener<T> {
		private @Getter(value = AccessLevel.PRIVATE) @Setter(value = AccessLevel.PRIVATE) Player player;
		private @Getter(value = AccessLevel.PRIVATE) @Setter(value = AccessLevel.PRIVATE) boolean playerSpecific = false;
		private @Getter(value = AccessLevel.PRIVATE) @Setter(value = AccessLevel.PRIVATE) String subChannel = "";
		private @Getter(value = AccessLevel.PRIVATE) @Setter(value = AccessLevel.PRIVATE) ResponseHandler responseHandler;


		/**
		 * @return if the ResponseListener handled the right response and can be removed
		 */
		public abstract boolean onResponse(T response);
	}

	private static interface ResponseHandler {
		/**
		 * @return if the ResponseHandler handled the right response and can be removed
		 */
		public void handleResponse(ByteArrayDataInput in);
	}

	public static class BungeePluginMessageListener implements PluginMessageListener {
		@Override
		public void onPluginMessageReceived(String mainChannel, Player player, byte[] data) {
			ByteArrayDataInput in = ByteStreams.newDataInput(data);

			String bungeeCordChannel = in.readUTF();
			String bungeeCordSubChannel = "";
			
			if(bungeeCordChannel.equalsIgnoreCase("Forward")) {
				bungeeCordSubChannel = in.readUTF();
			}

			List<ResponseListener<?>> allListeners = responseListeners.get(bungeeCordChannel);

			ResponseListener<?> finalResponseListener = null;

			for(ResponseListener<?> responseListener : allListeners) {
				if(bungeeCordSubChannel.equals(responseListener.getSubChannel()) && (!responseListener.isPlayerSpecific() || responseListener.getPlayer().equals(player))) {
					finalResponseListener = responseListener;

					break;
				}
			}

			if(finalResponseListener != null) {
				finalResponseListener.getResponseHandler().handleResponse(in);
				responseListeners.remove(bungeeCordChannel, finalResponseListener);
			}
		}
	}
}