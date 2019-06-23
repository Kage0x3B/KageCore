package de.syscy.kagecore.entityregistry.versioncompat;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.syscy.kagecore.entityregistry.IEntityRegistry;
import de.syscy.kagecore.versioncompat.reflect.Reflect;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.Location;

@SuppressWarnings(value = { "rawtypes", "unchecked" })
public class IEntityRegistry_v1_14_R1 extends RegistryBlocks<EntityTypes<?>> implements IEntityRegistry { //TODO: Add support for different versions
	private final BiMap<MinecraftKey, Class<? extends Entity>> keyToClass = HashBiMap.create();
	private final BiMap<Class<? extends Entity>, MinecraftKey> classToKey = keyToClass.inverse();
	private final BiMap<Class<? extends Entity>, Integer> classToId = HashBiMap.create();

	private final RegistryBlocks<EntityTypes<?>> wrappedRegistry;

	private IEntityRegistry_v1_14_R1() {
		super("pig");

		wrappedRegistry = IRegistry.ENTITY_TYPE;
	}

	@Override
	public void init() {
		Reflect registryReflect = Reflect.on(IRegistry.class);
		registryReflect.set("ENTITY_TYPE", this);
	}

	@Override
	public void registerEntity(int entityId, String entityName, Class<?> entityClass) {
		//TODO: Implement overriding entities
	}

	@Override
	public org.bukkit.entity.Entity spawnEntity(Class<?> entityClass, Location location) {
		return null;
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