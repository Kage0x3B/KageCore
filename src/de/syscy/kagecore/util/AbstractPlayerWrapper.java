package de.syscy.kagecore.util;

import com.destroystokyo.paper.ClientOption;
import com.destroystokyo.paper.Title;
import com.destroystokyo.paper.block.TargetBlockInfo;
import com.destroystokyo.paper.entity.TargetEntityInfo;
import com.destroystokyo.paper.profile.PlayerProfile;
import de.syscy.kagecore.versioncompat.reflect.Reflect;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.entity.*;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.*;
import org.bukkit.inventory.InventoryView.Property;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.net.InetSocketAddress;
import java.util.*;

@SuppressWarnings("deprecation")
public abstract class AbstractPlayerWrapper implements Player {
	private final @Getter Player bukkitPlayer;
	private final @Getter Reflect playerReflect;

	public AbstractPlayerWrapper(Player bukkitPlayer) {
		this.bukkitPlayer = bukkitPlayer;
		playerReflect = Reflect.on(bukkitPlayer);
	}

	@Override
	public int hashCode() {
		return bukkitPlayer.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		return bukkitPlayer.equals(object);
	}

	public Object getHandle() {
		return playerReflect.call("getHandle").get();
	}

	@Override
	public String getName() {
		return bukkitPlayer.getName();
	}

	@Override
	public PlayerInventory getInventory() {
		return bukkitPlayer.getInventory();
	}

	@Override
	public Inventory getEnderChest() {
		return bukkitPlayer.getEnderChest();
	}

	@Override
	public MainHand getMainHand() {
		return bukkitPlayer.getMainHand();
	}

	@Override
	public boolean setWindowProperty(Property property, int value) {
		return bukkitPlayer.setWindowProperty(property, value);
	}

	@Override
	public InventoryView getOpenInventory() {
		return bukkitPlayer.getOpenInventory();
	}

	@Override
	public InventoryView openInventory(Inventory inventory) {
		return bukkitPlayer.openInventory(inventory);
	}

	@Override
	public InventoryView openWorkbench(Location location, boolean var2) {
		return bukkitPlayer.openWorkbench(location, var2);
	}

	@Override
	public InventoryView openEnchanting(Location location, boolean var2) {
		return bukkitPlayer.openEnchanting(location, var2);
	}

	@Override
	public void openInventory(InventoryView inventoryView) {
		bukkitPlayer.openInventory(inventoryView);
	}

	@Override
	public InventoryView openMerchant(Villager villager, boolean var2) {
		return bukkitPlayer.openMerchant(villager, var2);
	}

	@Override
	public void closeInventory() {
		bukkitPlayer.closeInventory();
	}

	@Override
	public void closeInventory(InventoryCloseEvent.Reason reason) {
		bukkitPlayer.closeInventory(reason);
	}

	@Override
	@Deprecated
	public ItemStack getItemInHand() {
		return bukkitPlayer.getItemInHand();
	}

	@Override
	@Deprecated
	public void setItemInHand(ItemStack itemStack) {
		bukkitPlayer.setItemInHand(itemStack);
	}

	@Override
	public ItemStack getItemOnCursor() {
		return bukkitPlayer.getItemOnCursor();
	}

	@Override
	public void setItemOnCursor(ItemStack itemStack) {
		bukkitPlayer.setItemOnCursor(itemStack);
	}

	@Override
	public boolean isSleeping() {
		return bukkitPlayer.isSleeping();
	}

	@Override
	public int getSleepTicks() {
		return bukkitPlayer.getSleepTicks();
	}

	@Override
	public Location getPotentialBedLocation() {
		return bukkitPlayer.getPotentialBedLocation();
	}

	@Override
	public GameMode getGameMode() {
		return bukkitPlayer.getGameMode();
	}

	@Override
	public void setGameMode(GameMode gameMode) {
		bukkitPlayer.setGameMode(gameMode);
	}

	@Override
	public boolean isBlocking() {
		return bukkitPlayer.isBlocking();
	}

	@Override
	public int getExpToLevel() {
		return bukkitPlayer.getExpToLevel();
	}

	@Override
	public double getEyeHeight() {
		return bukkitPlayer.getEyeHeight();
	}

	@Override
	public double getEyeHeight(boolean var1) {
		return bukkitPlayer.getEyeHeight(var1);
	}

	@Override
	public Location getEyeLocation() {
		return bukkitPlayer.getEyeLocation();
	}

	@Override
	public List<Block> getLineOfSight(Set<Material> materials, int distance) {
		return bukkitPlayer.getLineOfSight(materials, distance);
	}

	@Override
	public Block getTargetBlock(Set<Material> materials, int distance) {
		return bukkitPlayer.getTargetBlock(materials, distance);
	}

	@Override
	public Block getTargetBlock(int i, TargetBlockInfo.FluidMode fluidMode) {
		return bukkitPlayer.getTargetBlock(i, fluidMode);
	}

	@Override
	public BlockFace getTargetBlockFace(int i, TargetBlockInfo.FluidMode fluidMode) {
		return bukkitPlayer.getTargetBlockFace(i, fluidMode);
	}

	@Override
	public TargetBlockInfo getTargetBlockInfo(int i, TargetBlockInfo.FluidMode fluidMode) {
		return bukkitPlayer.getTargetBlockInfo(i, fluidMode);
	}

	@Override
	public Entity getTargetEntity(int i, boolean b) {
		return bukkitPlayer.getTargetEntity(i, b);
	}

	@Override
	public TargetEntityInfo getTargetEntityInfo(int i, boolean b) {
		return bukkitPlayer.getTargetEntityInfo(i, b);
	}

	@Override
	public List<Block> getLastTwoTargetBlocks(Set<Material> materials, int distance) {
		return bukkitPlayer.getLastTwoTargetBlocks(materials, distance);
	}

	@Override
	public Block getTargetBlockExact(int i) {
		return bukkitPlayer.getTargetBlockExact(i);
	}

	@Override
	public Block getTargetBlockExact(int i, FluidCollisionMode fluidCollisionMode) {
		return bukkitPlayer.getTargetBlockExact(i, fluidCollisionMode);
	}

	@Override
	public RayTraceResult rayTraceBlocks(double v) {
		return bukkitPlayer.rayTraceBlocks(v);
	}

	@Override
	public RayTraceResult rayTraceBlocks(double v, FluidCollisionMode fluidCollisionMode) {
		return bukkitPlayer.rayTraceBlocks(v, fluidCollisionMode);
	}

	@Override
	public int getRemainingAir() {
		return bukkitPlayer.getRemainingAir();
	}

	@Override
	public void setRemainingAir(int remainingAir) {
		bukkitPlayer.setRemainingAir(remainingAir);
	}

	@Override
	public int getMaximumAir() {
		return bukkitPlayer.getMaximumAir();
	}

	@Override
	public void setMaximumAir(int maximumAir) {
		bukkitPlayer.setMaximumAir(maximumAir);
	}

	@Override
	public int getArrowCooldown() {
		return bukkitPlayer.getArrowCooldown();
	}

	@Override
	public void setArrowCooldown(int i) {
		bukkitPlayer.setArrowCooldown(i);
	}

	@Override
	public int getArrowsInBody() {
		return bukkitPlayer.getArrowsInBody();
	}

	@Override
	public void setArrowsInBody(int i) {
		bukkitPlayer.setArrowsInBody(i);
	}

	@Override
	public int getMaximumNoDamageTicks() {
		return bukkitPlayer.getMaximumNoDamageTicks();
	}

	@Override
	public void setMaximumNoDamageTicks(int maximumNoDamageTicks) {
		bukkitPlayer.setMaximumNoDamageTicks(maximumNoDamageTicks);
	}

