package de.syscy.kagecore.factory.entity;

import de.syscy.kagecore.KageCore;
import de.syscy.kagecore.factory.IFactory;
import de.syscy.kagecore.factory.IFactoryProviderPlugin;
import de.syscy.kagecore.factory.IFactoryTemplate;
import de.syscy.kagecore.factory.InvalidTemplateException;
import de.syscy.kagecore.util.Util;
import lombok.RequiredArgsConstructor;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
@RequiredArgsConstructor
public class EntityFactoryTemplate implements IFactoryTemplate<Entity> {
	private static Map<EntityType, SpecificEntityHandler<?>> specificEntityHandlers = new HashMap<>();

	private final EntityFactoryNMS entityFactoryNMS;

	private EntityFactory entityFactory;
	private String templateName;
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
	public void load(final IFactory<Entity> entityFactory, String templateName, final YamlConfiguration templateYaml) throws Exception {
		this.entityFactory = (EntityFactory) entityFactory;
		this.templateName = templateName;
		this.templateYaml = templateYaml;

		entityTypeID = templateYaml.getString("entityTypeID", "");
		nbt = templateYaml.getString("nbt", "");

		customName = ChatColor.translateAlternateColorCodes('$', templateYaml.getString("customName", ""));

		fireTicks = templateYaml.getInt("fireTicks", 0);
		glowing = templateYaml.getBoolean("glowing", false);
		gravity = templateYaml.getBoolean("gravity", true);
		invulnerable = templateYaml.getBoolean("invulnerable", false);
		silent = templateYaml.getBoolean("silent", false);
	}

	@Override
	public Entity create(final Object... args) throws Exception {
		final Entity entity = entityFactoryNMS.createEntity(entityTypeID, (Location) args[0], nbt);

		if(entity == null) {
			throw new InvalidTemplateException("Can't spawn entity with type \"" + entityTypeID + "\" (in template " + templateName + ".aet)");
		}

		if(!customName.isEmpty()) {
			entity.setCustomName(customName);
			entity.setCustomNameVisible(true);
		}

		entity.setFireTicks(fireTicks);
		entity.setGlowing(glowing);
		entity.setGravity(gravity);
		entity.setInvulnerable(invulnerable);
		entity.setSilent(silent);

		final SpecificEntityHandler<?> specificEntityHandler = specificEntityHandlers.get(entity.getType());

		if(specificEntityHandler != null) {
			specificEntityHandler.handleEntity(entityFactory.getPlugin(), entity, templateYaml);
		}

		entity.setMetadata("templateName", new FixedMetadataValue(KageCore.getInstance(), templateName));

		return entity;
	}

	private static abstract class SpecificEntityHandler<T> {
		public abstract void handleEntity(final IFactoryProviderPlugin plugin, final T entity, final YamlConfiguration templateYaml);

		public void handleEntity(final IFactoryProviderPlugin plugin, final Entity entity, final YamlConfiguration templateYaml) {
			handleEntity(plugin, (T) entity, templateYaml);
		}
	}

	private static class LivingEntityHandler<T extends LivingEntity> extends SpecificEntityHandler<T> {
		@Override
		public final void handleEntity(final IFactoryProviderPlugin plugin, final T livingEntity, final YamlConfiguration templateYaml) {
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
					String attributeName = attribute.name();

					if(!attributesSection.contains(attributeName)) {
						attributeName = attribute.name().toLowerCase();
					}

					if(attributesSection.contains(attributeName)) {
						AttributeInstance attributeInstance = livingEntity.getAttribute(attribute);

						if(attributeInstance == null) {
							continue;
						}

						ConfigurationSection currentAttributeSection = attributesSection.getConfigurationSection(attributeName);

						if(currentAttributeSection.contains("baseValue")) {
							attributeInstance.setBaseValue(currentAttributeSection.getDouble("baseValue"));
						}

						if(currentAttributeSection.contains("modifiers")) {
							ConfigurationSection modifiersSection = currentAttributeSection.getConfigurationSection("modifiers");

							for(String modifierName : modifiersSection.getKeys(false)) {
								try {
									String operationName = modifiersSection.getString(modifierName + ".operation");
									double amount = modifiersSection.getDouble(modifierName + ".amount");
									Operation operation = Operation.valueOf(operationName.toUpperCase());

									attributeInstance.addModifier(new AttributeModifier(modifierName, amount, operation));
								} catch(Exception ex) {
									ex.printStackTrace();
								}
							}
						}
					}
				}
			}

