package de.syscy.kagecore.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.comphenix.packetwrapper.WrapperPlayServerEntityMetadata;
import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntityLiving;
import com.comphenix.packetwrapper.WrapperPlayServerTileEntityData;
import com.comphenix.packetwrapper.WrapperPlayServerWorldEvent;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.comphenix.protocol.wrappers.nbt.NbtBase;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtList;
import com.comphenix.protocol.wrappers.nbt.NbtType;

import de.syscy.kagecore.KageCore;
import de.syscy.kagecore.translation.Translator;

@SuppressWarnings("unused")
public class TranslatorUtil {
	private static String packetTranslatorSign;

	public static void initPacketRewriting(KageCore plugin, ProtocolManager protocolManager) {
		packetTranslatorSign = plugin.getKageCoreConfig().getPacketTranslatorSign();

		if(packetTranslatorSign == null || packetTranslatorSign.isEmpty()) {
			return;
		}

		List<PacketType> packetTypes = new ArrayList<>();
		packetTypes.add(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
		packetTypes.add(PacketType.Play.Server.ENTITY_METADATA);
		packetTypes.add(PacketType.Play.Server.TILE_ENTITY_DATA);

		protocolManager.addPacketListener(new PacketAdapter(plugin, packetTypes) {
			@Override
			public void onPacketSending(PacketEvent event) {
				handlePacket(event);
			}
		});
	}

	private static void handlePacket(PacketEvent event) {
		if(event.getPacketType().equals(PacketType.Play.Server.SPAWN_ENTITY_LIVING)) {
			handleSpawnEntityLiving(event);
		} else if(event.getPacketType().equals(PacketType.Play.Server.ENTITY_METADATA)) {
			handleEntityMetadata(event);
		} else if(event.getPacketType().equals(PacketType.Play.Server.TILE_ENTITY_DATA)) {
			handleTileEntityData(event);
		}
	}

	private static Object tryTranslateObject(Object object, Player player) {
		return object instanceof String ? tryTranslateString((String) object, player) : object;
	}

	private static String tryTranslateString(String string, Player player) {
		if(string.startsWith(packetTranslatorSign)) {
			KageCore.debugMessage("translating " + string + " to " + Translator.translate(player, string.substring(1)));
		}
		
		return string.startsWith(packetTranslatorSign) ? Translator.translate(player, string.substring(1)) : string;
	}

	private static List<WrappedWatchableObject> translateWatchableObjects(List<WrappedWatchableObject> watchableObjects, Player player) {
		for(WrappedWatchableObject watchableObject : watchableObjects) {
			watchableObject.setValue(tryTranslateObject(watchableObject.getValue(), player));
		}
		
		return watchableObjects;
	}
	
	private static WrappedDataWatcher translateDataWatcher(WrappedDataWatcher dataWatcher, Player player) {
		for(WrappedWatchableObject watchableObject : dataWatcher) {
			watchableObject.setValue(tryTranslateObject(watchableObject.getValue(), player));
		}
		
		return dataWatcher;
	}

	@SuppressWarnings("unchecked")
	private static NbtBase<?> translateNBT(NbtBase<?> nbt, Player player) {
		if(nbt.getType().equals(NbtType.TAG_STRING)) {
			return translateNBTString((NbtBase<String>) nbt, player);
		} else if(nbt.getType().equals(NbtType.TAG_COMPOUND)) {
			return translateNBTCompound((NbtCompound) nbt, player);
		} else if(nbt.getType().equals(NbtType.TAG_LIST)) {
			NbtList<?> nbtList = (NbtList<?>) nbt;
			
			if(nbtList.getElementType() == NbtType.TAG_STRING) {
				return translateNBTList((NbtList<String>) nbtList, player);
			}
		}

		return nbt;
	}
	
	private static NbtBase<String> translateNBTString(NbtBase<String> nbtString, Player player) {
		nbtString.setValue(tryTranslateString(nbtString.getValue(), player));
		
		return nbtString;
	}
	
	private static NbtCompound translateNBTCompound(NbtCompound nbtCompound, Player player) {
		for(String key : nbtCompound.getKeys()) {
			nbtCompound.put(key, translateNBT(nbtCompound.getValue(key), player));
		}
		
		return nbtCompound;
	}
	
	private static NbtList<String> translateNBTList(NbtList<String> nbtList, Player player) {
		List<NbtBase<String>> newList = new ArrayList<>();
		
		for(NbtBase<String> nbtString : nbtList.getValue()) {
			newList.add(translateNBTString(nbtString, player));
		}
		
		nbtList.setValue(newList);
		
		return nbtList;
	}

	private static void handleSpawnEntityLiving(PacketEvent event) {
		WrapperPlayServerSpawnEntityLiving wrapper = new WrapperPlayServerSpawnEntityLiving(event.getPacket());
		wrapper.setMetadata(translateDataWatcher(wrapper.getMetadata(), event.getPlayer()));
	}
	
	private static void handleEntityMetadata(PacketEvent event) {
		WrapperPlayServerEntityMetadata wrapper = new WrapperPlayServerEntityMetadata(event.getPacket());
		wrapper.setEntityMetadata(translateWatchableObjects(wrapper.getEntityMetadata(), event.getPlayer()));
	}

	private static void handleTileEntityData(PacketEvent event) {
		WrapperPlayServerTileEntityData wrapper = new WrapperPlayServerTileEntityData(event.getPacket());

		if(wrapper.getAction() == 9) { //Sign text changed
			wrapper.setNbtData(translateNBT(wrapper.getNbtData(), event.getPlayer()));
		}
	}
}