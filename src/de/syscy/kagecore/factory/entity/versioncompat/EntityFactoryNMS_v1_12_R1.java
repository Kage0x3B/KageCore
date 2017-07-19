package de.syscy.kagecore.factory.entity.versioncompat;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent;

import de.syscy.kagecore.factory.entity.EntityFactoryNMS;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.ChunkRegionLoader;
import net.minecraft.server.v1_12_R1.EntityInsentient;
import net.minecraft.server.v1_12_R1.MojangsonParseException;
import net.minecraft.server.v1_12_R1.MojangsonParser;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.WorldServer;

public class EntityFactoryNMS_v1_12_R1 implements EntityFactoryNMS {
	@Override
	public Entity createEntity(String entityTypeID, Location location, String nbt) {
		final WorldServer nmsWorld = ((CraftWorld) location.getWorld()).getHandle();

		net.minecraft.server.v1_12_R1.Entity nmsEntity;

		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		boolean nbtInitialized = false;

		if(!nbt.isEmpty()) {
			try {
				nbtTagCompound = MojangsonParser.parse(nbt);
				nbtInitialized = true;
			} catch(final MojangsonParseException ex) {
				ex.printStackTrace();
			}
		}

		nbtTagCompound.setString("id", entityTypeID);
		nmsEntity = ChunkRegionLoader.spawnEntity(nbtTagCompound, nmsWorld, location.getX(), location.getY(), location.getZ(), true, CreatureSpawnEvent.SpawnReason.CUSTOM);

		if(nmsEntity == null) {
			return null;
		}

		nmsEntity.setPositionRotation(location.getX(), location.getY(), location.getZ(), nmsEntity.yaw, nmsEntity.pitch);

		if(!nbtInitialized && nmsEntity instanceof EntityInsentient) {
			((EntityInsentient) nmsEntity).prepare(nmsWorld.D(new BlockPosition(nmsEntity)), null);
		}

		return nmsEntity.getBukkitEntity();
	}
}