	@Override
	public double getLastDamage() {
		return bukkitPlayer.getLastDamage();
	}

	@Override
	public void setLastDamage(double lastDamage) {
		bukkitPlayer.setLastDamage(lastDamage);
	}

	@Override
	public int getNoDamageTicks() {
		return bukkitPlayer.getNoDamageTicks();
	}

	@Override
	public void setNoDamageTicks(int noDamageTicks) {
		bukkitPlayer.setNoDamageTicks(noDamageTicks);
	}

	@Override
	public Player getKiller() {
		return bukkitPlayer.getKiller();
	}

	@Override
	public void setKiller(Player player) {
		bukkitPlayer.setKiller(player);
	}

	@Override
	public boolean addPotionEffect(PotionEffect potionEffect) {
		return bukkitPlayer.addPotionEffect(potionEffect);
	}

	@Override
	public boolean addPotionEffect(PotionEffect potionEffect, boolean var2) {
		return bukkitPlayer.addPotionEffect(potionEffect, var2);
	}

	@Override
	public boolean addPotionEffects(Collection<PotionEffect> potionEffects) {
		return bukkitPlayer.addPotionEffects(potionEffects);
	}

	@Override
	public boolean hasPotionEffect(PotionEffectType potionEffectType) {
		return bukkitPlayer.hasPotionEffect(potionEffectType);
	}

	@Override
	public PotionEffect getPotionEffect(PotionEffectType potionEffectType) {
		return bukkitPlayer.getPotionEffect(potionEffectType);
	}

	@Override
	public void removePotionEffect(PotionEffectType potionEffectType) {
		bukkitPlayer.removePotionEffect(potionEffectType);
	}

	@Override
	public Collection<PotionEffect> getActivePotionEffects() {
		return bukkitPlayer.getActivePotionEffects();
	}

	@Override
	public boolean hasLineOfSight(Entity entity) {
		return bukkitPlayer.hasLineOfSight(entity);
	}

	@Override
	public boolean hasLineOfSight(Location location) {
		return bukkitPlayer.hasLineOfSight(location);
	}

	@Override
	public boolean getRemoveWhenFarAway() {
		return bukkitPlayer.getRemoveWhenFarAway();
	}

	@Override
	public void setRemoveWhenFarAway(boolean removeWhenFarAway) {
		bukkitPlayer.setRemoveWhenFarAway(removeWhenFarAway);
	}

	@Override
	public EntityEquipment getEquipment() {
		return bukkitPlayer.getEquipment();
	}

	@Override
	public void setCanPickupItems(boolean canPickupItems) {
		bukkitPlayer.setCanPickupItems(canPickupItems);
	}

	@Override
	public boolean getCanPickupItems() {
		return bukkitPlayer.getCanPickupItems();
	}

	@Override
	public boolean isLeashed() {
		return bukkitPlayer.isLeashed();
	}

	@Override
	public Entity getLeashHolder() throws IllegalStateException {
		return bukkitPlayer.getLeashHolder();
	}

	@Override
	public boolean setLeashHolder(Entity entity) {
		return bukkitPlayer.setLeashHolder(entity);
	}

	@Override
	public boolean isGliding() {
		return bukkitPlayer.isGliding();
	}

	@Override
	public void setGliding(boolean gliding) {
		bukkitPlayer.setGliding(gliding);
	}

	@Override
	public boolean isSwimming() {
		return bukkitPlayer.isSwimming();
	}

	@Override
	public void setSwimming(boolean b) {
		bukkitPlayer.setSwimming(b);
	}

	@Override
	public boolean isRiptiding() {
		return bukkitPlayer.isRiptiding();
	}

	@Override
	public void setAI(boolean ai) {
		bukkitPlayer.setAI(ai);
	}

	@Override
	public boolean hasAI() {
		return bukkitPlayer.hasAI();
	}

	@Override
	public void attack(Entity entity) {
		bukkitPlayer.attack(entity);
	}

	@Override
	public void swingMainHand() {
		bukkitPlayer.swingMainHand();
	}

	@Override
	public void swingOffHand() {
		bukkitPlayer.swingOffHand();
	}

	@Override
	public void setCollidable(boolean collidable) {
		bukkitPlayer.setCollidable(collidable);
	}

	@Override
	public boolean isCollidable() {
		return bukkitPlayer.isCollidable();
	}

	@Override
	public Set<UUID> getCollidableExemptions() {
		return bukkitPlayer.getCollidableExemptions();
	}

	@Override
	public <T> T getMemory(MemoryKey<T> memoryKey) {
		return bukkitPlayer.getMemory(memoryKey);
	}

	@Override
	public <T> void setMemory(MemoryKey<T> memoryKey, T t) {
		bukkitPlayer.setMemory(memoryKey, t);
	}

	@Override
	public EntityCategory getCategory() {
		return bukkitPlayer.getCategory();
	}

	@Override
	public void setInvisible(boolean b) {
		bukkitPlayer.setInvisible(b);
	}

	@Override
	public boolean isInvisible() {
		return bukkitPlayer.isInvisible();
	}

	@Override
	public AttributeInstance getAttribute(Attribute attribute) {
		return bukkitPlayer.getAttribute(attribute);
	}

	@Override
	public void registerAttribute(Attribute attribute) {
		bukkitPlayer.registerAttribute(attribute);
	}

	@Override
	public Location getLocation() {
		return bukkitPlayer.getLocation();
	}

	@Override
	public Location getLocation(Location location) {
		return bukkitPlayer.getLocation(location);
	}

	@Override
	public void setVelocity(Vector velocity) {
		bukkitPlayer.setVelocity(velocity);
	}

	@Override
	public Vector getVelocity() {
		return bukkitPlayer.getVelocity();
	}

	@Override
	public World getWorld() {
		return bukkitPlayer.getWorld();
	}

	@Override
	public void setRotation(float v, float v1) {
		bukkitPlayer.setRotation(v, v1);
	}

	@Override
	public boolean teleport(Location location) {
		return bukkitPlayer.teleport(location);
	}

	@Override
	public boolean teleport(Location location, TeleportCause teleportCause) {
		return bukkitPlayer.teleport(location, teleportCause);
	}

	@Override
	public boolean teleport(Entity entity) {
		return bukkitPlayer.teleport(entity);
	}

	@Override
	public boolean teleport(Entity entity, TeleportCause teleportCause) {
		return bukkitPlayer.teleport(entity, teleportCause);
	}

	@Override
	public List<Entity> getNearbyEntities(double radiusX, double radiusY, double radiusZ) {
		return bukkitPlayer.getNearbyEntities(radiusX, radiusY, radiusZ);
	}

	@Override
	public int getEntityId() {
		return bukkitPlayer.getEntityId();
	}

	@Override
	public int getFireTicks() {
		return bukkitPlayer.getFireTicks();
	}

	@Override
	public int getMaxFireTicks() {
		return bukkitPlayer.getMaxFireTicks();
	}

	@Override
	public void setFireTicks(int fireTicks) {
		bukkitPlayer.setFireTicks(fireTicks);
	}

	@Override
	public void remove() {
		bukkitPlayer.remove();
	}

	@Override
	public boolean isDead() {
		return bukkitPlayer.isDead();
	}

	@Override
	public boolean isValid() {
		return bukkitPlayer.isValid();
	}

	@Override
	public Server getServer() {
		return bukkitPlayer.getServer();
	}

	@Override
	public boolean isPersistent() {
		return bukkitPlayer.isPersistent();
	}

	@Override
	public void setPersistent(boolean b) {
		bukkitPlayer.setPersistent(b);
	}

	@Override
	@Deprecated
	public Entity getPassenger() {
		return bukkitPlayer.getPassenger();
	}

