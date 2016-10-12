package de.syscy.kagecore.factory.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_10_R1.CraftWorld;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Endermite;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.PolarBear;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Shulker;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.entity.Witch;
import org.bukkit.entity.Wither;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import de.syscy.kagecore.factory.AdventureFactory;
import de.syscy.kagecore.factory.FactoryTemplate;
import de.syscy.kagecore.factory.IFactoryProviderPlugin;
import de.syscy.kagecore.util.Util;
import net.minecraft.server.v1_10_R1.BlockPosition;
import net.minecraft.server.v1_10_R1.ChunkRegionLoader;
import net.minecraft.server.v1_10_R1.EntityInsentient;
import net.minecraft.server.v1_10_R1.MojangsonParseException;
import net.minecraft.server.v1_10_R1.MojangsonParser;
import net.minecraft.server.v1_10_R1.NBTTagCompound;
import net.minecraft.server.v1_10_R1.WorldServer;

@SuppressWarnings("deprecation")
public class EntityFactoryTemplate implements FactoryTemplate<Entity> {
	private static Map<EntityType, SpecificEntityHandler<?>> specificEntityHandlers = new HashMap<>();

	private EntityFactory entityFactory;
	private YamlConfiguration templateYaml;

	private String entityTypeID;
	private String nbt;

	private String customName;

	private int fireTicks;
	private boolean glowing;
	private boolean gravity;
	private boolean invulnerable;
	private boolean silent;

	@Override
	public void load(AdventureFactory<Entity> entityFactory, YamlConfiguration templateYaml) throws Exception {
		this.entityFactory = (EntityFactory) entityFactory;
		this.templateYaml = templateYaml;

		entityTypeID = templateYaml.getString("entityTypeID", "");
		nbt = templateYaml.getString("nbt", "");

		customName = ChatColor.translateAlternateColorCodes('&', templateYaml.getString("customName", ""));

		fireTicks = templateYaml.getInt("fireTicks", 0);
		glowing = templateYaml.getBoolean("glowing", false);
		gravity = templateYaml.getBoolean("gravity", true);
		invulnerable = templateYaml.getBoolean("invulnerable", false);
		silent = templateYaml.getBoolean("invulnerable", silent);
	}

	@Override
	public Entity create(Object... args) throws Exception {
		Location location = (Location) args[0];
		WorldServer nmsWorld = ((CraftWorld) location.getWorld()).getHandle();

		net.minecraft.server.v1_10_R1.Entity nmsEntity;

		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		boolean nbtInitialized = false;

		if(!nbt.isEmpty()) {
			try {
				nbtTagCompound = MojangsonParser.parse(nbt);
				nbtInitialized = true;
			} catch(MojangsonParseException ex) {
				ex.printStackTrace();
			}
		}

		nbtTagCompound.setString("id", entityTypeID);
		nmsEntity = ChunkRegionLoader.a(nbtTagCompound, nmsWorld, location.getX(), location.getY(), location.getZ(), true);

		if(nmsEntity == null) {
			return null;
		}

		nmsEntity.setPositionRotation(location.getX(), location.getY(), location.getZ(), nmsEntity.yaw, nmsEntity.pitch);

		if(!nbtInitialized && nmsEntity instanceof EntityInsentient) {
			((EntityInsentient) nmsEntity).prepare(nmsWorld.D(new BlockPosition(nmsEntity)), null);
		}

		Entity entity = nmsEntity.getBukkitEntity();

		if(!customName.isEmpty()) {
			entity.setCustomName(customName);
			entity.setCustomNameVisible(true);
		}

		entity.setFireTicks(fireTicks);
		entity.setGlowing(glowing);
		entity.setGravity(gravity);
		entity.setInvulnerable(invulnerable);
		entity.setSilent(silent);

		SpecificEntityHandler<?> specificEntityHandler = specificEntityHandlers.get(entity.getType());

		if(specificEntityHandler != null) {
			specificEntityHandler.handleEntity(entityFactory.getPlugin(), entity, templateYaml);
		}

		return entity;
	}

