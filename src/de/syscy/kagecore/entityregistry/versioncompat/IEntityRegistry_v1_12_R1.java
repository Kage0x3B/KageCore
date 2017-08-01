package de.syscy.kagecore.entityregistry.versioncompat;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftLivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import de.syscy.kagecore.entityregistry.IEntityRegistry;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.EntityInsentient;
import net.minecraft.server.v1_12_R1.EntityLiving;
import net.minecraft.server.v1_12_R1.EntityTypes;
import net.minecraft.server.v1_12_R1.MinecraftKey;
import net.minecraft.server.v1_12_R1.RegistryMaterials;
import net.minecraft.server.v1_12_R1.World;

@SuppressWarnings(value = { "rawtypes", "unchecked" })
public class IEntityRegistry_v1_12_R1 extends RegistryMaterials implements IEntityRegistry { //TODO: Add support for different versions
	private final BiMap<MinecraftKey, Class<? extends Entity>> keyToClass = HashBiMap.create();
	private final BiMap<Class<? extends Entity>, MinecraftKey> classToKey = keyToClass.inverse();
	private final BiMap<Class<? extends Entity>, Integer> classToId = HashBiMap.create();

	private final RegistryMaterials<MinecraftKey, Class<? extends Entity>> wrappedRegistry;

	private IEntityRegistry_v1_12_R1() {
		wrappedRegistry = EntityTypes.b;
	}

	@Override
	public void init() {
		try {
			Field registryMaterialsField = EntityTypes.class.getDeclaredField("b");
			registryMaterialsField.setAccessible(true);

			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(registryMaterialsField, registryMaterialsField.getModifiers() & ~Modifier.FINAL);

			registryMaterialsField.set(null, this);
		} catch(Exception ex) {
			throw new RuntimeException("Unable to override the old entity RegistryMaterials", ex);
		}
	}

	@Override
	public org.bukkit.entity.Entity spawnEntity(Class<?> entityClass, Location location) {
		World world = ((CraftWorld) location.getWorld()).getHandle();

		Entity entity = null;

		try {
			entity = (Entity) entityClass.getConstructor(World.class).newInstance(world);
		} catch(Exception ex) {
			ex.printStackTrace();
		}

		entity.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

		if(entity instanceof EntityInsentient) {
			((EntityInsentient) entity).prepare(world.D(new BlockPosition(entity)), null);
		}

		if(entity instanceof EntityLiving) {
			((CraftLivingEntity) entity.getBukkitEntity()).setRemoveWhenFarAway(false);
		}

		world.addEntity(entity, SpawnReason.CUSTOM);

		return entity.getBukkitEntity();
	}

	@Override
	public void registerEntity(int entityId, String entityName, Class<?> entityClass) {
		MinecraftKey minecraftKey = new MinecraftKey(entityName);

		keyToClass.put(minecraftKey, (Class<? extends Entity>) entityClass);
		classToId.put((Class<? extends Entity>) entityClass, entityId);
	}

	@Nullable
	@Override
	public Class<? extends Entity> get(@Nullable Object keyObject) {
		MinecraftKey key = (MinecraftKey) keyObject;

		if(keyToClass.containsKey(key)) {
			return keyToClass.get(key);
		}

		return wrappedRegistry.get(key);
	}

	@Override
	@Nullable
	public MinecraftKey b(Object entityClassObject) {
		Class<? extends Entity> entityClass = (Class<? extends Entity>) entityClassObject;

		if(classToKey.containsKey(entityClass)) {
			return classToKey.get(entityClass);
		}

		return wrappedRegistry.b(entityClass);
	}

	@Override
	public int a(@Nullable Object entityClassObject) {
		Class<? extends Entity> entityClass = (Class<? extends Entity>) entityClassObject;

		if(classToId.containsKey(entityClass)) {
			return classToId.get(entityClass);
		}

		return wrappedRegistry.a(entityClass);
	}
}