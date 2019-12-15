package de.syscy.kagecore.factory.entity.versioncompat;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.syscy.kagecore.factory.entity.EntityFactoryNMS;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.entity.Entity;

public class EntityFactoryNMS_v1_14_R1 implements EntityFactoryNMS {
	@Override
	public Entity createEntity(String entityTypeID, Location location, String nbt) {
		final WorldServer nmsWorld = ((CraftWorld) location.getWorld()).getHandle();

		NBTTagCompound nbtTagCompound = new NBTTagCompound();

		boolean nbtInitialized = false;

		if(!nbt.isEmpty()) {
			try {
				nbtTagCompound = MojangsonParser.parse(nbt);
				nbtInitialized = true;
			} catch(CommandSyntaxException ex) {
				ex.printStackTrace();
			}
		}

		nbtTagCompound.setString("id", entityTypeID);

		net.minecraft.server.v1_14_R1.Entity entity = EntityTypes.a(nbtTagCompound, nmsWorld, e -> {
			e.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

			return !nmsWorld.addEntitySerialized(e) ? null : e;
		});

		if(entity == null) {
			return null;
		}

		if(!nbtInitialized && entity instanceof EntityInsentient) {
			((EntityInsentient) entity).prepare(nmsWorld, nmsWorld.getDamageScaler(new BlockPosition(entity)), EnumMobSpawn.NATURAL, null, null);
		}

		return entity.getBukkitEntity();

		/*final WorldServer nmsWorld = ((CraftWorld) location.getWorld()).getHandle();

		net.minecraft.server.v1_14_R1.Entity nmsEntity;

		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		boolean nbtInitialized = false;

		if(!nbt.isEmpty()) {
			try {
				nbtTagCompound = MojangsonParser.parse(nbt);
				nbtInitialized = true;
			} catch(CommandSyntaxException ex) {
				ex.printStackTrace();
			}
		}

		nbtTagCompound.setString("id", entityTypeID);
		ChunkRegionLoader.a()
		nmsEntity = ChunkRegionLoader.spawnEntity(nbtTagCompound, nmsWorld, location.getX(), location.getY(), location.getZ(), true, CreatureSpawnEvent.SpawnReason.CUSTOM);

		if(nmsEntity == null) {
			return null;
		}

		nmsEntity.setPositionRotation(location.getX(), location.getY(), location.getZ(), nmsEntity.yaw, nmsEntity.pitch);

		if(!nbtInitialized && nmsEntity instanceof EntityInsentient) {
			((EntityInsentient) nmsEntity).prepare(nmsWorld.D(new BlockPosition(nmsEntity)), null);
		}

		return nmsEntity.getBukkitEntity();*/
	}
}