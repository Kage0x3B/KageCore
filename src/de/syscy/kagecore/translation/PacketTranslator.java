package de.syscy.kagecore.translation;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import com.comphenix.packetwrapper.WrapperPlayClientSettings;
import com.comphenix.packetwrapper.WrapperPlayServerCustomPayload;
import com.comphenix.packetwrapper.WrapperPlayServerEntityMetadata;
import com.comphenix.packetwrapper.WrapperPlayServerOpenWindow;
import com.comphenix.packetwrapper.WrapperPlayServerSetSlot;
import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntityLiving;
import com.comphenix.packetwrapper.WrapperPlayServerTileEntityData;
import com.comphenix.packetwrapper.WrapperPlayServerWindowItems;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.comphenix.protocol.wrappers.nbt.NbtBase;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtList;
import com.comphenix.protocol.wrappers.nbt.NbtType;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import de.syscy.kagecore.KageCore;
import de.syscy.kagecore.event.LanguageChangeEvent;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PacketTranslator {
	private static Pattern tsPattern = Pattern.compile(Translator.SIGN + "[\\w\\d.]+(;!?[A-Za-z0-9 ]+)*;"); //Matches parts of strings like "§test;" or "§test;arg1:2;arg3;" to translate
	private static List<Character> partTypeIdentifiers = Arrays.asList('i', 'd', 'l', 'f');

	public static void initPacketRewriting(KageCore plugin) {
		List<PacketType> packetTypes = new ArrayList<>();
		packetTypes.add(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
		packetTypes.add(PacketType.Play.Server.ENTITY_METADATA);
		packetTypes.add(PacketType.Play.Server.TILE_ENTITY_DATA);
		packetTypes.add(PacketType.Play.Server.OPEN_WINDOW);
		packetTypes.add(PacketType.Play.Server.WINDOW_ITEMS);
		packetTypes.add(PacketType.Play.Server.SET_SLOT);
		packetTypes.add(PacketType.Play.Server.CUSTOM_PAYLOAD);

		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

		protocolManager.addPacketListener(new PacketAdapter(plugin, PacketType.Play.Client.SETTINGS) {
			@Override
			public void onPacketReceiving(final PacketEvent event) {
				Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
					@Override
					public void run() {
						WrapperPlayClientSettings wrapper = new WrapperPlayClientSettings(event.getPacket());

						String language = wrapper.getLocale().substring(0, 2);
						String lastLanguage = Translator.getPlayerLanguages().get(event.getPlayer());

						if(lastLanguage == null || lastLanguage.isEmpty()) {
							lastLanguage = Translator.getDefaultLocale();
						}

						Translator.getPlayerLanguages().put(event.getPlayer(), language);

						Bukkit.getPluginManager().callEvent(new LanguageChangeEvent(event.getPlayer(), language, lastLanguage));
					}
				}, 0);
			}
		});

		protocolManager.addPacketListener(new PacketAdapter(plugin, packetTypes) {
			@Override
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

				for(WrappedWatchableObject watchableObject : wrapper.getMetadata()) {
					metadata.add(translateWatchableObject(watchableObject, player));
				}

				wrapper.setMetadata(metadata);
			} else if(event.getPacketType().equals(PacketType.Play.Server.TILE_ENTITY_DATA)) {
				WrapperPlayServerTileEntityData wrapper = new WrapperPlayServerTileEntityData(packet);

				if(wrapper.getNbtData() != null) {
					wrapper.setNbtData(translateNBT(wrapper.getNbtData(), player));
				}
			} else if(event.getPacketType().equals(PacketType.Play.Server.OPEN_WINDOW)) {
				WrapperPlayServerOpenWindow wrapper = new WrapperPlayServerOpenWindow(packet);
				wrapper.setWindowTitle(translateChatComponent(wrapper.getWindowTitle(), player));
			} else if(event.getPacketType().equals(PacketType.Play.Server.WINDOW_ITEMS)) {
				WrapperPlayServerWindowItems wrapper = new WrapperPlayServerWindowItems(packet);

				List<ItemStack> newItemStacks = new ArrayList<>();

				for(ItemStack itemStack : wrapper.getSlotData()) {
					newItemStacks.add(translateItemStack(itemStack, player));
				}

				wrapper.setSlotData(newItemStacks);
			} else if(event.getPacketType().equals(PacketType.Play.Server.SET_SLOT)) {
				WrapperPlayServerSetSlot wrapper = new WrapperPlayServerSetSlot(packet);
				wrapper.setSlotData(translateItemStack(wrapper.getSlotData(), player));
			} else if(event.getPacketType().equals(PacketType.Play.Server.CUSTOM_PAYLOAD)) {
				handlePluginMessage(new WrapperPlayServerCustomPayload(packet), player);
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	private void handlePluginMessage(WrapperPlayServerCustomPayload wrapper, Player player) {
		if(wrapper.getChannel().equals("MC|TrList")) {
			ByteBuf in = wrapper.getContentsBuffer();
			ByteArrayDataOutput out = ByteStreams.newDataOutput();

			out.writeInt(in.readInt()); //Window ID
			int size = in.readByte();
			out.writeByte(size);

			for(int i = 0; i < size; i++) {
				ItemStack inputItem1 = NetworkUtil.readItemStack(in);
				NetworkUtil.writeItemStack(out, translateItemStack(inputItem1, player));
				ItemStack outputItem = NetworkUtil.readItemStack(in);
				NetworkUtil.writeItemStack(out, translateItemStack(outputItem, player));
				boolean hasSecondItem = in.readBoolean();
				out.writeBoolean(hasSecondItem);

				if(hasSecondItem) {
					ItemStack inputItem2 = NetworkUtil.readItemStack(in);
					NetworkUtil.writeItemStack(out, translateItemStack(inputItem2, player));
				}

				out.writeBoolean(in.readBoolean()); //Trade disabled boolean
				out.writeInt(in.readInt()); //Uses int
				out.writeInt(in.readInt()); //Max uses int
			}

			wrapper.setContents(out.toByteArray());
		}
	}

	private static Object tryTranslateObject(Object object, Player player) {
		return object instanceof String ? tryTranslateString((String) object, player) : object;
	}

	private static String tryTranslateString(String string, Player player) {
		if(string == null || string.indexOf(Translator.SIGN) < 0) {
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
		private final String string;

		@Override
		public void append(StringBuilder stringBuilder, String language) {
			stringBuilder.append(string);
		}
	}

	@AllArgsConstructor
	private static class TranslatableString implements TranslateString {
		private String string;

		@Override
		public void append(StringBuilder stringBuilder, String language) {
			string = string.substring(1, string.length() - 1);
			String[] parts = string.split(";");
			Object[] args = new Object[parts.length - 1];

			for(int i = 1; i < parts.length; i++) {
				if(parts[i] == null || parts[i].isEmpty()) {
					continue;
				}

				String part = parts[i].toLowerCase();
				char partType = part.charAt(part.length() - 1);

				if(partTypeIdentifiers.contains(partType)) {
					try {
						part = part.substring(0, part.length() - 1);

						NumberFormat formatter = NumberFormat.getInstance();
						ParsePosition pos = new ParsePosition(0);
						Number number = formatter.parse(part, pos);

						if(part.length() == pos.getIndex()) {
							switch(partType) {
								case 'i':
									args[i - 1] = number.intValue();
									break;
								case 'd':
									args[i - 1] = number.doubleValue();
									break;
								case 'l':
									args[i - 1] = number.longValue();
									break;
								case 'f':
									args[i - 1] = number.floatValue();
									break;
							}
						}
					} catch(Exception ex) {
						ex.printStackTrace();

						args[i - 1] = parts[i];
					}
				} else {
					args[i - 1] = parts[i];
				}
			}

			stringBuilder.append(Translator.translate(language, parts[0], args));
		}
	}

	private static WrappedChatComponent translateChatComponent(WrappedChatComponent chatComponent, Player player) {
		return WrappedChatComponent.fromJson(tryTranslateString(chatComponent.getJson(), player));
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

		if(itemStack.getType().equals(Material.WRITTEN_BOOK)) {
			BookMeta bookMeta = (BookMeta) itemMeta;

			bookMeta.setTitle(tryTranslateString(bookMeta.getTitle(), player));
			bookMeta.setAuthor(tryTranslateString(bookMeta.getAuthor(), player));
			bookMeta.setPages(translateList(bookMeta.getPages(), player));
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

	private static List<String> translateList(List<String> list, Player player) {
		if(list.size() <= 0) {
			return list;
		}

		List<String> newList = new ArrayList<>(list.size());

		for(String string : list) {
			newList.add(tryTranslateString(string, player));
		}

		return newList;
	}
}