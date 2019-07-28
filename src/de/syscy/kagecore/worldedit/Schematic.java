package de.syscy.kagecore.worldedit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.session.ClipboardHolder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Schematic {
	private @Getter @Setter EditSession editSession;

	private @Getter @Setter ClipboardHolder clipboardHolder;

	public void rotateX(double theta) {
		rotate(theta, 0, 0);
	}

	public void rotateY(double theta) {
		rotate(0, theta, 0);
	}

	public void rotateZ(double theta) {
		rotate(0, 0, theta);
	}

	public void rotate(double x, double y, double z) {
		AffineTransform transform = new AffineTransform();

		if(x != 0) {
			transform = transform.rotateX(x);
		}

		if(y != 0) {
			transform = transform.rotateY(y);
		}

		if(z != 0) {
			transform = transform.rotateZ(z);
		}

		clipboardHolder.setTransform(clipboardHolder.getTransform().combine(transform));
	}

	/**
	 * Applies the current transform to the clipboard.
	 * Usually this only happens immediately before the schematic is pasted.
	 */
	public void bakeTransform() throws WorldEditException {
		FlattenedClipboardTransform result = FlattenedClipboardTransform.transform(clipboardHolder.getClipboard(), clipboardHolder.getTransform());

		Clipboard newClipboard = new BlockArrayClipboard(result.getTransformedRegion());
		newClipboard.setOrigin(clipboardHolder.getClipboard().getOrigin());
		Operations.complete(result.copyTo(newClipboard));

		clipboardHolder = new ClipboardHolder(newClipboard);
	}

	public void paste(Location location) throws WorldEditException {
		paste(location, true);
	}

	public void paste(Location location, boolean ignoreAirBlocks) throws WorldEditException {
		editSession.setBlockChangeLimit(Integer.MAX_VALUE);

		BlockVector3 position = BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ());

		Operation pasteOperation = clipboardHolder.createPaste(editSession).to(position).ignoreAirBlocks(ignoreAirBlocks).copyBiomes(false).copyEntities(false).build();

		Operations.completeLegacy(pasteOperation);
		editSession.flushSession();
	}
}