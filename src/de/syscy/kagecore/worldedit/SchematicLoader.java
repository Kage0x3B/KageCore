package de.syscy.kagecore.worldedit;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.bukkit.Location;
import org.bukkit.World;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.util.io.Closer;
import com.sk89q.worldedit.world.registry.WorldData;

import de.syscy.kagecore.util.BoundingBox;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SchematicLoader {
	/**
	 * Loads a schematic from the plugins/WorldEdit/schematics folder
	 * @param world The world in which the schematic will be used
	 * @param schematicName The file name in the schematics folder
	 */
	public static Schematic load(World world, String schematicName) throws IOException {
		if(!WorldEditUtil.initWorldEdit()) {
			return null;
		}

		return load(world, WorldEditUtil.getWorldEdit().getWorkingDirectoryFile("schematics/" + schematicName));
	}

	/**
	 * Loads a schematic from a file
	 * @param world The world in which the schematic will be used
	 * @param file
	 */
	public static Schematic load(World world, File schematicFile) throws IOException {
		if(!WorldEditUtil.initWorldEdit()) {
			return null;
		}

		com.sk89q.worldedit.world.World weWorld = WorldEditUtil.getWEWorld(world);
		WorldData worldData = weWorld.getWorldData();

		ClipboardFormat format = ClipboardFormat.findByFile(schematicFile);

		Closer closer = Closer.create();

		BufferedInputStream inputStream = closer.register(new BufferedInputStream(closer.register(new FileInputStream(schematicFile))));
		ClipboardReader clipboardReader = format.getReader(inputStream);

		Clipboard clipboard = clipboardReader.read(worldData);
		ClipboardHolder clipboardHolder = new ClipboardHolder(clipboard, worldData);

		closer.close();

		return new Schematic(WorldEditUtil.createSession(weWorld), new ClipboardHolder(clipboardHolder.getClipboard(), clipboardHolder.getWorldData()));
	}

	public static Schematic loadArea(World world, BoundingBox boundingBox, Location origin) {
		if(!WorldEditUtil.initWorldEdit()) {
			return null;
		}

		com.sk89q.worldedit.world.World weWorld = WorldEditUtil.getWEWorld(world);

		CuboidRegion region = new CuboidRegion(toVector(boundingBox.getMin()), toVector(boundingBox.getMax()));

		EditSession editSession = WorldEditUtil.createSession(weWorld);

		BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
		clipboard.setOrigin(toVector(origin));
		ForwardExtentCopy copy = new ForwardExtentCopy(editSession, region, clipboard, region.getMinimumPoint());

		try {
			Operations.completeLegacy(copy);
		} catch(MaxChangedBlocksException ex) {
			return null;
		}

		return new Schematic(editSession, new ClipboardHolder(clipboard, editSession.getWorld().getWorldData()));
	}

	private static Vector toVector(Location location) {
		return new Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}
}