	private static abstract class SpecificEntityHandler<T> {
		public abstract void handleEntity(IFactoryProviderPlugin plugin, T entity, YamlConfiguration templateYaml);

		@SuppressWarnings("unchecked")
		public void handleEntity(IFactoryProviderPlugin plugin, Entity entity, YamlConfiguration templateYaml) {
			handleEntity(plugin, (T) entity, templateYaml);
		}
	}

	private static class LivingEntityHandler<T extends LivingEntity> extends SpecificEntityHandler<T> {
		@Override
		public final void handleEntity(IFactoryProviderPlugin plugin, T livingEntity, YamlConfiguration templateYaml) {
			livingEntity.setAI(templateYaml.getBoolean("ai", true));
			livingEntity.setCanPickupItems(templateYaml.getBoolean("canPickupItems", true));
			livingEntity.setCollidable(templateYaml.getBoolean("collidable", true));

			if(templateYaml.contains("health")) {
				int health = templateYaml.getInt("health");
				livingEntity.setMaxHealth(health);
				livingEntity.setHealth(health);
			}

			livingEntity.setRemoveWhenFarAway(templateYaml.getBoolean("removeWhenFarAway", true));

			if(templateYaml.contains("attributes")) {
				ConfigurationSection attributesSection = templateYaml.getConfigurationSection("attributes");

				for(Attribute attribute : Attribute.values()) {
					if(attributesSection.contains(attribute.name().toLowerCase())) {
						AttributeInstance attributeInstance = livingEntity.getAttribute(attribute);
						ConfigurationSection currentAttributeSection = attributesSection.getConfigurationSection(attribute.name().toLowerCase());

						if(currentAttributeSection.contains("baseValue")) {
							double baseValue = currentAttributeSection.getDouble("baseValue");

							attributeInstance.setBaseValue(baseValue);
						}

						if(currentAttributeSection.contains("modifiers")) {
							ConfigurationSection modifiersSection = attributesSection.getConfigurationSection(attribute.name().toLowerCase());

							for(String modifierName : modifiersSection.getKeys(false)) {
								try {
									String operationName = modifiersSection.getString(modifierName + ".operation");
									double amount = modifiersSection.getDouble(modifierName + ".amount");
									Operation operation = Operation.valueOf(operationName.toUpperCase());

									attributeInstance.addModifier(new AttributeModifier(modifierName, amount, operation));
								} catch(Exception ex) {

								}
							}
						}
					}
				}
			}

			if(templateYaml.contains("potionEffects")) {
				ConfigurationSection potionEffectsSection = templateYaml.getConfigurationSection("potionEffects");

				for(String potionEffectName : potionEffectsSection.getKeys(false)) {
					ConfigurationSection currentPotionEffectsSection = potionEffectsSection.getConfigurationSection("potionEffectName");

					PotionEffectType type = PotionEffectType.getByName(potionEffectName.toUpperCase());

					int duration = currentPotionEffectsSection.getInt("duration", Integer.MAX_VALUE);
					int amplifier = currentPotionEffectsSection.getInt("amplifier", 1);
					boolean ambient = currentPotionEffectsSection.getBoolean("ambient", false);
					boolean particles = currentPotionEffectsSection.getBoolean("particles", true);
					Color color = currentPotionEffectsSection.getColor("color", null);

					PotionEffect potionEffect;

					if(color == null) {
						potionEffect = new PotionEffect(type, duration, amplifier, ambient, particles);
					} else {
						potionEffect = new PotionEffect(type, duration, amplifier, ambient, particles, color);
					}

					livingEntity.addPotionEffect(potionEffect, true);
				}
			}

			if(templateYaml.contains("equipment")) {
				ConfigurationSection equipmentSection = templateYaml.getConfigurationSection("equipmentSection");

				EntityEquipment entityEquipment = livingEntity.getEquipment();

				entityEquipment.setItemInMainHand(plugin.getItemStackFactory().create(equipmentSection.getString("mainHand")));
				entityEquipment.setItemInMainHandDropChance((float) equipmentSection.getDouble("mainHandDropChance", 0.0));
				entityEquipment.setItemInOffHand(plugin.getItemStackFactory().create(equipmentSection.getString("offHand")));
				entityEquipment.setItemInOffHandDropChance((float) equipmentSection.getDouble("offHandDropChance", 0.0));

				entityEquipment.setHelmet(plugin.getItemStackFactory().create(equipmentSection.getString("helmet")));
				entityEquipment.setHelmetDropChance((float) equipmentSection.getDouble("helmetDropChance", 0.0));
				entityEquipment.setChestplate(plugin.getItemStackFactory().create(equipmentSection.getString("chestplate")));
				entityEquipment.setChestplateDropChance((float) equipmentSection.getDouble("chestplateDropChance", 0.0));
				entityEquipment.setLeggings(plugin.getItemStackFactory().create(equipmentSection.getString("leggings")));
				entityEquipment.setLeggingsDropChance((float) equipmentSection.getDouble("leggingsDropChance", 0.0));
				entityEquipment.setBoots(plugin.getItemStackFactory().create(equipmentSection.getString("boots")));
				entityEquipment.setBootsDropChance((float) equipmentSection.getDouble("bootsDropChance", 0.0));
			}

			handleLivingEntitySuperclass(plugin, livingEntity, templateYaml);
		}