	@Override
	@Deprecated
	public boolean setPassenger(Entity passenger) {
		return bukkitPlayer.setPassenger(passenger);
	}

	@Override
	public boolean isEmpty() {
		return bukkitPlayer.isEmpty();
	}

	@Override
	public boolean eject() {
		return bukkitPlayer.eject();
	}

	@Override
	public float getFallDistance() {
		return bukkitPlayer.getFallDistance();
	}

	@Override
	public void setFallDistance(float fallDistance) {
		bukkitPlayer.setFallDistance(fallDistance);
	}

	@Override
	public void setLastDamageCause(EntityDamageEvent entityDamageEvent) {
		bukkitPlayer.setLastDamageCause(entityDamageEvent);
	}

	@Override
	public EntityDamageEvent getLastDamageCause() {
		return bukkitPlayer.getLastDamageCause();
	}

	@Override
	public UUID getUniqueId() {
		return bukkitPlayer.getUniqueId();
	}

	@Override
	public int getTicksLived() {
		return bukkitPlayer.getTicksLived();
	}

	@Override
	public void setTicksLived(int ticksLived) {
		bukkitPlayer.setTicksLived(ticksLived);
	}

	@Override
	public void playEffect(EntityEffect effect) {
		bukkitPlayer.playEffect(effect);
	}

	@Override
	public EntityType getType() {
		return bukkitPlayer.getType();
	}

	@Override
	public boolean isInsideVehicle() {
		return bukkitPlayer.isInsideVehicle();
	}

	@Override
	public boolean leaveVehicle() {
		return bukkitPlayer.leaveVehicle();
	}

	@Override
	public Entity getVehicle() {
		return bukkitPlayer.getVehicle();
	}

	@Override
	public void setCustomName(String customName) {
		bukkitPlayer.setCustomName(customName);
	}

	@Override
	public Component customName() {
		return bukkitPlayer.customName();
	}

	@Override
	public void customName(Component component) {
		bukkitPlayer.customName(component);
	}

	@Override
	public String getCustomName() {
		return bukkitPlayer.getCustomName();
	}

	@Override
	public void setCustomNameVisible(boolean customNameVisible) {
		bukkitPlayer.setCustomNameVisible(customNameVisible);
	}

	@Override
	public boolean isCustomNameVisible() {
		return bukkitPlayer.isCustomNameVisible();
	}

	@Override
	public void setGlowing(boolean glowing) {
		bukkitPlayer.setGlowing(glowing);
	}

	@Override
	public boolean isGlowing() {
		return bukkitPlayer.isGlowing();
	}

	@Override
	public void setInvulnerable(boolean invulnerable) {
		bukkitPlayer.setInvulnerable(invulnerable);
	}

	@Override
	public boolean isInvulnerable() {
		return bukkitPlayer.isInvulnerable();
	}

	@Override
	public boolean isSilent() {
		return bukkitPlayer.isSilent();
	}

	@Override
	public void setSilent(boolean silent) {
		bukkitPlayer.setSilent(silent);
	}

	@Override
	public boolean hasGravity() {
		return bukkitPlayer.hasGravity();
	}

	@Override
	public void setGravity(boolean gravity) {
		bukkitPlayer.setGravity(gravity);
	}

	@Override
	public void setMetadata(String key, MetadataValue value) {
		bukkitPlayer.setMetadata(key, value);
	}

	@Override
	public List<MetadataValue> getMetadata(String key) {
		return bukkitPlayer.getMetadata(key);
	}

	@Override
	public boolean hasMetadata(String key) {
		return bukkitPlayer.hasMetadata(key);
	}

	@Override
	public void removeMetadata(String key, Plugin plugin) {
		bukkitPlayer.removeMetadata(key, plugin);
	}

	@Override
	public void sendMessage(String message) {
		bukkitPlayer.sendMessage(message);
	}

	@Override
	public void sendMessage(String[] message) {
		bukkitPlayer.sendMessage(message);
	}

	@Override
	public void sendMessage(UUID uuid, String s) {
		bukkitPlayer.sendMessage(uuid, s);
	}

	@Override
	public void sendMessage(UUID uuid, String[] strings) {
		bukkitPlayer.sendMessage(uuid, strings);
	}

	@Override
	public boolean isPermissionSet(String permission) {
		return bukkitPlayer.isPermissionSet(permission);
	}

	@Override
	public boolean isPermissionSet(Permission permission) {
		return bukkitPlayer.isPermissionSet(permission);
	}

	@Override
	public boolean hasPermission(String permission) {
		return bukkitPlayer.hasPermission(permission);
	}

	@Override
	public boolean hasPermission(Permission permission) {
		return bukkitPlayer.hasPermission(permission);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin) {
		return bukkitPlayer.addAttachment(plugin);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, int var2) {
		return bukkitPlayer.addAttachment(plugin, var2);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String permission, boolean var3) {
		return bukkitPlayer.addAttachment(plugin, permission, var3);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String permission, boolean var3, int var4) {
		return bukkitPlayer.addAttachment(plugin, permission, var3, var4);
	}

	@Override
	public void removeAttachment(PermissionAttachment attachment) {
		bukkitPlayer.removeAttachment(attachment);
	}

