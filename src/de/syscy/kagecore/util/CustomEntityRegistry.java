package de.syscy.kagecore.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.annotation.Nullable;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.minecraft.server.v1_11_R1.Entity;
import net.minecraft.server.v1_11_R1.EntityTypes;
import net.minecraft.server.v1_11_R1.MinecraftKey;
import net.minecraft.server.v1_11_R1.RegistryMaterials;

public class CustomEntityRegistry extends RegistryMaterials<MinecraftKey, Class<? extends Entity>> {
	private static CustomEntityRegistry instance = null;

	private final BiMap<MinecraftKey, Class<? extends Entity>> keyToClass = HashBiMap.create();
	private final BiMap<Class<? extends Entity>, MinecraftKey> classToKey = keyToClass.inverse();
	private final BiMap<Class<? extends Entity>, Integer> classToId = HashBiMap.create();

	private final RegistryMaterials<MinecraftKey, Class<? extends Entity>> wrappedRegistry;

	private CustomEntityRegistry(RegistryMaterials<MinecraftKey, Class<? extends Entity>> originalRegistry) {
		wrappedRegistry = originalRegistry;
	}

	public static CustomEntityRegistry getInstance() {
		if(instance != null) {
			return instance;
		}

		instance = new CustomEntityRegistry(EntityTypes.b);

		try {
			Field registryMaterialsField = EntityTypes.class.getDeclaredField("b");
			registryMaterialsField.setAccessible(true);

			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(registryMaterialsField, registryMaterialsField.getModifiers() & ~Modifier.FINAL);

			registryMaterialsField.set(null, instance);
		} catch(Exception ex) {
			instance = null;

			throw new RuntimeException("Unable to override the old entity RegistryMaterials", ex);
		}

		return instance;
	}

	public static void registerCustomEntity(int entityId, String entityName, Class<? extends Entity> entityClass) {
		getInstance().putCustomEntity(entityId, entityName, entityClass);
	}

	public void putCustomEntity(int entityId, String entityName, Class<? extends Entity> entityClass) {
		MinecraftKey minecraftKey = new MinecraftKey(entityName);

		keyToClass.put(minecraftKey, entityClass);
		classToId.put(entityClass, entityId);
	}

	@Nullable
	@Override
	public Class<? extends Entity> get(@Nullable MinecraftKey key) {
		if(keyToClass.containsKey(key)) {
			return keyToClass.get(key);
		}

		return wrappedRegistry.get(key);
	}

	@Override
	@Nullable
	public MinecraftKey b(Class<? extends Entity> entityClass) {
		if(classToKey.containsKey(entityClass)) {
			return classToKey.get(entityClass);
		}

		return wrappedRegistry.b(entityClass);
	}

	@Override
	public int a(@Nullable Class<? extends Entity> entityClass) {
		if(classToId.containsKey(entityClass)) {
			return classToId.get(entityClass);
		}

		return wrappedRegistry.a(entityClass);
	}
}