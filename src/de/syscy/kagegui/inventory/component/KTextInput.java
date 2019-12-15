package de.syscy.kagegui.inventory.component;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import de.syscy.kagecore.translation.Translator;
import de.syscy.kagegui.IInventoryWrapper;
import de.syscy.kagegui.callbacks.ChatInputCallback;
import de.syscy.kagegui.icon.ItemIcon;
import de.syscy.kagegui.inventory.listener.TextInputListener;
import de.syscy.kagegui.listener.ChatListener;
import de.syscy.kagegui.util.LoreBuilder;
import lombok.Getter;
import lombok.Setter;

public class KTextInput extends KComponent implements ChatInputCallback {
	protected @Getter @Setter String title;
	protected final @Getter LoreBuilder loreBuilder = new LoreBuilder();
	protected @Getter @Setter ItemIcon icon = new ItemIcon(new ItemStack(Material.WRITABLE_BOOK));

	protected @Getter String text = "";

	protected @Setter TextInputListener textInputListener;

	public KTextInput(int x, int y) {
		super(x, y);

		loreBuilder.set(LoreBuilder.KCOMPONENT_LORE, Translator.SIGN + "kgui.textInput.lore1;");
	}

	@Override
	public void update() {
		icon.update(gui);
	}

	@Override
	public void render(IInventoryWrapper inventory) {
		this.renderItem(inventory, x, y, width, height, icon, title, loreBuilder);
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
		loreBuilder.set(LoreBuilder.KCOMPONENT_LORE, Translator.SIGN + "kgui.textInput.lore1;" + text + ";");

		gui.markDirty();
	}

	public void setText(String text) {
		this.text = text;
		loreBuilder.set(LoreBuilder.KCOMPONENT_LORE, Translator.SIGN + "kgui.textInput.lore1;" + text + ";");
	}
}