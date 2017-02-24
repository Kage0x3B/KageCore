package de.syscy.kagecore.worldedit;

import org.bukkit.Location;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.session.ClipboardHolder;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class Schematic {
	private final @Getter EditSession editSession;

	private final @Getter ClipboardHolder clipboardHolder;

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

	public void paste(Location location) throws WorldEditException {
		paste(location, true);
	}

	public void paste(Location location, boolean ignoreAirBlocks) throws WorldEditException {
		editSession.setBlockChangeLimit(Integer.MAX_VALUE);

		Vector position = new Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ());
		Operation pasteOperation = clipboardHolder.createPaste(editSession, clipboardHolder.getWorldData()).to(position).ignoreAirBlocks(ignoreAirBlocks).build();

		Operations.complete(pasteOperation);
	}
}