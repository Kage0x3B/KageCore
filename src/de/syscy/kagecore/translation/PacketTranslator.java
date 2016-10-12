package de.syscy.kagecore.translation;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.comphenix.packetwrapper.WrapperPlayServerEntityMetadata;
import com.comphenix.packetwrapper.WrapperPlayServerOpenWindow;
import com.comphenix.packetwrapper.WrapperPlayServerSetSlot;
import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntityLiving;
import com.comphenix.packetwrapper.WrapperPlayServerTileEntityData;
import com.comphenix.packetwrapper.WrapperPlayServerWindowItems;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.comphenix.protocol.wrappers.nbt.NbtBase;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtList;
import com.comphenix.protocol.wrappers.nbt.NbtType;

import de.syscy.kagecore.KageCore;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PacketTranslator {
	private static char packetTranslatorSign = '§';
	private static Pattern tsPattern = Pattern.compile(packetTranslatorSign + "[\\w\\d.]+(;!?[A-Za-z0-9 ]+)*;"); //Matches parts of strings like "§test;" or "§test;arg1:2;arg3;" to translate

	private final KageCore plugin;
	private final ProtocolManager protocolManager;

	public void initPacketRewriting() {
		List<PacketType> packetTypes = new ArrayList<>();
		packetTypes.add(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
		packetTypes.add(PacketType.Play.Server.ENTITY_METADATA);
		packetTypes.add(PacketType.Play.Server.TILE_ENTITY_DATA);
		packetTypes.add(PacketType.Play.Server.OPEN_WINDOW);
		packetTypes.add(PacketType.Play.Server.WINDOW_ITEMS);
		packetTypes.add(PacketType.Play.Server.SET_SLOT);

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
			if(event.getPacketType().equals(PacketType.Play.Server.SPAWN_ENTITY_LIVING)) {
				WrapperPlayServerSpawnEntityLiving wrapper = new WrapperPlayServerSpawnEntityLiving(packet);
				wrapper.setMetadata(translateDataWatcher(wrapper.getMetadata(), player));
			} else if(event.getPacketType().equals(PacketType.Play.Server.ENTITY_METADATA)) {
				WrapperPlayServerEntityMetadata wrapper = new WrapperPlayServerEntityMetadata(packet);
				List<WrappedWatchableObject> metadata = new ArrayList<>();

				for(WrappedWatchableObject watchableObject : wrapper.getEntityMetadata()) {
					metadata.add(translateWatchableObject(watchableObject, player));
				}

				wrapper.setEntityMetadata(metadata);
			} else if(event.getPacketType().equals(PacketType.Play.Server.TILE_ENTITY_DATA)) {
				WrapperPlayServerTileEntityData wrapper = new WrapperPlayServerTileEntityData(packet);

				if(wrapper.getNbtData() != null) {
					wrapper.setNbtData(translateNBT(wrapper.getNbtData(), player));
				}
			} else if(event.getPacketType().equals(PacketType.Play.Server.OPEN_WINDOW)) {
				WrapperPlayServerOpenWindow wrapper = new WrapperPlayServerOpenWindow(packet);
				wrapper.setWindowTitle(tryTranslateString(wrapper.getWindowTitle(), player));
			} else if(event.getPacketType().equals(PacketType.Play.Server.WINDOW_ITEMS)) {
				WrapperPlayServerWindowItems wrapper = new WrapperPlayServerWindowItems(packet);

				ItemStack[] newItemStacks = wrapper.getItems();

				for(int i = 0; i < newItemStacks.length; i++) {
					newItemStacks[i] = translateItemStack(newItemStacks[i], player);
				}

				wrapper.setItems(newItemStacks);
			} else if(event.getPacketType().equals(PacketType.Play.Server.SET_SLOT)) {
				WrapperPlayServerSetSlot wrapper = new WrapperPlayServerSetSlot(packet);
				wrapper.setSlotData(translateItemStack(wrapper.getSlotData(), player));
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
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
			String[] parts = string.split(";");
			Object[] args = new Object[parts.length - 1];

			for(int i = 1; i < parts.length; i++) {
				String part = parts[i];
				
				if(part.startsWith("!")) {
					part = part.substring(1);
					
					char partType = part.charAt(0);
					part = part.substring(1);
					
					switch(partType) {
						case 'i':
							int intArg = Integer.parseInt(part);
							args[i - 1] = intArg;
							break;
						case 'd':
							double doubleArg = Double.parseDouble(part);
							args[i - 1] = doubleArg;
							break;
					}
				} else {
					args[i - 1] = parts[i];
				}
			}

			stringBuilder.append(Translator.translate(language, parts[0], args));
		}
	}

	private static ItemStack translateItemStack(ItemStack itemStack, Player player) {
		if(itemStack == null || !itemStack.hasItemMeta()) {
			return itemStack;
		}

		ItemMeta itemMeta = itemStack.getItemMeta();

		if(itemMeta.hasDisplayName()) {
			itemMeta.setDisplayName(tryTranslateString(itemMeta.getDisplayName(), player));
		}

		if(itemMeta.hasLore()) {
			List<String> newLore = new ArrayList<>();

			for(String loreString : itemMeta.getLore()) {
				newLore.add(tryTranslateString(loreString, player));
			}

			itemMeta.setLore(newLore);
		}

		itemStack.setItemMeta(itemMeta);

		return itemStack;
	}

	private static WrappedWatchableObject translateWatchableObject(WrappedWatchableObject watchableObject, Player player) {
		watchableObject.setValue(tryTranslateObject(watchableObject.getValue(), player));

		return watchableObject;
	}

	private static WrappedDataWatcher translateDataWatcher(WrappedDataWatcher dataWatcher, Player player) {
		for(WrappedWatchableObject watchableObject : dataWatcher) {
			watchableObject.setValue(tryTranslateObject(watchableObject.getValue(), player));
		}

		return dataWatcher;
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