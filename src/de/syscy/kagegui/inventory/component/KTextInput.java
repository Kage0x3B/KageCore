package de.syscy.kagegui.inventory.component;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import de.syscy.kagegui.IInventoryWrapper;
import de.syscy.kagegui.callbacks.ChatInputCallback;
import de.syscy.kagegui.icon.ItemIcon;
import de.syscy.kagegui.inventory.listener.TextInputListener;
import de.syscy.kagegui.listener.ChatListener;
import de.syscy.kagegui.util.Lore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

public class KTextInput extends KComponent implements ChatInputCallback {
	private @Getter String title;
	private @Getter Lore lore;
	private @Getter ItemIcon icon;

	private @Getter String text;

	private TextInputListener textInputListener;

	private KTextInput(Builder builder) {
		super(builder);

		title = builder.title;
		lore = builder.lore;
		icon = builder.icon;
		text = builder.text;
		textInputListener = builder.textInputListener;

		lore.setTemporaryFirstLine("§kgui.textInput.lore1;");
	}

	@Override
	public void update() {
		icon.update(gui);
	}

	@Override
	public void render(IInventoryWrapper inventory) {
		this.renderItem(inventory, x, y, width, height, icon, title, lore);
	}

	@Override
	public void onClick(InventoryClickEvent event, Player player, int localX, int localY) {
		if(textInputListener != null) {
			ChatListener.addPlayerChatInputCallback(player, getTitle(), this);
			getGui().hide();
		}
	}

	@Override
	public void onChatInput(Player player, String text) {
		getGui().unhide();

		if(textInputListener != null) {
			textInputListener.onTextInput(player, text);
		}

		this.text = text;
		lore.setTemporaryFirstLine(text);

		gui.markDirty();
	}

	public void setText(String text) {
		this.text = text;
		lore.setTemporaryFirstLine(text);
	}

	public static Builder builder() {
		return new Builder();
	}

	@Accessors(fluent = true)
	public static class Builder extends KComponent.Builder<KTextInput> {
		private @Setter String title;
		private @Setter Lore lore;
		private @Setter ItemIcon icon = new ItemIcon(new ItemStack(Material.BOOK_AND_QUILL));

		private @Setter String text = "";

		private @Setter TextInputListener textInputListener;

		@Override
		public KTextInput build() {
			return new KTextInput(this);
		}
	}
}