		public void handleLivingEntitySuperclass(IFactoryProviderPlugin plugin, T livingEntity, YamlConfiguration templateYaml) {

		}
	}

	private static class AgeableEntityHandler<T extends Ageable> extends LivingEntityHandler<T> {
		@Override
		public void handleLivingEntitySuperclass(IFactoryProviderPlugin plugin, T ageableEntity, YamlConfiguration templateYaml) {
			String ageString = templateYaml.getString("age", "");

			if(!ageString.isEmpty()) {
				if(Util.isNumber(ageString)) {
					try {
						ageableEntity.setAge(Integer.parseInt(ageString));
					} catch(NumberFormatException ex) {
						ex.printStackTrace();
					}
				} else if(ageString.equalsIgnoreCase("baby")) {
					ageableEntity.setBaby();
				} else if(ageString.equalsIgnoreCase("adult")) {
					ageableEntity.setAdult();
				}
			}

			ageableEntity.setAgeLock(templateYaml.getBoolean("ageLock", false));
			ageableEntity.setBreed(templateYaml.getBoolean("breed", true));

			handleAgeableSuperclass(plugin, ageableEntity, templateYaml);
		}

		public void handleAgeableSuperclass(IFactoryProviderPlugin plugin, T ageableEntity, YamlConfiguration templateYaml) {

		}
	}