			if(templateYaml.contains("potionEffects")) {
				final ConfigurationSection potionEffectsSection = templateYaml.getConfigurationSection("potionEffects");

				for(final String potionEffectName : potionEffectsSection.getKeys(false)) {
					final ConfigurationSection currentPotionEffectsSection = potionEffectsSection.getConfigurationSection("potionEffectName");

					final PotionEffectType type = PotionEffectType.getByName(potionEffectName.toUpperCase());

					final int duration = currentPotionEffectsSection.getInt("duration", Integer.MAX_VALUE);
					final int amplifier = currentPotionEffectsSection.getInt("amplifier", 0);
					final boolean ambient = currentPotionEffectsSection.getBoolean("ambient", false);
					final boolean particles = currentPotionEffectsSection.getBoolean("particles", true);
					final Color color = currentPotionEffectsSection.getColor("color", null);

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
				final ConfigurationSection equipmentSection = templateYaml.getConfigurationSection("equipment");

				final EntityEquipment entityEquipment = livingEntity.getEquipment();

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

		public void handleLivingEntitySuperclass(final IFactoryProviderPlugin plugin, final T livingEntity, final YamlConfiguration templateYaml) {

		}
	}

	private static class AgeableEntityHandler<T extends Ageable> extends LivingEntityHandler<T> {
		@Override
		public void handleLivingEntitySuperclass(final IFactoryProviderPlugin plugin, final T ageableEntity, final YamlConfiguration templateYaml) {
			final String ageString = templateYaml.getString("age", "");

			if(!ageString.isEmpty()) {
				if(Util.isNumber(ageString)) {
					try {
						ageableEntity.setAge(Integer.parseInt(ageString));
					} catch(final NumberFormatException ex) {
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

		public void handleAgeableSuperclass(final IFactoryProviderPlugin plugin, final T ageableEntity, final YamlConfiguration templateYaml) {

		}
	}

	static {
		specificEntityHandlers.put(EntityType.BAT, new LivingEntityHandler<Bat>());
		specificEntityHandlers.put(EntityType.BLAZE, new LivingEntityHandler<Blaze>());
		specificEntityHandlers.put(EntityType.CAVE_SPIDER, new LivingEntityHandler<CaveSpider>());
		specificEntityHandlers.put(EntityType.CHICKEN, new AgeableEntityHandler<Chicken>());
		specificEntityHandlers.put(EntityType.COW, new AgeableEntityHandler<Cow>());
		specificEntityHandlers.put(EntityType.CREEPER, new LivingEntityHandler<Creeper>() {
			@Override
			public void handleLivingEntitySuperclass(final IFactoryProviderPlugin plugin, final Creeper creeper, final YamlConfiguration templateYaml) {
				creeper.setPowered(templateYaml.getBoolean("powered", false));
			}
		});
		specificEntityHandlers.put(EntityType.ENDER_DRAGON, new LivingEntityHandler<EnderDragon>());
		specificEntityHandlers.put(EntityType.ENDERMAN, new LivingEntityHandler<Enderman>() {
			@Override
			public void handleLivingEntitySuperclass(final IFactoryProviderPlugin plugin, final Enderman enderman, final YamlConfiguration templateYaml) {
				final String materialDataString = templateYaml.getString("carriedMaterial", "");

				if(!materialDataString.isEmpty()) {
					MaterialData carriedMaterial = null;

					if(materialDataString.contains(":")) {
						final String[] materialDataStringParts = materialDataString.split(":");

						if(materialDataStringParts.length >= 2 && Util.isNumber(materialDataStringParts[1])) {
							final Material material = Material.matchMaterial(materialDataStringParts[0]);
							final byte data = Byte.parseByte(materialDataStringParts[1]);

							carriedMaterial = new MaterialData(material, data);
						}
					} else {
						final Material material = Material.matchMaterial(materialDataString);
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
			@Override
			public void handleLivingEntitySuperclass(final IFactoryProviderPlugin plugin, final Guardian guardian, final YamlConfiguration templateYaml) {
				guardian.setElder(templateYaml.getBoolean("elderGuardian", false));
			}
		});
		specificEntityHandlers.put(EntityType.HORSE, new LivingEntityHandler<Horse>() {
			@Override
			public void handleLivingEntitySuperclass(final IFactoryProviderPlugin plugin, final Horse horse, final YamlConfiguration templateYaml) {
				try {
					final Variant horseVariant = Variant.valueOf(templateYaml.getString("horseVariant", "horse").toUpperCase());
					horse.setVariant(horseVariant);

					final org.bukkit.entity.Horse.Color horseColor = org.bukkit.entity.Horse.Color.valueOf(templateYaml.getString("horseColor", "white")
																													   .toUpperCase());
					horse.setColor(horseColor);

					final Style horseStyle = Style.valueOf(templateYaml.getString("horseStyle", "none").toUpperCase());
					horse.setStyle(horseStyle);

					horse.setCarryingChest(templateYaml.getBoolean("carryingChest", false));

					if(templateYaml.contains("horseJumpStrenght")) {
						horse.setJumpStrength(templateYaml.getDouble("horseJumpStrenght"));
					}

					horse.setTamed(templateYaml.getBoolean("tamed", false));
				} catch(final Exception ex) {

				}
			}
		});
		specificEntityHandlers.put(EntityType.IRON_GOLEM, new LivingEntityHandler<IronGolem>() {
			@Override
			public void handleLivingEntitySuperclass(final IFactoryProviderPlugin plugin, final IronGolem ironGolem, final YamlConfiguration templateYaml) {
				ironGolem.setPlayerCreated(templateYaml.getBoolean("playerCreated", false));
			}
		});
		specificEntityHandlers.put(EntityType.MAGMA_CUBE, new LivingEntityHandler<MagmaCube>() {
			@Override
			public void handleLivingEntitySuperclass(final IFactoryProviderPlugin plugin, final MagmaCube magmaCube, final YamlConfiguration templateYaml) {
				if(templateYaml.contains("size")) {
					magmaCube.setSize(templateYaml.getInt("size"));
				}
			}
		});
		specificEntityHandlers.put(EntityType.MUSHROOM_COW, new LivingEntityHandler<MushroomCow>());
		specificEntityHandlers.put(EntityType.OCELOT, new LivingEntityHandler<Ocelot>() {
			@Override
			public void handleLivingEntitySuperclass(final IFactoryProviderPlugin plugin, final Ocelot ocelot, final YamlConfiguration templateYaml) {
				final org.bukkit.entity.Ocelot.Type catType = org.bukkit.entity.Ocelot.Type.valueOf(templateYaml.getString("catType", "wild_ocelot")
																												.toUpperCase());
				ocelot.setCatType(catType);

				ocelot.setSitting(templateYaml.getBoolean("sitting", false));

				ocelot.setTamed(templateYaml.getBoolean("tamed", false));
			}
		});
		specificEntityHandlers.put(EntityType.PIG, new LivingEntityHandler<Pig>() {
			@Override
			public void handleLivingEntitySuperclass(final IFactoryProviderPlugin plugin, final Pig pig, final YamlConfiguration templateYaml) {
				pig.setSaddle(templateYaml.getBoolean("saddled", false));
			}
		});
		specificEntityHandlers.put(EntityType.PIG_ZOMBIE, new LivingEntityHandler<PigZombie>() {
			@Override
			public void handleLivingEntitySuperclass(final IFactoryProviderPlugin plugin, final PigZombie pigZombie, final YamlConfiguration templateYaml) {
				pigZombie.setAnger(templateYaml.getInt("anger", 0));
				pigZombie.setAngry(templateYaml.getBoolean("angry", false));
			}
		});
		specificEntityHandlers.put(EntityType.POLAR_BEAR, new LivingEntityHandler<PolarBear>());
		specificEntityHandlers.put(EntityType.RABBIT, new LivingEntityHandler<Rabbit>() {
			@Override
			public void handleLivingEntitySuperclass(final IFactoryProviderPlugin plugin, final Rabbit rabbit, final YamlConfiguration templateYaml) {
				final org.bukkit.entity.Rabbit.Type rabbitType = org.bukkit.entity.Rabbit.Type.valueOf(templateYaml.getString("rabbitType", "brown")
																												   .toUpperCase());
				rabbit.setRabbitType(rabbitType);
			}
		});
		specificEntityHandlers.put(EntityType.SHEEP, new LivingEntityHandler<Sheep>() {
			@Override
			public void handleLivingEntitySuperclass(final IFactoryProviderPlugin plugin, final Sheep sheep, final YamlConfiguration templateYaml) {
				sheep.setSheared(templateYaml.getBoolean("sheared", false));
			}
		});
		specificEntityHandlers.put(EntityType.SHULKER, new LivingEntityHandler<Shulker>());
		specificEntityHandlers.put(EntityType.SILVERFISH, new LivingEntityHandler<Silverfish>());
		specificEntityHandlers.put(EntityType.SKELETON, new LivingEntityHandler<Skeleton>());
		specificEntityHandlers.put(EntityType.SLIME, new LivingEntityHandler<Slime>() {
			@Override
			public void handleLivingEntitySuperclass(final IFactoryProviderPlugin plugin, final Slime slime, final YamlConfiguration templateYaml) {
				if(templateYaml.contains("size")) {
					slime.setSize(templateYaml.getInt("size"));
				}
			}
		});
		specificEntityHandlers.put(EntityType.SNOWMAN, new LivingEntityHandler<Snowman>() {
			@Override
			public void handleLivingEntitySuperclass(final IFactoryProviderPlugin plugin, final Snowman snowman, final YamlConfiguration templateYaml) {
				snowman.setDerp(templateYaml.getBoolean("derp", false));
			}
		});
		specificEntityHandlers.put(EntityType.SPIDER, new LivingEntityHandler<Spider>());
		specificEntityHandlers.put(EntityType.SQUID, new LivingEntityHandler<Squid>());
		specificEntityHandlers.put(EntityType.VILLAGER, new LivingEntityHandler<Villager>() {
			@Override
			public void handleLivingEntitySuperclass(final IFactoryProviderPlugin plugin, final Villager villager, final YamlConfiguration templateYaml) {
				handleVillager(plugin, villager, templateYaml);
			}
		});
		specificEntityHandlers.put(EntityType.WITCH, new LivingEntityHandler<Witch>());
		specificEntityHandlers.put(EntityType.WITHER, new LivingEntityHandler<Wither>());
		specificEntityHandlers.put(EntityType.WOLF, new LivingEntityHandler<Wolf>() {
			@Override
			public void handleLivingEntitySuperclass(final IFactoryProviderPlugin plugin, final Wolf wolf, final YamlConfiguration templateYaml) {
				wolf.setAngry(templateYaml.getBoolean("angry", false));
				wolf.setSitting(templateYaml.getBoolean("sitting", false));
				wolf.setTamed(templateYaml.getBoolean("tamed", false));

				final DyeColor collarColor = DyeColor.getByColor(templateYaml.getColor("collarColor", Color.RED));
				wolf.setCollarColor(collarColor);
			}
		});
		specificEntityHandlers.put(EntityType.ZOMBIE, new LivingEntityHandler<Zombie>() {
			@Override
			public void handleLivingEntitySuperclass(final IFactoryProviderPlugin plugin, final Zombie zombie, final YamlConfiguration templateYaml) {
				zombie.setBaby(templateYaml.getBoolean("baby", false));
			}
		});
		specificEntityHandlers.put(EntityType.HUSK, new LivingEntityHandler<Husk>() {
			@Override
			public void handleLivingEntitySuperclass(final IFactoryProviderPlugin plugin, final Husk husk, final YamlConfiguration templateYaml) {
				husk.setBaby(templateYaml.getBoolean("baby", false));
			}
		});
		specificEntityHandlers.put(EntityType.ZOMBIE_VILLAGER, new LivingEntityHandler<ZombieVillager>() {
			@Override
			public void handleLivingEntitySuperclass(final IFactoryProviderPlugin plugin, final ZombieVillager zombieVillager, final YamlConfiguration templateYaml) {
				zombieVillager.setBaby(templateYaml.getBoolean("baby", false));
				final Profession profession = Profession.valueOf(templateYaml.getString("profession", "normal").toUpperCase());
				zombieVillager.setVillagerProfession(profession);
			}
		});
		specificEntityHandlers.put(EntityType.STRAY, new LivingEntityHandler<Stray>());
		specificEntityHandlers.put(EntityType.VEX, new LivingEntityHandler<Vex>());
		specificEntityHandlers.put(EntityType.VINDICATOR, new LivingEntityHandler<Vindicator>());
	}

	private static void handleVillager(final IFactoryProviderPlugin plugin, final Villager villager, final YamlConfiguration templateYaml) {
		final Profession profession = Profession.valueOf(templateYaml.getString("profession", "normal").toUpperCase());
		villager.setProfession(profession);

		if(templateYaml.contains("riches")) {
			villager.setRiches(templateYaml.getInt("riches"));
		}

		if(templateYaml.contains("merchantRecipes")) {
			final List<MerchantRecipe> merchantRecipes = new ArrayList<>();

			final ConfigurationSection merchantRecipesSection = templateYaml.getConfigurationSection("merchantRecipes");
			for(final String merchantRecipeKey : merchantRecipesSection.getKeys(false)) {
				final ConfigurationSection currentMerchantRecipeSection = merchantRecipesSection.getConfigurationSection(merchantRecipeKey);

				final String resultItemStackName = currentMerchantRecipeSection.getString("result");
				final ItemStack resultItemStack = plugin.getItemStackFactory().create(resultItemStackName);
				resultItemStack.setAmount(currentMerchantRecipeSection.getInt("resultAmount", 1));

				final int uses = currentMerchantRecipeSection.getInt("uses", 0);
				final int maxUses = currentMerchantRecipeSection.getInt("maxUses", Integer.MAX_VALUE);
				final boolean experienceReward = currentMerchantRecipeSection.getBoolean("experienceReward", false);

				if(resultItemStack != null) {
					final MerchantRecipe merchantRecipe = new MerchantRecipe(resultItemStack, uses, maxUses, experienceReward);

					if(currentMerchantRecipeSection.contains("ingredient1")) {
						final String ingredientName = currentMerchantRecipeSection.getString("ingredient1");
						final ItemStack ingredientItemStack = plugin.getItemStackFactory().create(ingredientName);
						ingredientItemStack.setAmount(currentMerchantRecipeSection.getInt("ingredient1Amount", 1));

						if(ingredientItemStack != null) {
							merchantRecipe.addIngredient(ingredientItemStack);
						}
					}

					if(currentMerchantRecipeSection.contains("ingredient2")) {
						final String ingredientName = currentMerchantRecipeSection.getString("ingredient2");
						final ItemStack ingredientItemStack = plugin.getItemStackFactory().create(ingredientName);
						ingredientItemStack.setAmount(currentMerchantRecipeSection.getInt("ingredient2Amount", 1));

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