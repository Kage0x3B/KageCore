package de.syscy.kagegui.util;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import de.syscy.kagecore.util.book.KBook;
import de.syscy.kagegui.icon.ItemIcon;
import de.syscy.kagegui.inventory.component.KButton;
import de.syscy.kagegui.inventory.listener.ButtonClickListener;
import lombok.Getter;

public class KBookButton extends KButton {
	private @Getter KBook book;

	public KBookButton(int x, int y, String bookName) {
		this(x, y, new KBook(bookName));
	}

	public KBookButton(int x, int y, KBook book) {
		super(x, y);

		setBook(book);

		clickListener[ClickType.GENERAL.ordinal()] = new ButtonClickListener() {
			@Override
			public void onClick(KButton button, Player player) {
				if(gui != null) {
					gui.close();
				}

				KBookButton.this.book.showPlayer(player);
			}
		};
	}

	public void setBook(String bookName) {
		setBook(new KBook(bookName));
	}

	public void setBook(KBook book) {
		this.book = book;

		setTitle(book.getTitle());
		setIcon(new ItemIcon(Material.BOOK));
	}
}