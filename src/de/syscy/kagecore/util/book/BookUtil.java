package de.syscy.kagecore.util.book;

import java.io.File;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta.Generation;

import de.syscy.kagecore.KageCore;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.minecraft.server.v1_11_R1.EntityPlayer;
import net.minecraft.server.v1_11_R1.EnumHand;

@UtilityClass
public class BookUtil {
	private static @Getter File bookDirectory;

	//	private static Reflect CRAFT_ITEMSTACK;
	//	private static Object MAIN_HAND_ENUM;

	private static boolean initialized = false;

	public void init() {
		initialized = true;

		bookDirectory = new File(KageCore.getPluginDirectory(), "books");

		if(!bookDirectory.exists()) {
			bookDirectory.mkdirs();
		}

		//		CRAFT_ITEMSTACK = PackageType.CRAFTBUKKIT_INVENTORY.getClass("CraftItemStack");
		//
		//		MAIN_HAND_ENUM = PackageType.MINECRAFT_SERVER.getClass("EnumHand").get("MAIN_HAND");
	}

	public static void openBookGUI(Player player, String bookName) {
		openBookGUI(player, new File(bookDirectory, bookName + ".mcb"));
	}

	public static void openBookGUI(Player player, File yamlFile) {
		openBookGUI(player, new KBook(yamlFile));
	}

	public static void openBookGUI(Player player, List<String> content) {
		openBookGUI(player, new KBook("Book GUI", "", Generation.ORIGINAL, content));
	}

	public static void openBookGUI(Player player, KBook book) {
		openBookGUI(player, book.getItemStack());
	}

	public static void openBookGUI(Player player, ItemStack bookItem) {
		if(!initialized) {
			KageCore.debugMessage("Can't display book gui!");

			return;
		}

		if(!bookItem.getType().equals(Material.WRITTEN_BOOK)) {
			throw new IllegalArgumentException("bookItem needs to be a written book");
		}

		PlayerInventory inv = player.getInventory();

		ItemStack currentHeldItem = inv.getItemInMainHand();

		inv.setItemInMainHand(bookItem);

		try {
			KageCore.debugMessage("Opening book");
			EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
			nmsPlayer.a(CraftItemStack.asNMSCopy(bookItem), EnumHand.MAIN_HAND);
			KageCore.debugMessage("Openend book");
			//			Reflect nmsPlayerReflect = Reflect.on(Reflect.on(player).call("getHandle"));
			//
			//			Object nmsBookItemObject = CRAFT_ITEMSTACK.call("asNMSCopy", bookItem).get();
			//
			//			nmsPlayerReflect.call("a", nmsBookItemObject, MAIN_HAND_ENUM);
		} catch(Exception ex) {
			ex.printStackTrace();

			initialized = false;
		}

		inv.setItemInMainHand(currentHeldItem);
	}
}