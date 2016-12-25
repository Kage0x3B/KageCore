package de.syscy.kagegui.util;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import de.syscy.kagecore.KageCore;
import de.syscy.kagecore.util.book.KBook;
import de.syscy.kagegui.icon.ItemIcon;
import de.syscy.kagegui.inventory.component.KButton;
import de.syscy.kagegui.inventory.listener.ButtonClickListener;

public class KBookButton extends KButton {
	private KBookButton(Builder builder) {
		super(builder);
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder extends KButton.Builder {
		private KBook book;

		public Builder book(String bookName) {
			return book(new KBook(bookName));
		}

		public Builder book(KBook book) {
			this.book = book;

			title(book.getTitle());
			icon(new ItemIcon(Material.BOOK));

			return this;
		}

		@Override
		public KButton build() {
			clickListener(new ButtonClickListener() {
				@Override
				public void onClick(KButton button, Player player) {
					KageCore.debugMessage(this + " clicked!");
					book.showPlayer(player);
				}
			});

			return new KBookButton(this);
		}
	}
}