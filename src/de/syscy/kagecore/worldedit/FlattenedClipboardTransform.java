/*
 * WorldEdit, a Minecraft world manipulation toolkit
 * Copyright (C) sk89q <http://www.sk89q.com>
 * Copyright (C) WorldEdit team and contributors
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.syscy.kagecore.worldedit;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.transform.BlockTransformExtent;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.math.transform.CombinedTransform;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;

/**
 * Helper class to 'bake' a transform into a clipboard.
 *
 * <p>This class needs a better name and may need to be made more generic.</p>
 *
 * @see Clipboard
 * @see Transform
 */
public class FlattenedClipboardTransform {
	private final Clipboard original;
	private final Transform transform;

	private FlattenedClipboardTransform(Clipboard original, Transform transform) {
		Preconditions.checkNotNull(original);
		Preconditions.checkNotNull(transform);
		this.original = original;
		this.transform = transform;
	}

	public Region getTransformedRegion() {
		Region region = this.original.getRegion();
		Vector3 minimum = region.getMinimumPoint().toVector3();
		Vector3 maximum = region.getMaximumPoint().toVector3();
		Transform transformAround = new CombinedTransform((new AffineTransform()).translate(this.original.getOrigin().multiply(-1)), this.transform, (new AffineTransform()).translate(this.original.getOrigin()));
		Vector3[] corners = new Vector3[] { minimum, maximum, minimum.withX(maximum.getX()), minimum.withY(maximum.getY()), minimum.withZ(maximum.getZ()), maximum.withX(minimum.getX()), maximum.withY(minimum.getY()), maximum.withZ(minimum.getZ()) };

		for(int i = 0; i < corners.length; ++i) {
			corners[i] = transformAround.apply(corners[i]);
		}

		Vector3 newMinimum = corners[0];
		Vector3 newMaximum = corners[0];

		for(int i = 1; i < corners.length; ++i) {
			newMinimum = newMinimum.getMinimum(corners[i]);
			newMaximum = newMaximum.getMaximum(corners[i]);
		}

		newMinimum = newMinimum.floor();
		newMaximum = newMaximum.ceil();
		return new CuboidRegion(newMinimum.toBlockPoint(), newMaximum.toBlockPoint());
	}

	public Operation copyTo(Extent target) {
		BlockTransformExtent extent = new BlockTransformExtent(this.original, this.transform);
		ForwardExtentCopy copy = new ForwardExtentCopy(extent, this.original.getRegion(), this.original.getOrigin(), target, this.original.getOrigin());
		copy.setTransform(this.transform);
		if(this.original.hasBiomes()) {
			copy.setCopyingBiomes(true);
		}

		return copy;
	}

	public static FlattenedClipboardTransform transform(Clipboard original, Transform transform) {
		return new FlattenedClipboardTransform(original, transform);
	}
}