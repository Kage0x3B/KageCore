package de.syscy.kagecore.translation;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.comphenix.protocol.wrappers.nbt.NbtBase;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtList;
import com.comphenix.protocol.wrappers.nbt.NbtType;

import de.syscy.kagecore.KageCore;
import de.syscy.kagecore.util.Util;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PacketTranslator {
	private static char packetTranslatorSign = '%';
	private static Pattern tsPattern = Pattern.compile(packetTranslatorSign + "[\\w\\d.]+(:[A-Za-z0-9 ]+)*:"); //Matches parts of strings like "%test:" or "%test:arg1:2:arg3:" to translate

	private final KageCore plugin;
	private final ProtocolManager protocolManager;

	public void initPacketRewriting() {
		List<PacketType> packetTypes = new ArrayList<>();
		packetTypes.add(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
		packetTypes.add(PacketType.Play.Server.ENTITY_METADATA);
		packetTypes.add(PacketType.Play.Server.TILE_ENTITY_DATA);
		packetTypes.add(PacketType.Play.Server.BOSS);
		packetTypes.add(PacketType.Play.Server.CHAT);

		protocolManager.addPacketListener(new PacketAdapter(plugin, packetTypes) {
			public void onPacketSending(PacketEvent event) {
				handlePacket(event);
			}
		});
	}

	private void handlePacket(PacketEvent event) {
		PacketContainer packet = event.getPacket();
		Player player = event.getPlayer();

		try {
			handleStructureModifier(packet.getStrings(), StringStructureHandler.instance, player);
			handleStructureModifierArray(packet.getStringArrays(), StringStructureHandler.instance, player);
			handleStructureModifier(packet.getNbtModifier(), NBTStructureHandler.instance, player);
			handleStructureModifierList(packet.getListNbtModifier(), NBTStructureHandler.instance, player);
			handleStructureModifier(packet.getChatComponents(), ChatComponentStructureHandler.instance, player);
//			handleStructureModifierArray(packet.getChatComponentArrays(), ChatComponentStructureHandler.instance, player);
			handleStructureModifier(packet.getDataWatcherModifier(), DataWatcherStructureHandler.instance, player);
			handleStructureModifierList(packet.getWatchableCollectionModifier(), WatchableObjectStructureHandler.instance, player);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	private <T> void handleStructureModifierArray(StructureModifier<T[]> structureModifierList, StructureHandler<T> structureHandler, Player player) {
		for(int i = 0; i < structureModifierList.size(); i++) {
			T[] objectArray = structureModifierList.read(i);
			
			for(int j = 0; j < objectArray.length; j++) {
				objectArray[j] = structureHandler.handleStructure(objectArray[j], player);
			}
			
			structureModifierList.write(i, objectArray);
		}
	}

	private <T> void handleStructureModifierList(StructureModifier<List<T>> structureModifierList, StructureHandler<T> structureHandler, Player player) {
		for(int i = 0; i < structureModifierList.size(); i++) {
			List<T> objectList = structureModifierList.read(i);
			
			for(int j = 0; j < objectList.size(); j++) {
				objectList.set(j, structureHandler.handleStructure(objectList.get(j), player));
			}
			
			structureModifierList.write(i, objectList);
		}
	}

	private <T> void handleStructureModifier(StructureModifier<T> structureModifier, StructureHandler<T> structureHandler, Player player) {
		for(int i = 0; i < structureModifier.size(); i++) {
			structureModifier.write(i, structureHandler.handleStructure(structureModifier.read(i), player));
		}
	}

	private interface StructureHandler<T> {
		public T handleStructure(T object, Player player);
	}

	private static Object tryTranslateObject(Object object, Player player) {
		return object instanceof String ? tryTranslateString((String) object, player) : object;
	}

	private static String tryTranslateString(String string, Player player) {
		if(string.indexOf(packetTranslatorSign) < 0) {
			return string;
		}

		String language = Translator.getLanguage(player);
		TranslateString[] parts = parse(string);

		StringBuilder result = new StringBuilder();

		for(TranslateString part : parts) {
			part.append(result, language);
		}

		return result.toString();
	}

	private static TranslateString[] parse(String string) {
		ArrayList<TranslateString> translateStrings = new ArrayList<>();
		Matcher matcher = tsPattern.matcher(string);

		for(int i = 0, len = string.length(); i < len;) {
			if(matcher.find(i)) {
				if(matcher.start() != i) {
					translateStrings.add(new FixedString(string.substring(i, matcher.start())));
				}

				translateStrings.add(new TranslatableString(string.substring(matcher.start(), matcher.end())));
				i = matcher.end();
			} else {
				translateStrings.add(new FixedString(string.substring(i)));

				break;
			}
		}

		return translateStrings.toArray(new TranslateString[translateStrings.size()]);
	}

	private interface TranslateString {
		public void append(StringBuilder stringBuilder, String language);
	}

	@AllArgsConstructor
	private static class FixedString implements TranslateString {
		private String string;

		public void append(StringBuilder stringBuilder, String language) {
			stringBuilder.append(string);
		}
	}

	@AllArgsConstructor
	private static class TranslatableString implements TranslateString {
		private String string;

		public void append(StringBuilder stringBuilder, String language) {
			string = string.substring(1, string.length() - 1);
			String[] parts = string.split(":");
			Object[] args = new Object[parts.length - 1];

			for(int i = 1; i < parts.length; i++) {
				if(Util.isNumber(parts[i])) {
					args[i - 1] = Float.parseFloat(parts[i]);
				} else {
					args[i - 1] = parts[i];
				}
			}

			stringBuilder.append(Translator.translate(language, parts[0], args));
		}
	}
	
	private static class StringStructureHandler implements StructureHandler<String> {
		private static StructureHandler<String> instance = new StringStructureHandler();

		@Override
		public String handleStructure(String string, Player player) {
			return tryTranslateString(string, player);
		}
	}

	private static class ChatComponentStructureHandler implements StructureHandler<WrappedChatComponent> {
		private static StructureHandler<WrappedChatComponent> instance = new ChatComponentStructureHandler();

		@Override
		public WrappedChatComponent handleStructure(WrappedChatComponent chatComponent, Player player) {
			return WrappedChatComponent.fromJson(tryTranslateString(chatComponent.getJson(), player));
		}
	}
	
	private static class WatchableObjectStructureHandler implements StructureHandler<WrappedWatchableObject> {
		private static StructureHandler<WrappedWatchableObject> instance = new WatchableObjectStructureHandler();

		@Override
		public WrappedWatchableObject handleStructure(WrappedWatchableObject watchableObject, Player player) {
			watchableObject.setValue(tryTranslateObject(watchableObject.getValue(), player));

			return watchableObject;
		}
	}
	
	private static class DataWatcherStructureHandler implements StructureHandler<WrappedDataWatcher> {
		private static StructureHandler<WrappedDataWatcher> instance = new DataWatcherStructureHandler();

		@Override
		public WrappedDataWatcher handleStructure(WrappedDataWatcher dataWatcher, Player player) {
			for(WrappedWatchableObject watchableObject : dataWatcher) {
				watchableObject.setValue(tryTranslateObject(watchableObject.getValue(), player));
			}

			return dataWatcher;
		}
	}
	
	private static class NBTStructureHandler implements StructureHandler<NbtBase<?>> {
		private static StructureHandler<NbtBase<?>> instance = new NBTStructureHandler();

		@Override
		public NbtBase<?> handleStructure(NbtBase<?> nbt, Player player) {
			nbt = translateNBT(nbt, player);

			return nbt;
		}
	}

	@SuppressWarnings("unchecked")
	public static NbtBase<?> translateNBT(NbtBase<?> nbt, Player player) {
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
}