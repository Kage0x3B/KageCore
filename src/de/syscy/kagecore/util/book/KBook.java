package de.syscy.kagecore.util.book;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import de.syscy.kagecore.KageCore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.BookMeta.Generation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class KBook {
	private @Getter @Setter String title;
	private @Getter @Setter String author;
	private @Getter @Setter Generation generation;
	private @Getter @Setter List<String> pages = new ArrayList<>();

	public KBook(String bookName) {
		this(new File(BookUtil.getBookDirectory(), bookName + ".mcb"));
	}

	public KBook(File yamlFile) {
		YamlConfiguration bookYaml = YamlConfiguration.loadConfiguration(yamlFile);

		title = ChatColor.translateAlternateColorCodes('&', bookYaml.getString("title", "Book"));
		author = ChatColor.translateAlternateColorCodes('&', bookYaml.getString("author", "Kage0x3B"));
		String generationString = bookYaml.getString("generation", "original");

		try {
			generation = Generation.valueOf(generationString.toUpperCase().replaceAll(" ", "_"));
		} catch(Exception ex) {
			KageCore.debugMessage("Invalid generation: " + generationString);

			generation = Generation.ORIGINAL;
		}

		if(bookYaml.contains("pages")) {
			ConfigurationSection pagesSection = bookYaml.getConfigurationSection("pages");

			for(String key : pagesSection.getKeys(false)) {
				if(key.startsWith("page")) {
					try {
						String pageContent = "";

						if(pagesSection.isList(key)) {
							pageContent = Joiner.on('\n').join(pagesSection.getStringList(key));
						} else if(pagesSection.isString(key)) {
							pageContent = pagesSection.getString(key);
						}

						pages.add(ChatColor.translateAlternateColorCodes('&', pageContent));
					} catch(Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}
	}

	public KBook(ItemStack itemStack) {
		if(!itemStack.hasItemMeta() || !(itemStack.getItemMeta() instanceof BookMeta)) {
			throw new IllegalArgumentException("the itemstack needs to be a book");
		}

		BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();

		title = bookMeta.getTitle();
		author = bookMeta.getAuthor();
		generation = bookMeta.getGeneration();
		pages.addAll(bookMeta.getPages());
	}

	public boolean hasAuthor() {
		return !Strings.isNullOrEmpty(author);
	}

	public boolean hasTitle() {
		return !Strings.isNullOrEmpty(title);
	}

	public boolean hasPages() {
		return !pages.isEmpty();
	}

	public boolean hasGeneration() {
		if(generation != null) {
			return true;
		}

		return false;
	}

	public String getPage(int i) {
		return pages.get(i);
	}

	public void setPage(int i, String content) {
		pages.set(i, content);
	}

	public void addPage(String... lines) {
		pages.add(Joiner.on('\n').join(lines));
	}

	public int getPageCount() {
		return pages.size();
	}

	@Override
	public KBook clone() {
		return new KBook(title, author, generation, new ArrayList<>(pages));
	}

	public ItemStack getItemStack() {
		ItemStack bookItem = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta bookMeta = (BookMeta) bookItem.getItemMeta();

		bookMeta.setTitle(title);
		bookMeta.setAuthor(author);
		bookMeta.setGeneration(generation);
		bookMeta.setPages(pages);

		bookMeta.addItemFlags(ItemFlag.values());

		bookItem.setItemMeta(bookMeta);

		return bookItem;
	}

	public void showPlayer(Player player) {
		BookUtil.openBookGUI(player, this);
	}
}