package de.syscy.kagecore.entityregistry;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import de.syscy.kagecore.KageCore;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.HashMap;
import java.util.Map;

public class EntityRegistry {
    private static final Map<String, EntityTypes<?>> registeredEntityMap = new HashMap<>();

    public static void init() {

    }

    public static <T extends Entity> void registerEntity(String entityName, String replacingName, EntityConstructor<T> entityConstructor, EnumCreatureType creatureType) {
        Map<String, Type<?>> types = (Map<String, Type<?>>) DataConverterRegistry.a().getSchema(DataFixUtils.makeKey(SharedConstants.getGameVersion().getWorldVersion())).findChoiceType(DataConverterTypes.ENTITY).types();
        types.put("minecraft:" + entityName, types.get("minecraft:" + replacingName));

        EntityTypes.Builder<T> a = EntityTypes.Builder.a(entityConstructor, creatureType);
        registeredEntityMap.put(entityName.toLowerCase(), IRegistry.a(IRegistry.ENTITY_TYPE, entityName, a.a(entityName)));
    }

    public static org.bukkit.entity.Entity spawnEntity(String entityName, Location location) {
        WorldServer world = ((CraftWorld) location.getWorld()).getHandle();

        EntityTypes<?> type = registeredEntityMap.get(entityName.toLowerCase());

        if (type == null) {
            throw new IllegalArgumentException("Invalid custom entity name \"" + entityName + "\"! Already registered the entity?");
        }

        net.minecraft.server.v1_16_R3.Entity nmsEntity = type.createCreature(world, null, null, null, new BlockPosition(location.getX(), location.getY(), location.getZ()), EnumMobSpawn.MOB_SUMMONED, false, false);

        if (nmsEntity != null) {
            nmsEntity.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

            world.addEntity(nmsEntity, CreatureSpawnEvent.SpawnReason.CUSTOM);

            if (nmsEntity instanceof EntityLiving) {
                ((CraftLivingEntity) nmsEntity.getBukkitEntity()).setRemoveWhenFarAway(false);
            }

            return nmsEntity.getBukkitEntity();
        } else {
            KageCore.debugMessage("Could not spawn custom entity \"" + entityName + "\".");

            return null;
        }
    }

    public interface EntityConstructor<T extends Entity> extends EntityTypes.b<T> {
        T create(EntityTypes<T> type, World world);
    }
}