package de.syscy.kagecore.entityregistry.versioncompat;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import de.syscy.kagecore.KageCore;
import de.syscy.kagecore.entityregistry.IEntityRegistry;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftLivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings(value = { "rawtypes", "unchecked" })
public class IEntityRegistry_v1_14_R1 implements IEntityRegistry { //TODO: Add support for different versions
	private Map<String, EntityTypes<?>> registeredEntityMap = new HashMap<>();

	private IEntityRegistry_v1_14_R1() {

	}

	@Override
	public void init() {

	}

	@Override
	public void registerEntity(String entityName, String replacingName, Object entityConstructor) {
		if(!(entityConstructor instanceof EntityTypes.b)) {
			throw new IllegalArgumentException("No valid entity constructor given");
		}

		Map<String, Type<?>> types = (Map<String, Type<?>>) DataConverterRegistry.a().getSchema(DataFixUtils.makeKey(SharedConstants.a().getWorldVersion())).findChoiceType(DataConverterTypes.ENTITY).types();
		types.put("minecraft:" + entityName, types.get("minecraft:" + replacingName));
		EntityTypes.a<Entity> a = EntityTypes.a.a((EntityTypes.b) entityConstructor, EnumCreatureType.MONSTER);
		registeredEntityMap.put(entityName.toLowerCase(), IRegistry.a(IRegistry.ENTITY_TYPE, entityName, a.a(entityName)));
	}

	@Override
	public org.bukkit.entity.Entity spawnEntity(String entityName, Location location) {
		WorldServer world = ((CraftWorld) location.getWorld()).getHandle();

		EntityTypes<?> type = registeredEntityMap.get(entityName.toLowerCase());

		if(type == null) {
			throw new IllegalArgumentException("Invalid custom entity name \"" + entityName + "\"! Already registered the entity?");
		}

		Entity nmsEntity = type.b(world, null, null, null, new BlockPosition(location.getX(), location.getY(), location.getZ()), EnumMobSpawn.MOB_SUMMONED, false, false);

		if(nmsEntity != null) {
			nmsEntity.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

			world.addEntity(nmsEntity, CreatureSpawnEvent.SpawnReason.CUSTOM);

			if(nmsEntity instanceof EntityLiving) {
				((CraftLivingEntity) nmsEntity.getBukkitEntity()).setRemoveWhenFarAway(false);
			}

			return nmsEntity.getBukkitEntity();
		} else {
			KageCore.debugMessage("Could not spawn custom entity \"" + entityName + "\".");

			return null;
		}
	}
/*
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
	*/
}