	@Override
	public void recalculatePermissions() {
		bukkitPlayer.recalculatePermissions();
	}

	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		return bukkitPlayer.getEffectivePermissions();
	}

	@Override
	public boolean isOp() {
		return bukkitPlayer.isOp();
	}

	@Override
	public void setOp(boolean op) {
		bukkitPlayer.setOp(op);
	}

	@Override
	public void damage(double amount) {
		bukkitPlayer.damage(amount);
	}

	@Override
	public void damage(double amount, Entity source) {
		bukkitPlayer.damage(amount, source);
	}

	@Override
	public double getHealth() {
		return bukkitPlayer.getHealth();
	}

	@Override
	public void setHealth(double health) {
		bukkitPlayer.setHealth(health);
	}

	@Override
	public double getAbsorptionAmount() {
		return bukkitPlayer.getAbsorptionAmount();
	}

	@Override
	public void setAbsorptionAmount(double v) {
		bukkitPlayer.setAbsorptionAmount(v);
	}

	@Override
	@Deprecated
	public double getMaxHealth() {
		return bukkitPlayer.getMaxHealth();
	}

	@Override
	@Deprecated
	public void setMaxHealth(double maxHealth) {
		bukkitPlayer.setMaxHealth(maxHealth);
	}

	@Override
	@Deprecated
	public void resetMaxHealth() {
		bukkitPlayer.resetMaxHealth();
	}

	@Override
	public <T extends Projectile> T launchProjectile(Class<? extends T> projectileClass) {
		return bukkitPlayer.launchProjectile(projectileClass);
	}

	@Override
	public <T extends Projectile> T launchProjectile(Class<? extends T> projectileClass, Vector direction) {
		return bukkitPlayer.launchProjectile(projectileClass, direction);
	}

	@Override
	public boolean isConversing() {
		return bukkitPlayer.isConversing();
	}

	@Override
	public void acceptConversationInput(String conversationInput) {
		bukkitPlayer.acceptConversationInput(conversationInput);
	}

	@Override
	public boolean beginConversation(Conversation conversation) {
		return bukkitPlayer.beginConversation(conversation);
	}

	@Override
	public void abandonConversation(Conversation conversation) {
		bukkitPlayer.abandonConversation(conversation);
	}

	@Override
	public void abandonConversation(Conversation conversation, ConversationAbandonedEvent conversationAbandonedEvent) {
		bukkitPlayer.abandonConversation(conversation, conversationAbandonedEvent);
	}

	@Override
	public boolean isOnline() {
		return bukkitPlayer.isOnline();
	}

	@Override
	public boolean isBanned() {
		return bukkitPlayer.isBanned();
	}

	@Override
	public boolean isWhitelisted() {
		return bukkitPlayer.isWhitelisted();
	}

	@Override
	public void setWhitelisted(boolean whitelisted) {
		bukkitPlayer.setWhitelisted(whitelisted);
	}

	@Override
	public Player getPlayer() {
		return bukkitPlayer;
	}

	@Override
	public long getFirstPlayed() {
		return bukkitPlayer.getFirstPlayed();
	}

	@Override
	public long getLastPlayed() {
		return bukkitPlayer.getLastPlayed();
	}

	@Override
	public boolean hasPlayedBefore() {
		return bukkitPlayer.hasPlayedBefore();
	}

	@Override
	public Map<String, Object> serialize() {
		return bukkitPlayer.serialize();
	}

	@Override
	public void sendPluginMessage(Plugin plugin, String channel, byte[] data) {
		bukkitPlayer.sendPluginMessage(plugin, channel, data);
	}

	@Override
	public Set<String> getListeningPluginChannels() {
		return bukkitPlayer.getListeningPluginChannels();
	}

	@Override
	public Component displayName() {
		return bukkitPlayer.displayName();
	}

	@Override
	public void displayName(Component component) {
		bukkitPlayer.displayName(component);
	}

	@Override
	public String getDisplayName() {
		return bukkitPlayer.getDisplayName();
	}

	@Override
	public void setDisplayName(String displayName) {
		bukkitPlayer.setDisplayName(displayName);
	}

	@Override
	public void playerListName(Component component) {
		bukkitPlayer.playerListName(component);
	}

	@Override
	public Component playerListName() {
		return bukkitPlayer.playerListName();
	}

	@Override
	public Component playerListHeader() {
		return bukkitPlayer.playerListHeader();
	}

	@Override
	public Component playerListFooter() {
		return bukkitPlayer.playerListFooter();
	}

	@Override
	public String getPlayerListName() {
		return bukkitPlayer.getPlayerListName();
	}

	@Override
	public void setPlayerListName(String playerListName) {
		bukkitPlayer.setPlayerListName(playerListName);
	}

	@Override
	public String getPlayerListHeader() {
		return bukkitPlayer.getPlayerListHeader();
	}

	@Override
	public String getPlayerListFooter() {
		return bukkitPlayer.getPlayerListFooter();
	}

	@Override
	public void setPlayerListHeader(String s) {
		bukkitPlayer.setPlayerListHeader(s);
	}

	@Override
	public void setPlayerListFooter(String s) {
		bukkitPlayer.setPlayerListFooter(s);
	}

	@Override
	public void setPlayerListHeaderFooter(String s, String s1) {
		bukkitPlayer.setPlayerListHeaderFooter(s, s1);
	}

	@Override
	public void setCompassTarget(Location compassTarget) {
		bukkitPlayer.setCompassTarget(compassTarget);
	}

	@Override
	public Location getCompassTarget() {
		return bukkitPlayer.getCompassTarget();
	}

	@Override
	public InetSocketAddress getAddress() {
		return bukkitPlayer.getAddress();
	}

	@Override
	public int getProtocolVersion() {
		return bukkitPlayer.getProtocolVersion();
	}

	@Override
	public InetSocketAddress getVirtualHost() {
		return bukkitPlayer.getVirtualHost();
	}

	@Override
	public void sendRawMessage(String message) {
		bukkitPlayer.sendRawMessage(message);
	}

	@Override
	public void sendRawMessage(UUID uuid, String s) {
		bukkitPlayer.sendRawMessage(uuid, s);
	}

	@Override
	public void kickPlayer(String reason) {
		bukkitPlayer.kickPlayer(reason);
	}

	@Override
	public void kick(Component component) {
		bukkitPlayer.kick(component);
	}

	@Override
	public void kick(Component component, PlayerKickEvent.Cause cause) {
		bukkitPlayer.kick(component, cause);
	}

	@Override
	public void chat(String message) {
		bukkitPlayer.chat(message);
	}

	@Override
	public boolean performCommand(String command) {
		return bukkitPlayer.performCommand(command);
	}

	@Override
	public boolean isSneaking() {
		return bukkitPlayer.isSneaking();
	}

	@Override
	public void setSneaking(boolean sneaking) {
		bukkitPlayer.setSneaking(sneaking);
	}

	@Override
	public boolean isSprinting() {
		return bukkitPlayer.isSprinting();
	}

	@Override
	public void setSprinting(boolean sprinting) {
		bukkitPlayer.setSprinting(sprinting);
	}

	@Override
	public void saveData() {
		bukkitPlayer.saveData();
	}

	@Override
	public void loadData() {
		bukkitPlayer.loadData();
	}

	@Override
	public void setSleepingIgnored(boolean sleepIgnored) {
		bukkitPlayer.setSleepingIgnored(sleepIgnored);
	}

	@Override
	public boolean isSleepingIgnored() {
		return bukkitPlayer.isSleepingIgnored();
	}

	@Override
	@Deprecated
	public void playNote(Location location, byte instrumentId, byte noteId) {
		bukkitPlayer.playNote(location, instrumentId, noteId);
	}

	@Override
	public void playNote(Location location, Instrument instrument, Note note) {
		bukkitPlayer.playNote(location, instrument, note);
	}

	@Override
	public void playSound(Location location, Sound sound, float var3, float var4) {
		bukkitPlayer.playSound(location, sound, var3, var4);
	}

	@Override
	public void playSound(Location location, String soundName, float var3, float var4) {
		bukkitPlayer.playSound(location, soundName, var3, var4);
	}

	@Override
	public void stopSound(Sound sound) {
		bukkitPlayer.stopSound(sound);
	}

	@Override
	public void stopSound(String soundName) {
		bukkitPlayer.stopSound(soundName);
	}

	@Override
	@Deprecated
	public void playEffect(Location location, Effect effect, int var3) {
		bukkitPlayer.playEffect(location, effect, var3);
	}

	@Override
	public <T> void playEffect(Location location, Effect effect, T var3) {
		bukkitPlayer.playEffect(location, effect, var3);
	}

	@Override
	public void sendBlockChange(Location location, BlockData blockData) {
		bukkitPlayer.sendBlockChange(location, blockData);
	}

	@Override
	public void sendBlockDamage(Location location, float v) {
		bukkitPlayer.sendBlockDamage(location, v);
	}

	@Override
	@Deprecated
	public void sendBlockChange(Location location, Material material, byte data) {
		bukkitPlayer.sendBlockChange(location, material, data);
	}

	@Override
	@Deprecated
	public boolean sendChunkChange(Location location, int var2, int var3, int var4, byte[] var5) {
		return bukkitPlayer.sendChunkChange(location, var2, var3, var4, var5);
	}

	@Override
	public void sendSignChange(Location location, List<Component> list) throws IllegalArgumentException {
		bukkitPlayer.sendSignChange(location, list);
	}

	@Override
	public void sendSignChange(Location location, List<Component> list, DyeColor dyeColor) throws IllegalArgumentException {
		bukkitPlayer.sendSignChange(location, list, dyeColor);
	}

	@Override
	public void sendSignChange(Location location, String[] lines) throws IllegalArgumentException {
		bukkitPlayer.sendSignChange(location, lines);
	}

	@Override
	public void sendSignChange(Location location, String[] strings, DyeColor dyeColor) throws IllegalArgumentException {
		bukkitPlayer.sendSignChange(location, strings, dyeColor);
	}

	@Override
	public void sendMap(MapView mapView) {
		bukkitPlayer.sendMap(mapView);
	}

	@Override
	public void updateInventory() {
		bukkitPlayer.updateInventory();
	}

	@Override
	public void incrementStatistic(Statistic statistic) throws IllegalArgumentException {
		bukkitPlayer.incrementStatistic(statistic);
	}

	@Override
	public void incrementStatistic(Statistic statistic, int amount) throws IllegalArgumentException {
		bukkitPlayer.incrementStatistic(statistic, amount);
	}

	@Override
	public void incrementStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
		bukkitPlayer.incrementStatistic(statistic, material);
	}

	@Override
	public void incrementStatistic(Statistic statistic, Material material, int amount) throws IllegalArgumentException {
		bukkitPlayer.incrementStatistic(statistic, material, amount);

	}

	@Override
	public void incrementStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
		bukkitPlayer.incrementStatistic(statistic, entityType);

	}

	@Override
	public void incrementStatistic(Statistic statistic, EntityType entityType, int amount) throws IllegalArgumentException {
		bukkitPlayer.incrementStatistic(statistic, entityType, amount);

	}

	@Override
	public void decrementStatistic(Statistic statistic) throws IllegalArgumentException {
		bukkitPlayer.decrementStatistic(statistic);
	}

	@Override
	public void decrementStatistic(Statistic statistic, int amount) throws IllegalArgumentException {
		bukkitPlayer.decrementStatistic(statistic, amount);
	}

	@Override
	public void decrementStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
		bukkitPlayer.decrementStatistic(statistic, material);

	}

	@Override
	public void decrementStatistic(Statistic statistic, Material material, int amount) throws IllegalArgumentException {
		bukkitPlayer.decrementStatistic(statistic, material, amount);

	}

	@Override
	public void decrementStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
		bukkitPlayer.decrementStatistic(statistic, entityType);

	}

	@Override
	public void decrementStatistic(Statistic statistic, EntityType entityType, int amount) {
		bukkitPlayer.decrementStatistic(statistic, entityType, amount);

	}

	@Override
	public int getStatistic(Statistic statistic) throws IllegalArgumentException {
		return bukkitPlayer.getStatistic(statistic);
	}

	@Override
	public int getStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
		return bukkitPlayer.getStatistic(statistic, material);
	}

	@Override
	public int getStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
		return bukkitPlayer.getStatistic(statistic, entityType);
	}

	@Override
	public void setStatistic(Statistic statistic, int amount) throws IllegalArgumentException {
		bukkitPlayer.setStatistic(statistic, amount);
	}

	@Override
	public void setStatistic(Statistic statistic, Material material, int amount) throws IllegalArgumentException {
		bukkitPlayer.setStatistic(statistic, material, amount);
	}

	@Override
	public void setStatistic(Statistic statistic, EntityType entityType, int amount) {
		bukkitPlayer.setStatistic(statistic, entityType, amount);
	}

	@Override
	public void setPlayerTime(long time, boolean var3) {
		bukkitPlayer.setPlayerTime(time, var3);
	}

	@Override
	public long getPlayerTime() {
		return bukkitPlayer.getPlayerTime();
	}

	@Override
	public long getPlayerTimeOffset() {
		return bukkitPlayer.getPlayerTimeOffset();
	}

	@Override
	public boolean isPlayerTimeRelative() {
		return bukkitPlayer.isPlayerTimeRelative();
	}

	@Override
	public void resetPlayerTime() {
		bukkitPlayer.resetPlayerTime();
	}

	@Override
	public void setPlayerWeather(WeatherType weather) {
		bukkitPlayer.setPlayerWeather(weather);
	}

	@Override
	public WeatherType getPlayerWeather() {
		return bukkitPlayer.getPlayerWeather();
	}

	@Override
	public void resetPlayerWeather() {
		bukkitPlayer.resetPlayerWeather();
	}

	@Override
	public void giveExp(int exp) {
		bukkitPlayer.giveExp(exp);
	}

	@Override
	public void giveExp(int i, boolean b) {
		bukkitPlayer.giveExp(i, b);
	}

	@Override
	public int applyMending(int i) {
		return bukkitPlayer.applyMending(i);
	}

	@Override
	public void giveExpLevels(int levels) {
		bukkitPlayer.giveExpLevels(levels);
	}

	@Override
	public float getExp() {
		return bukkitPlayer.getExp();
	}

	@Override
	public void setExp(float exp) {
		bukkitPlayer.setExp(exp);
	}

	@Override
	public int getLevel() {
		return bukkitPlayer.getLevel();
	}

	@Override
	public void setLevel(int level) {
		bukkitPlayer.setLevel(level);
	}

	@Override
	public int getTotalExperience() {
		return bukkitPlayer.getTotalExperience();
	}

	@Override
	public void setTotalExperience(int totalExperience) {
		bukkitPlayer.setTotalExperience(totalExperience);
	}

	@Override
	public void sendExperienceChange(float v) {
		bukkitPlayer.sendExperienceChange(v);
	}

	@Override
	public void sendExperienceChange(float v, int i) {
		bukkitPlayer.sendExperienceChange(v, i);
	}

	@Override
	public float getExhaustion() {
		return bukkitPlayer.getExhaustion();
	}

	@Override
	public void setExhaustion(float exhaustion) {
		bukkitPlayer.setExhaustion(exhaustion);
	}

	@Override
	public float getSaturation() {
		return bukkitPlayer.getSaturation();
	}

	@Override
	public void setSaturation(float saturation) {
		bukkitPlayer.setSaturation(saturation);
	}

	@Override
	public int getFoodLevel() {
		return bukkitPlayer.getFoodLevel();
	}

	@Override
	public void setFoodLevel(int foodLevel) {
		bukkitPlayer.setFoodLevel(foodLevel);
	}

	@Override
	public int getSaturatedRegenRate() {
		return bukkitPlayer.getSaturatedRegenRate();
	}

	@Override
	public void setSaturatedRegenRate(int i) {
		bukkitPlayer.setSaturatedRegenRate(i);
	}

	@Override
	public int getUnsaturatedRegenRate() {
		return bukkitPlayer.getUnsaturatedRegenRate();
	}

	@Override
	public void setUnsaturatedRegenRate(int i) {
		bukkitPlayer.setUnsaturatedRegenRate(i);
	}

	@Override
	public int getStarvationRate() {
		return bukkitPlayer.getStarvationRate();
	}

	@Override
	public void setStarvationRate(int i) {
		bukkitPlayer.setStarvationRate(i);
	}

	@Override
	public Location getBedSpawnLocation() {
		return bukkitPlayer.getBedSpawnLocation();
	}

	@Override
	public long getLastLogin() {
		return bukkitPlayer.getLastLogin();
	}

	@Override
	public long getLastSeen() {
		return bukkitPlayer.getLastSeen();
	}

	@Override
	public void setBedSpawnLocation(Location location) {
		bukkitPlayer.setBedSpawnLocation(location);
	}

	@Override
	public void setBedSpawnLocation(Location location, boolean var2) {
		bukkitPlayer.setBedSpawnLocation(location, var2);
	}

	@Override
	public boolean sleep(Location location, boolean b) {
		return bukkitPlayer.sleep(location, b);
	}

	@Override
	public void wakeup(boolean b) {
		bukkitPlayer.wakeup(b);
	}

	@Override
	public Location getBedLocation() {
		return bukkitPlayer.getBedLocation();
	}

	@Override
	public boolean getAllowFlight() {
		return bukkitPlayer.getAllowFlight();
	}

	@Override
	public void setAllowFlight(boolean allowFlight) {
		bukkitPlayer.setAllowFlight(allowFlight);
	}

	@Override
	public void hidePlayer(Player player) {
		bukkitPlayer.hidePlayer(toBukkitPlayer(player));
	}

	@Override
	public void hidePlayer(Plugin plugin, Player player) {
		bukkitPlayer.hidePlayer(plugin, player);
	}

	@Override
	public void showPlayer(Player player) {
		bukkitPlayer.showPlayer(toBukkitPlayer(player));
	}

	@Override
	public void showPlayer(Plugin plugin, Player player) {
		bukkitPlayer.showPlayer(plugin, player);
	}

	@Override
	public boolean canSee(Player player) {
		return bukkitPlayer.canSee(player);
	}

	@Override
	@Deprecated
	public boolean isOnGround() {
		return bukkitPlayer.isOnGround();
	}

	@Override
	public boolean isInWater() {
		return bukkitPlayer.isInWater();
	}

	@Override
	public boolean isFlying() {
		return bukkitPlayer.isFlying();
	}

	@Override
	public void setFlying(boolean flying) {
		bukkitPlayer.setFlying(flying);
	}

	@Override
	public void setFlySpeed(float flySpeed) throws IllegalArgumentException {
		bukkitPlayer.setFlySpeed(flySpeed);
	}

	@Override
	public void setWalkSpeed(float walkSpeed) throws IllegalArgumentException {
		bukkitPlayer.setWalkSpeed(walkSpeed);
	}

	@Override
	public float getFlySpeed() {
		return bukkitPlayer.getFlySpeed();
	}

	@Override
	public float getWalkSpeed() {
		return bukkitPlayer.getWalkSpeed();
	}

	@Override
	@Deprecated
	public void setTexturePack(String texturePack) {
		bukkitPlayer.setTexturePack(texturePack);
	}

	@Override
	@Deprecated
	public void setResourcePack(String resourcePack) {
		bukkitPlayer.setResourcePack(resourcePack);
	}

	@Override
	public Scoreboard getScoreboard() {
		return bukkitPlayer.getScoreboard();
	}

	@Override
	public void setScoreboard(Scoreboard scoreboard) throws IllegalArgumentException, IllegalStateException {
		bukkitPlayer.setScoreboard(scoreboard);
	}

	@Override
	public boolean isHealthScaled() {
		return bukkitPlayer.isHealthScaled();
	}

	@Override
	public void setHealthScaled(boolean healthScaled) {
		bukkitPlayer.setHealthScaled(healthScaled);
	}

	@Override
	public void setHealthScale(double healthScale) throws IllegalArgumentException {
		bukkitPlayer.setHealthScale(healthScale);
	}

	@Override
	public double getHealthScale() {
		return bukkitPlayer.getHealthScale();
	}

	@Override
	public Entity getSpectatorTarget() {
		return bukkitPlayer.getSpectatorTarget();
	}

	@Override
	public void setSpectatorTarget(Entity spectatorTarget) {
		bukkitPlayer.setSpectatorTarget(spectatorTarget);
	}

	@Override
	@Deprecated
	public void sendTitle(String title, String subtitle) {
		bukkitPlayer.sendTitle(title, subtitle);
	}

	@Override
	@Deprecated
	public void resetTitle() {
		bukkitPlayer.resetTitle();
	}

	@Override
	public void spawnParticle(Particle particle, Location location, int var3) {
		bukkitPlayer.spawnParticle(particle, location, var3);
	}

	@Override
	public void spawnParticle(Particle particle, double x, double y, double z, int var8) {
		bukkitPlayer.spawnParticle(particle, x, y, z, var8);
	}

	@Override
	public <T> void spawnParticle(Particle particle, Location location, int var3, T var4) {
		bukkitPlayer.spawnParticle(particle, location, var3, var4);
	}

	@Override
	public <T> void spawnParticle(Particle particle, double x, double y, double z, int var8, T var9) {
		bukkitPlayer.spawnParticle(particle, x, y, z, var8, var9);
	}

	@Override
	public void spawnParticle(Particle particle, Location location, int var3, double var4, double var6, double var8) {
		bukkitPlayer.spawnParticle(particle, location, var3, var4, var6, var8);
	}

	@Override
	public void spawnParticle(Particle particle, double x, double y, double z, int var8, double var9, double var11, double var13) {
		bukkitPlayer.spawnParticle(particle, x, y, z, var8, var9, var11, var13);
	}

	@Override
	public <T> void spawnParticle(Particle particle, Location location, int var3, double var4, double var6, double var8, T var10) {
		bukkitPlayer.spawnParticle(particle, location, var3, var4, var6, var8, var10);
	}

	@Override
	public <T> void spawnParticle(Particle particle, double x, double y, double z, int var8, double var9, double var11, double var13, T var15) {
		bukkitPlayer.spawnParticle(particle, x, y, z, var8, var9, var11, var13, var15);
	}

	@Override
	public void spawnParticle(Particle particle, Location location, int var3, double var4, double var6, double var8, double var10) {
		bukkitPlayer.spawnParticle(particle, location, var3, var4, var6, var8);
	}

	@Override
	public void spawnParticle(Particle particle, double x, double y, double z, int var8, double var9, double var11, double var13, double var15) {
		bukkitPlayer.spawnParticle(particle, x, y, z, var8, var9, var11, var13, var15);
	}

	@Override
	public <T> void spawnParticle(Particle particle, Location location, int var3, double var4, double var6, double var8, double var10, T var12) {
		bukkitPlayer.spawnParticle(particle, location, var3, var4, var6, var8, var10, var12);
	}

	@Override
	public <T> void spawnParticle(Particle particle, double x, double y, double z, int var8, double var9, double var11, double var13, double var15, T var17) {
		bukkitPlayer.spawnParticle(particle, x, y, z, var8, var9, var11, var13, var15, var17);
	}

	@Override
	public Spigot spigot() {
		return bukkitPlayer.spigot();
	}

	@Override
	public boolean isAfk() {
		return bukkitPlayer.isAfk();
	}

	@Override
	public void setAfk(boolean afk) {
		bukkitPlayer.setAfk(afk);
	}

	@Override
	public void resetIdleTimer() {
		bukkitPlayer.resetIdleTimer();
	}

	@Override
	public boolean isSpawnInvulnerable() {
		return bukkitPlayer.isSpawnInvulnerable();
	}

	@Override
	public int getSpawnInvulnerableTicks() {
		return bukkitPlayer.getSpawnInvulnerableTicks();
	}

	@Override
	public void setSpawnInvulnerableTicks(int ticks) {
		bukkitPlayer.setSpawnInvulnerableTicks(ticks);
	}

	@Override
	public void setFlyingFallDamage(boolean b) {
		bukkitPlayer.setFlyingFallDamage(b);
	}

	@Override
	public boolean hasFlyingFallDamage() {
		return bukkitPlayer.hasFlyingFallDamage();
	}

	@Override
	public boolean isHandRaised() {
		return bukkitPlayer.isHandRaised();
	}

	@Override
	public EquipmentSlot getHandRaised() {
		return bukkitPlayer.getHandRaised();
	}

	@Override
	public boolean isJumping() {
		return bukkitPlayer.isJumping();
	}

	@Override
	public void setJumping(boolean jumping) {
		bukkitPlayer.setJumping(jumping);
	}

	@Override
	public void playPickupItemAnimation(Item item, int i) {
		bukkitPlayer.playPickupItemAnimation(item, i);
	}

	@Override
	public float getHurtDirection() {
		return bukkitPlayer.getHurtDirection();
	}

	@Override
	public void setHurtDirection(float v) {
		bukkitPlayer.setHurtDirection(v);
	}

	@Override
	public float getSafeFallDistance() {
		return bukkitPlayer.getSafeFallDistance();
	}

	@Override
	public void setSafeFallDistance(float v) {
		bukkitPlayer.setSafeFallDistance(v);
	}

	@Override
	public void broadcastItemBreak(EquipmentSlot equipmentSlot) {
		bukkitPlayer.broadcastItemBreak(equipmentSlot);
	}

	@Override
	public boolean shouldBurnInDay() {
		return bukkitPlayer.shouldBurnInDay();
	}

	@Override
	public void setShouldBurnInDay(boolean b) {
		bukkitPlayer.setShouldBurnInDay(b);
	}

	@Override
	public int getPortalCooldown() {
		return bukkitPlayer.getPortalCooldown();
	}

	@Override
	public void setPortalCooldown(int portalCooldown) {
		bukkitPlayer.setPortalCooldown(portalCooldown);
	}

	@Override
	public Set<String> getScoreboardTags() {
		return bukkitPlayer.getScoreboardTags();
	}

	@Override
	public boolean addScoreboardTag(String scoreboardTag) {
		return bukkitPlayer.addScoreboardTag(scoreboardTag);
	}

	@Override
	public boolean removeScoreboardTag(String scoreboardTag) {
		return bukkitPlayer.removeScoreboardTag(scoreboardTag);
	}

	@Override
	public void playSound(Location location, Sound sound, SoundCategory category, float arg3, float arg4) {
		bukkitPlayer.playSound(location, sound, category, arg3, arg4);
	}

	@Override
	public void playSound(Location location, String soundName, SoundCategory category, float arg3, float arg4) {
		bukkitPlayer.playSound(location, soundName, category, arg3, arg4);
	}

	@Override
	public void sendTitle(String title, String subtitle, int arg2, int arg3, int arg4) {
		bukkitPlayer.sendTitle(title, subtitle, arg2, arg3, arg4);
	}

	@Override
	public void stopSound(Sound sound, SoundCategory category) {
		bukkitPlayer.stopSound(sound, category);
	}

	@Override
	public void stopSound(String soundName, SoundCategory category) {
		bukkitPlayer.stopSound(soundName, category);
	}

	@Override
	public InventoryView openMerchant(Merchant merchant, boolean arg1) {
		return bukkitPlayer.openMerchant(merchant, arg1);
	}

	@Override
	public InventoryView openAnvil(Location location, boolean b) {
		return bukkitPlayer.openAnvil(location, b);
	}

	@Override
	public InventoryView openCartographyTable(Location location, boolean b) {
		return bukkitPlayer.openCartographyTable(location, b);
	}

	@Override
	public InventoryView openGrindstone(Location location, boolean b) {
		return bukkitPlayer.openGrindstone(location, b);
	}

	@Override
	public InventoryView openLoom(Location location, boolean b) {
		return bukkitPlayer.openLoom(location, b);
	}

	@Override
	public InventoryView openSmithingTable(Location location, boolean b) {
		return bukkitPlayer.openSmithingTable(location, b);
	}

	@Override
	public InventoryView openStonecutter(Location location, boolean b) {
		return bukkitPlayer.openStonecutter(location, b);
	}

	@Override
	public boolean addPassenger(Entity entity) {
		return bukkitPlayer.addPassenger(entity);
	}

	@Override
	public List<Entity> getPassengers() {
		return bukkitPlayer.getPassengers();
	}

	@Override
	public boolean removePassenger(Entity entity) {
		return bukkitPlayer.removePassenger(entity);
	}

	@Override
	public boolean getAffectsSpawning() {
		return bukkitPlayer.getAffectsSpawning();
	}

	@Override
	public String getResourcePackHash() {
		return bukkitPlayer.getResourcePackHash();
	}

	@Override
	public Status getResourcePackStatus() {
		return bukkitPlayer.getResourcePackStatus();
	}

	@Override
	public int getViewDistance() {
		return bukkitPlayer.getViewDistance();
	}

	@Override
	public boolean hasResourcePack() {
		return bukkitPlayer.hasResourcePack();
	}

	@Override
	public PlayerProfile getPlayerProfile() {
		return bukkitPlayer.getPlayerProfile();
	}

	@Override
	public void setPlayerProfile(PlayerProfile playerProfile) {
		bukkitPlayer.setPlayerProfile(playerProfile);
	}

	@Override
	public float getCooldownPeriod() {
		return bukkitPlayer.getCooldownPeriod();
	}

	@Override
	public float getCooledAttackStrength(float v) {
		return bukkitPlayer.getCooledAttackStrength(v);
	}

	@Override
	public void resetCooldown() {
		bukkitPlayer.resetCooldown();
	}

	@Override
	public <T> T getClientOption(ClientOption<T> clientOption) {
		return bukkitPlayer.getClientOption(clientOption);
	}

	@Override
	public Firework boostElytra(ItemStack itemStack) {
		return bukkitPlayer.boostElytra(itemStack);
	}

	@Override
	public void sendOpLevel(byte b) {
		bukkitPlayer.sendOpLevel(b);
	}

	@Override
	public Set<Player> getTrackedPlayers() {
		return bukkitPlayer.getTrackedPlayers();
	}

	@Override
	public String getClientBrandName() {
		return bukkitPlayer.getClientBrandName();
	}

	@Override
	public void hideTitle() {
		bukkitPlayer.hideTitle();
	}

	@Override
	public void sendActionBar(String text) {
		bukkitPlayer.sendActionBar(text);
	}

	@Override
	public void sendActionBar(char c, String text) {
		bukkitPlayer.sendActionBar(c, text);
	}

	@Override
	public void sendActionBar(BaseComponent... baseComponents) {
		bukkitPlayer.sendActionBar(baseComponents);
	}

	@Override
	public void sendTitle(Title title) {
		bukkitPlayer.sendTitle(title);
	}

	@Override
	public void setAffectsSpawning(boolean affectsSpawning) {
		bukkitPlayer.setAffectsSpawning(affectsSpawning);
	}

	@Override
	public void updateCommands() {
		bukkitPlayer.updateCommands();
	}

	@Override
	public void openBook(ItemStack itemStack) {
		bukkitPlayer.openBook(itemStack);
	}

	@Override
	public void setPlayerListHeaderFooter(BaseComponent[] header, BaseComponent[] footer) {
		bukkitPlayer.setPlayerListHeaderFooter(header, footer);
	}

	@Override
	public void setPlayerListHeaderFooter(BaseComponent header, BaseComponent footer) {
		bukkitPlayer.setPlayerListHeaderFooter(header, footer);
	}

	@Override
	public void setResourcePack(String arg0, String arg1) {
		bukkitPlayer.setResourcePack(arg0, arg1);
	}

	@Override
	@Deprecated
	public void setSubtitle(BaseComponent[] subtitle) {
		bukkitPlayer.setSubtitle(subtitle);
	}

	@Override
	@Deprecated
	public void setSubtitle(BaseComponent subtitle) {
		bukkitPlayer.setSubtitle(subtitle);
	}

	@Override
	@Deprecated
	public void setTitleTimes(int fadeInDuration, int stayDuration, int fadeOutDuration) {
		bukkitPlayer.setTitleTimes(fadeInDuration, stayDuration, fadeOutDuration);
	}

	@Override
	public void setViewDistance(int viewDistance) {
		bukkitPlayer.setViewDistance(viewDistance);
	}

	@Override
	public int getNoTickViewDistance() {
		return bukkitPlayer.getNoTickViewDistance();
	}

	@Override
	public void setNoTickViewDistance(int i) {
		bukkitPlayer.setNoTickViewDistance(i);
	}

	@Override
	public int getSendViewDistance() {
		return bukkitPlayer.getSendViewDistance();
	}

	@Override
	public void setSendViewDistance(int i) {
		bukkitPlayer.setSendViewDistance(i);
	}

	@Override
	@Deprecated
	public void showTitle(BaseComponent[] title) {
		bukkitPlayer.showTitle(title);
	}

	@Override
	@Deprecated
	public void showTitle(BaseComponent title) {
		bukkitPlayer.showTitle(title);
	}

	@Override
	@Deprecated
	public void showTitle(BaseComponent[] title, BaseComponent[] subtitle, int fadeInDuration, int stayDuration, int fadeOutDuration) {
		bukkitPlayer.showTitle(title, subtitle, fadeInDuration, stayDuration, fadeOutDuration);
	}

	@Override
	@Deprecated
	public void showTitle(BaseComponent title, BaseComponent subtitle, int fadeInDuration, int stayDuration, int fadeOutDuration) {
		bukkitPlayer.showTitle(title, subtitle, fadeInDuration, stayDuration, fadeOutDuration);
	}

	@Override
	public void updateTitle(Title title) {
		bukkitPlayer.updateTitle(title);
	}

	@Override
	public int getArrowsStuck() {
		return bukkitPlayer.getArrowsStuck();
	}

	@Override
	public void setArrowsStuck(int amount) {
		bukkitPlayer.setArrowsStuck(amount);
	}

	@Override
	public int getShieldBlockingDelay() {
		return bukkitPlayer.getShieldBlockingDelay();
	}

	@Override
	public void setShieldBlockingDelay(int i) {
		bukkitPlayer.setShieldBlockingDelay(i);
	}

	@Override
	public ItemStack getActiveItem() {
		return bukkitPlayer.getActiveItem();
	}

	@Override
	public void clearActiveItem() {
		bukkitPlayer.clearActiveItem();
	}

	@Override
	public int getItemUseRemainingTime() {
		return bukkitPlayer.getItemUseRemainingTime();
	}

	@Override
	public int getHandRaisedTime() {
		return bukkitPlayer.getHandRaisedTime();
	}

	@Override
	public Location getOrigin() {
		return bukkitPlayer.getOrigin();
	}

	@Override
	public void setResourcePack(String arg0, byte[] arg1) {
		bukkitPlayer.setResourcePack(arg0, arg1);
	}

	@Override
	public double getHeight() {
		return bukkitPlayer.getHeight();
	}

	@Override
	public double getWidth() {
		return bukkitPlayer.getWidth();
	}

	@Override
	public BoundingBox getBoundingBox() {
		return bukkitPlayer.getBoundingBox();
	}

	@Override
	public void setCooldown(Material material, int cooldown) {
		bukkitPlayer.setCooldown(material, cooldown);
	}

	@Override
	public boolean isDeeplySleeping() {
		return bukkitPlayer.isDeeplySleeping();
	}

	@Override
	public boolean fromMobSpawner() {
		return bukkitPlayer.fromMobSpawner();
	}

	@Override
	public Chunk getChunk() {
		return bukkitPlayer.getChunk();
	}

	@Override
	public CreatureSpawnEvent.SpawnReason getEntitySpawnReason() {
		return bukkitPlayer.getEntitySpawnReason();
	}

	@Override
	public boolean isInRain() {
		return bukkitPlayer.isInRain();
	}

	@Override
	public boolean isInBubbleColumn() {
		return bukkitPlayer.isInBubbleColumn();
	}

	@Override
	public boolean isInWaterOrRain() {
		return bukkitPlayer.isInWaterOrRain();
	}

	@Override
	public boolean isInWaterOrBubbleColumn() {
		return bukkitPlayer.isInWaterOrBubbleColumn();
	}

	@Override
	public boolean isInWaterOrRainOrBubbleColumn() {
		return bukkitPlayer.isInWaterOrRainOrBubbleColumn();
	}

	@Override
	public boolean isInLava() {
		return bukkitPlayer.isInLava();
	}

	@Override
	public boolean isTicking() {
		return bukkitPlayer.isTicking();
	}

	@Override
	public Player getRider() {
		return bukkitPlayer.getRider();
	}

	@Override
	public boolean hasRider() {
		return bukkitPlayer.hasRider();
	}

	@Override
	public boolean isRidable() {
		return bukkitPlayer.isRidable();
	}

	@Override
	public boolean isRidableInWater() {
		return bukkitPlayer.isRidableInWater();
	}

	@Override
	public boolean spawnAt(Location location, CreatureSpawnEvent.SpawnReason spawnReason) {
		return bukkitPlayer.spawnAt(location, spawnReason);
	}

	@Override
	public boolean isInDaylight() {
		return bukkitPlayer.isInDaylight();
	}

	@Override
	public AdvancementProgress getAdvancementProgress(Advancement advancement) {
		return bukkitPlayer.getAdvancementProgress(advancement);
	}

	@Override
	public int getClientViewDistance() {
		return bukkitPlayer.getClientViewDistance();
	}

	@Override
	public Locale locale() {
		return bukkitPlayer.locale();
	}

	@Override
	public int getPing() {
		return bukkitPlayer.getPing();
	}

	@Override
	public int getCooldown(Material material) {
		return bukkitPlayer.getCooldown(material);
	}

	@Override
	public String getLocale() {
		return bukkitPlayer.getLocale();
	}

	@Override
	public PistonMoveReaction getPistonMoveReaction() {
		return bukkitPlayer.getPistonMoveReaction();
	}

	@Override
	public BlockFace getFacing() {
		return bukkitPlayer.getFacing();
	}

	@Override
	public Pose getPose() {
		return bukkitPlayer.getPose();
	}

	@Override
	@Deprecated
	public void setShoulderEntityLeft(Entity entity) {
		bukkitPlayer.setShoulderEntityLeft(entity);
	}

	@Override
	@Deprecated
	public void setShoulderEntityRight(Entity entity) {
		bukkitPlayer.setShoulderEntityRight(entity);
	}

	@Override
	public void openSign(Sign sign) {
		bukkitPlayer.openSign(sign);
	}

	@Override
	public boolean dropItem(boolean b) {
		return bukkitPlayer.dropItem(b);
	}

	@Override
	@Deprecated
	public Entity getShoulderEntityLeft() {
		return bukkitPlayer.getShoulderEntityLeft();
	}

	@Override
	@Deprecated
	public Entity getShoulderEntityRight() {
		return bukkitPlayer.getShoulderEntityRight();
	}

	@Override
	public boolean hasCooldown(Material material) {
		return bukkitPlayer.hasCooldown(material);
	}

	@Override
	public Entity releaseLeftShoulderEntity() {
		return bukkitPlayer.releaseLeftShoulderEntity();
	}

	@Override
	public Entity releaseRightShoulderEntity() {
		return bukkitPlayer.releaseRightShoulderEntity();
	}

	@Override
	public float getAttackCooldown() {
		return bukkitPlayer.getAttackCooldown();
	}

	@Override
	public boolean discoverRecipe(NamespacedKey namespacedKey) {
		return bukkitPlayer.discoverRecipe(namespacedKey);
	}

	@Override
	public int discoverRecipes(Collection<NamespacedKey> collection) {
		return bukkitPlayer.discoverRecipes(collection);
	}

	@Override
	public boolean undiscoverRecipe(NamespacedKey namespacedKey) {
		return bukkitPlayer.undiscoverRecipe(namespacedKey);
	}

	@Override
	public int undiscoverRecipes(Collection<NamespacedKey> collection) {
		return bukkitPlayer.undiscoverRecipes(collection);
	}

	@Override
	public boolean hasDiscoveredRecipe(NamespacedKey namespacedKey) {
		return bukkitPlayer.hasDiscoveredRecipe(namespacedKey);
	}

	@Override
	public Set<NamespacedKey> getDiscoveredRecipes() {
		return bukkitPlayer.getDiscoveredRecipes();
	}

	@Override
	public PersistentDataContainer getPersistentDataContainer() {
		return bukkitPlayer.getPersistentDataContainer();
	}

	public static Player toBukkitPlayer(Player player) {
		return player instanceof AbstractPlayerWrapper ? ((AbstractPlayerWrapper) player).bukkitPlayer : player;
	}
}