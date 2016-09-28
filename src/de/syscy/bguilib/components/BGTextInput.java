package de.syscy.bguilib.components;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.syscy.bguilib.callbacks.ChatInputCallback;
import de.syscy.bguilib.components.icon.ItemIcon;
import de.syscy.bguilib.components.listener.TextInputListener;
import de.syscy.bguilib.listener.ChatListener;
import de.syscy.bguilib.util.Lore;
import lombok.Getter;
import lombok.Setter;

public class BGTextInput extends BGComponent implements ChatInputCallback {
	private List<TextInputListener> listeners = new ArrayList<>();
	private @Getter @Setter ItemIcon buttonIcon;
	private @Getter @Setter String title;
	private @Getter @Setter Lore lore = new Lore();
	private @Getter String text = "";

	public BGTextInput(int x, int y, String title) {
		super(x, y, 1, 1);

		this.title = title;
		this.setButtonIcon(new ItemIcon(new ItemStack(Material.BOOK_AND_QUILL)));
		this.lore.setTemporaryFirstLine("Click to edit...");
	}

	public void update() {
		this.buttonIcon.update();
	}

	public void render(BGInventory inventory) {
		this.renderItem(inventory, this.x, this.y, this.width, this.height, this.buttonIcon, this.title, this.lore);
	}

	public void onClick(Player player, int localX, int localY) {
		ChatListener.addPlayerChatInputCallback(player, this.getTitle(), this);
		this.getGui().hide();
	}

	public void onChatInput(Player player, String text) {
		this.getGui().unhide();

		for(TextInputListener listener : listeners) {
			listener.onTextInput(player, text);
		}

		this.text = text;
		this.lore.setTemporaryFirstLine(text);
	}

	public void addTextInputListener(TextInputListener listener) {
		this.listeners.add(listener);
	}

	public void setText(String text) {
		this.text = text;
		this.lore.setTemporaryFirstLine(text);
	}
}