	static {
		specificEntityHandlers.put(EntityType.BAT, new LivingEntityHandler<Bat>());
		specificEntityHandlers.put(EntityType.BLAZE, new LivingEntityHandler<Blaze>());
		specificEntityHandlers.put(EntityType.CAVE_SPIDER, new LivingEntityHandler<CaveSpider>());
		specificEntityHandlers.put(EntityType.CHICKEN, new AgeableEntityHandler<Chicken>());
		specificEntityHandlers.put(EntityType.COW, new AgeableEntityHandler<Cow>());
		specificEntityHandlers.put(EntityType.CREEPER, new LivingEntityHandler<Creeper>() {
			public void handleLivingEntitySuperclass(IFactoryProviderPlugin plugin, Creeper creeper, YamlConfiguration templateYaml) {
				creeper.setPowered(templateYaml.getBoolean("powered", false));
			}
		});
		specificEntityHandlers.put(EntityType.ENDER_DRAGON, new LivingEntityHandler<EnderDragon>());
		specificEntityHandlers.put(EntityType.ENDERMAN, new LivingEntityHandler<Enderman>() {
			public void handleLivingEntitySuperclass(IFactoryProviderPlugin plugin, Enderman enderman, YamlConfiguration templateYaml) {
				String materialDataString = templateYaml.getString("carriedMaterial", "");

				if(!materialDataString.isEmpty()) {
					MaterialData carriedMaterial = null;

					if(materialDataString.contains(":")) {
						String[] materialDataStringParts = materialDataString.split(":");

						if(materialDataStringParts.length >= 2 && Util.isNumber(materialDataStringParts[1])) {
							Material material = Material.matchMaterial(materialDataStringParts[0]);
							byte data = Byte.parseByte(materialDataStringParts[1]);

							carriedMaterial = new MaterialData(material, data);
						}
					} else {
						Material material = Material.matchMaterial(materialDataString);
						carriedMaterial = new MaterialData(material);
					}

					if(carriedMaterial != null) {
						enderman.setCarriedMaterial(carriedMaterial);
					}
				}
			}
		});
		specificEntityHandlers.put(EntityType.ENDERMITE, new LivingEntityHandler<Endermite>());
		specificEntityHandlers.put(EntityType.GHAST, new LivingEntityHandler<Ghast>());
		specificEntityHandlers.put(EntityType.GIANT, new LivingEntityHandler<Giant>());
		specificEntityHandlers.put(EntityType.GUARDIAN, new LivingEntityHandler<Guardian>() {
			public void handleLivingEntitySuperclass(IFactoryProviderPlugin plugin, Guardian guardian, YamlConfiguration templateYaml) {
				guardian.setElder(templateYaml.getBoolean("elderGuardian", false));
			}
		});
		specificEntityHandlers.put(EntityType.HORSE, new LivingEntityHandler<Horse>() {
			public void handleLivingEntitySuperclass(IFactoryProviderPlugin plugin, Horse horse, YamlConfiguration templateYaml) {
				try {
					Variant horseVariant = Variant.valueOf(templateYaml.getString("horseVariant", "horse").toUpperCase());
					horse.setVariant(horseVariant);

					org.bukkit.entity.Horse.Color horseColor = org.bukkit.entity.Horse.Color.valueOf(templateYaml.getString("horseColor", "white").toUpperCase());
					horse.setColor(horseColor);

					Style horseStyle = Style.valueOf(templateYaml.getString("horseStyle", "none").toUpperCase());
					horse.setStyle(horseStyle);

					horse.setCarryingChest(templateYaml.getBoolean("carryingChest", false));

					if(templateYaml.contains("horseJumpStrenght")) {
						horse.setJumpStrength(templateYaml.getDouble("horseJumpStrenght"));
					}

					horse.setTamed(templateYaml.getBoolean("tamed", false));
				} catch(Exception ex) {

				}
			}
		});
		specificEntityHandlers.put(EntityType.IRON_GOLEM, new LivingEntityHandler<IronGolem>() {
			public void handleLivingEntitySuperclass(IFactoryProviderPlugin plugin, IronGolem ironGolem, YamlConfiguration templateYaml) {
				ironGolem.setPlayerCreated(templateYaml.getBoolean("playerCreated", false));
			}
		});
		specificEntityHandlers.put(EntityType.MAGMA_CUBE, new LivingEntityHandler<MagmaCube>() {
			public void handleLivingEntitySuperclass(IFactoryProviderPlugin plugin, MagmaCube magmaCube, YamlConfiguration templateYaml) {
				if(templateYaml.contains("size")) {
					magmaCube.setSize(templateYaml.getInt("size"));
				}
			}
		});
		specificEntityHandlers.put(EntityType.MUSHROOM_COW, new LivingEntityHandler<MushroomCow>());
		specificEntityHandlers.put(EntityType.OCELOT, new LivingEntityHandler<Ocelot>() {
			public void handleLivingEntitySuperclass(IFactoryProviderPlugin plugin, Ocelot ocelot, YamlConfiguration templateYaml) {
				org.bukkit.entity.Ocelot.Type catType = org.bukkit.entity.Ocelot.Type.valueOf(templateYaml.getString("catType", "wild_ocelot").toUpperCase());
				ocelot.setCatType(catType);

				ocelot.setSitting(templateYaml.getBoolean("sitting", false));

				ocelot.setTamed(templateYaml.getBoolean("tamed", false));
			}
		});
		specificEntityHandlers.put(EntityType.PIG, new LivingEntityHandler<Pig>() {
			public void handleLivingEntitySuperclass(IFactoryProviderPlugin plugin, Pig pig, YamlConfiguration templateYaml) {
				pig.setSaddle(templateYaml.getBoolean("saddled", false));
			}
		});
		specificEntityHandlers.put(EntityType.PIG_ZOMBIE, new LivingEntityHandler<PigZombie>() {
			public void handleLivingEntitySuperclass(IFactoryProviderPlugin plugin, PigZombie pigZombie, YamlConfiguration templateYaml) {
				pigZombie.setAnger(templateYaml.getInt("anger", 0));
				pigZombie.setAngry(templateYaml.getBoolean("angry", false));
			}
		});
		specificEntityHandlers.put(EntityType.POLAR_BEAR, new LivingEntityHandler<PolarBear>());
		specificEntityHandlers.put(EntityType.RABBIT, new LivingEntityHandler<Rabbit>() {
			public void handleLivingEntitySuperclass(IFactoryProviderPlugin plugin, Rabbit rabbit, YamlConfiguration templateYaml) {
				org.bukkit.entity.Rabbit.Type rabbitType = org.bukkit.entity.Rabbit.Type.valueOf(templateYaml.getString("rabbitType", "brown").toUpperCase());
				rabbit.setRabbitType(rabbitType);
			}
		});
		specificEntityHandlers.put(EntityType.SHEEP, new LivingEntityHandler<Sheep>() {
			public void handleLivingEntitySuperclass(IFactoryProviderPlugin plugin, Sheep sheep, YamlConfiguration templateYaml) {
				sheep.setSheared(templateYaml.getBoolean("sheared", false));
			}
		});
		specificEntityHandlers.put(EntityType.SHULKER, new LivingEntityHandler<Shulker>());
		specificEntityHandlers.put(EntityType.SILVERFISH, new LivingEntityHandler<Silverfish>());
		specificEntityHandlers.put(EntityType.SKELETON, new LivingEntityHandler<Skeleton>() {
			public void handleLivingEntitySuperclass(IFactoryProviderPlugin plugin, Skeleton skeleton, YamlConfiguration templateYaml) {
				SkeletonType skeletonType = SkeletonType.valueOf(templateYaml.getString("skeletonType", "normal").toUpperCase());
				skeleton.setSkeletonType(skeletonType);
			}
		});
		specificEntityHandlers.put(EntityType.SLIME, new LivingEntityHandler<Slime>() {
			public void handleLivingEntitySuperclass(IFactoryProviderPlugin plugin, Slime slime, YamlConfiguration templateYaml) {
				if(templateYaml.contains("size")) {
					slime.setSize(templateYaml.getInt("size"));
				}
			}
		});
		specificEntityHandlers.put(EntityType.SNOWMAN, new LivingEntityHandler<Snowman>() {
			public void handleLivingEntitySuperclass(IFactoryProviderPlugin plugin, Snowman snowman, YamlConfiguration templateYaml) {
				snowman.setDerp(templateYaml.getBoolean("derp", false));
			}
		});
		specificEntityHandlers.put(EntityType.SPIDER, new LivingEntityHandler<Spider>());
		specificEntityHandlers.put(EntityType.SQUID, new LivingEntityHandler<Squid>());
		specificEntityHandlers.put(EntityType.VILLAGER, new LivingEntityHandler<Villager>() {
			public void handleLivingEntitySuperclass(IFactoryProviderPlugin plugin, Villager villager, YamlConfiguration templateYaml) {
				handleVillager(plugin, villager, templateYaml);
			}
		});
		specificEntityHandlers.put(EntityType.WITCH, new LivingEntityHandler<Witch>());
		specificEntityHandlers.put(EntityType.WITHER, new LivingEntityHandler<Wither>());
		specificEntityHandlers.put(EntityType.WOLF, new LivingEntityHandler<Wolf>() {
			public void handleLivingEntitySuperclass(IFactoryProviderPlugin plugin, Wolf wolf, YamlConfiguration templateYaml) {
				wolf.setAngry(templateYaml.getBoolean("angry", false));
				wolf.setSitting(templateYaml.getBoolean("sitting", false));
				wolf.setTamed(templateYaml.getBoolean("tamed", false));

				DyeColor collarColor = DyeColor.getByColor(templateYaml.getColor("collarColor", Color.RED));
				wolf.setCollarColor(collarColor);
			}
		});
		specificEntityHandlers.put(EntityType.ZOMBIE, new LivingEntityHandler<Zombie>() {
			public void handleLivingEntitySuperclass(IFactoryProviderPlugin plugin, Zombie zombie, YamlConfiguration templateYaml) {
				zombie.setBaby(templateYaml.getBoolean("baby", false));
				zombie.setVillager(templateYaml.getBoolean("villager", false));

				Profession profession = Profession.valueOf(templateYaml.getString("profession", "normal").toUpperCase());
				zombie.setVillagerProfession(profession);
			}
		});
	}

