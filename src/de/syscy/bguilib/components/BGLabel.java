package de.syscy.bguilib.components;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.syscy.bguilib.components.icon.ItemIcon;
import de.syscy.bguilib.util.Lore;
import lombok.Getter;
import lombok.Setter;

public class BGLabel extends BGComponent {
	private @Getter @Setter ItemIcon labelIcon;
	private @Getter @Setter String title;
	private @Getter @Setter Lore lore;

	public BGLabel(int x, int y, String title) {
		this(x, y, 1, 1, title);
	}

	public BGLabel(int x, int y, int width, int height, String title) {
		super(x, y, width, height);
		this.title = "Label";
		this.lore = new Lore(new String[] { "" });
		this.title = title;
		this.setLabelIcon(new ItemIcon(new ItemStack(Material.STAINED_GLASS_PANE)));
	}

	public void update() {
		this.labelIcon.update();
	}

	public void render(BGInventory inventory) {
		this.renderItem(inventory, this.x, this.y, this.width, this.height, this.labelIcon, this.title, this.lore);
	}

	public void onClick(Player player, int localX, int localY) {

	}
}