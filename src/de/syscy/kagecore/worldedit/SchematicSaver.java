package de.syscy.kagecore.worldedit;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.util.io.Closer;
import lombok.experimental.UtilityClass;

import java.io.*;

@UtilityClass
public class SchematicSaver {
	/**
	 * Saves a schematic to a file (Ignores any transforms right now)
	 * @param schematic The schematic
	 * @param schematicFile The file to save the schematic to
	 */
	public static boolean save(Schematic schematic, File schematicFile) throws IOException {
		return save(schematic, schematicFile, BuiltInClipboardFormat.MCEDIT_SCHEMATIC);
	}

	/**
	 * Saves a schematic to a file (Ignores any transforms right now)
	 * @param schematic The schematic
	 * @param schematicFile The file to save the schematic to
	 * @param clipboardFormat schematic format
	 */
	public static boolean save(Schematic schematic, File schematicFile, ClipboardFormat clipboardFormat) throws IOException {
		File parent = schematicFile.getParentFile();

		if(parent != null && !parent.exists()) {
			if(!parent.mkdirs()) {
				throw new IOException("Could not create folder for the schematic!");
			}
		}

		try(FileOutputStream schematicOutputStream = new FileOutputStream(schematicFile)) {
			return save(schematic, schematicOutputStream, clipboardFormat);
		}
	}

	/**
	 * Saves a schematic to a file (Ignores any transforms right now)
	 * @param schematic The schematic
	 * @param schematicOutputStream output stream which to write the schematic to
	 * @param clipboardFormat the schematic format
	 * @return if saving succeeded
	 * @throws IOException
	 */
	public static boolean save(Schematic schematic, OutputStream schematicOutputStream, ClipboardFormat clipboardFormat) throws IOException {
		if(!WorldEditUtil.initWorldEdit()) {
			return false;
		}

		ClipboardHolder clipboardHolder = schematic.getClipboardHolder();
		Clipboard clipboard = clipboardHolder.getClipboard();

		Closer closer = Closer.create();

		BufferedOutputStream bufferedOutputStream = closer.register(new BufferedOutputStream(schematicOutputStream));
		ClipboardWriter clipboardWriter = closer.register(clipboardFormat.getWriter(bufferedOutputStream));
		clipboardWriter.write(clipboard);

		try {
			closer.close();
		} catch(IOException ignored) {
		}

		return true;
	}
}