	private static void handleVillager(IFactoryProviderPlugin plugin, Villager villager, YamlConfiguration templateYaml) {
		Profession profession = Profession.valueOf(templateYaml.getString("profession", "normal").toUpperCase());
		villager.setProfession(profession);

		if(templateYaml.contains("riches")) {
			villager.setRiches(templateYaml.getInt("riches"));
		}

		if(templateYaml.contains("merchantRecipes")) {
			List<MerchantRecipe> merchantRecipes = new ArrayList<>();

			ConfigurationSection merchantRecipesSection = templateYaml.getConfigurationSection("merchantRecipes");
			for(String merchantRecipeKey : merchantRecipesSection.getKeys(false)) {
				ConfigurationSection currentMerchantRecipeSection = merchantRecipesSection.getConfigurationSection(merchantRecipeKey);

				String resultItemStackName = currentMerchantRecipeSection.getString("result");
				ItemStack resultItemStack = plugin.getItemStackFactory().create(resultItemStackName);
				int uses = currentMerchantRecipeSection.getInt("uses", 0);
				int maxUses = currentMerchantRecipeSection.getInt("maxUses", Integer.MAX_VALUE);
				boolean experienceReward = currentMerchantRecipeSection.getBoolean("experienceReward", false);

				if(resultItemStack != null) {
					MerchantRecipe merchantRecipe = new MerchantRecipe(resultItemStack, uses, maxUses, experienceReward);

					if(currentMerchantRecipeSection.contains("ingredient1")) {
						String ingredientName = currentMerchantRecipeSection.getString("ingredient1");
						ItemStack ingredientItemStack = plugin.getItemStackFactory().create(ingredientName);

						if(ingredientItemStack != null) {
							merchantRecipe.addIngredient(ingredientItemStack);
						}
					}

					if(currentMerchantRecipeSection.contains("ingredient2")) {
						String ingredientName = currentMerchantRecipeSection.getString("ingredient2");
						ItemStack ingredientItemStack = plugin.getItemStackFactory().create(ingredientName);

						if(ingredientItemStack != null) {
							merchantRecipe.addIngredient(ingredientItemStack);
						}
					}

					merchantRecipes.add(merchantRecipe);
				}
			}

			if(!merchantRecipes.isEmpty()) {
				villager.setRecipes(merchantRecipes);
			}
		}
	}
}