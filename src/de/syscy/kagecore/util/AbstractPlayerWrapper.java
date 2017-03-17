package de.syscy.kagecore.util;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Achievement;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Particle;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.Statistic;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Villager;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.InventoryView.Property;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import com.destroystokyo.paper.Title;

import de.syscy.kagecore.versioncompat.reflect.Reflect;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;

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
	@Deprecated
	public List<Block> getLineOfSight(HashSet<Byte> blockIds, int distance) {
		return bukkitPlayer.getLineOfSight(blockIds, distance);
	}

	@Override
	public List<Block> getLineOfSight(Set<Material> materials, int distance) {
		return bukkitPlayer.getLineOfSight(materials, distance);
	}

	@Override
	@Deprecated
	public Block getTargetBlock(HashSet<Byte> blockIds, int distance) {
		return bukkitPlayer.getTargetBlock(blockIds, distance);
	}

	@Override
	public Block getTargetBlock(Set<Material> materials, int distance) {
		return bukkitPlayer.getTargetBlock(materials, distance);
	}

	@Override
	@Deprecated
	public List<Block> getLastTwoTargetBlocks(HashSet<Byte> blockIds, int distance) {
		return bukkitPlayer.getLastTwoTargetBlocks(blockIds, distance);
	}

	@Override
	public List<Block> getLastTwoTargetBlocks(Set<Material> materials, int distance) {
		return bukkitPlayer.getLastTwoTargetBlocks(materials, distance);
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
	@Deprecated
	public int _INVALID_getLastDamage() {
		return bukkitPlayer._INVALID_getLastDamage();
	}

	@Override
	public void setLastDamage(double lastDamage) {
		bukkitPlayer.setLastDamage(lastDamage);
	}

	@Override
	@Deprecated
	public void _INVALID_setLastDamage(int lastDamage) {
		bukkitPlayer._INVALID_setLastDamage(lastDamage);
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
	public void setAI(boolean ai) {
		bukkitPlayer.setAI(ai);
	}

	@Override
	public boolean hasAI() {
		return bukkitPlayer.hasAI();
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
	public AttributeInstance getAttribute(Attribute attribute) {
		return bukkitPlayer.getAttribute(attribute);
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
	@Deprecated
	public void _INVALID_damage(int amount) {
		bukkitPlayer._INVALID_damage(amount);
	}

	@Override
	public void damage(double amount, Entity source) {
		bukkitPlayer.damage(amount, source);
	}

	@Override
	@Deprecated
	public void _INVALID_damage(int amount, Entity source) {
		bukkitPlayer._INVALID_damage(amount, source);
	}

	@Override
	public double getHealth() {
		return bukkitPlayer.getHealth();
	}

	@Override
	@Deprecated
	public int _INVALID_getHealth() {
		return bukkitPlayer._INVALID_getHealth();
	}

	@Override
	public void setHealth(double health) {
		bukkitPlayer.setHealth(health);
	}

	@Override
	@Deprecated
	public void _INVALID_setHealth(int health) {
		bukkitPlayer._INVALID_setHealth(health);
	}

	@Override
	@Deprecated
	public double getMaxHealth() {
		return bukkitPlayer.getMaxHealth();
	}

	@Override
	@Deprecated
	public int _INVALID_getMaxHealth() {
		return bukkitPlayer._INVALID_getMaxHealth();
	}

	@Override
	@Deprecated
	public void setMaxHealth(double maxHealth) {
		bukkitPlayer.setMaxHealth(maxHealth);
	}

	@Override
	@Deprecated
	public void _INVALID_setMaxHealth(int maxHealth) {
		bukkitPlayer._INVALID_setMaxHealth(maxHealth);
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
	@Deprecated
	public void setBanned(boolean banned) {
		bukkitPlayer.setBanned(banned);
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
	public String getDisplayName() {
		return bukkitPlayer.getDisplayName();
	}

	@Override
	public void setDisplayName(String displayName) {
		bukkitPlayer.setDisplayName(displayName);
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
	public void sendRawMessage(String message) {
		bukkitPlayer.sendRawMessage(message);
	}

	@Override
	public void kickPlayer(String reason) {
		bukkitPlayer.kickPlayer(reason);
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
	@Deprecated
	public void sendBlockChange(Location location, int blockId, byte data) {
		bukkitPlayer.sendBlockChange(location, blockId, data);
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
	public void sendSignChange(Location location, String[] lines) throws IllegalArgumentException {
		bukkitPlayer.sendSignChange(location, lines);
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
	public void awardAchievement(Achievement achievement) {
		bukkitPlayer.awardAchievement(achievement);
	}

	@Override
	public void removeAchievement(Achievement achievement) {
		bukkitPlayer.removeAchievement(achievement);
	}

	@Override
	public boolean hasAchievement(Achievement achievement) {
		return bukkitPlayer.hasAchievement(achievement);
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
	public Location getBedSpawnLocation() {
		return bukkitPlayer.getBedSpawnLocation();
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
	public boolean getAllowFlight() {
		return bukkitPlayer.getAllowFlight();
	}

	@Override
	public void setAllowFlight(boolean allowFlight) {
		bukkitPlayer.setAllowFlight(allowFlight);
	}

	@Override
	public void hidePlayer(Player player) {
		bukkitPlayer.hidePlayer(player);
	}

	@Override
	public void showPlayer(Player player) {
		bukkitPlayer.showPlayer(player);
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
	public boolean isHandRaised() {
		return bukkitPlayer.isHandRaised();
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
	public void sendTitle(Title title) {
		bukkitPlayer.sendTitle(title);
	}

	@Override
	public void setAffectsSpawning(boolean affectsSpawning) {
		bukkitPlayer.setAffectsSpawning(affectsSpawning);
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
	public Location getOrigin() {
		return bukkitPlayer.getOrigin();
	}

	@Override
	public void setResourcePack(String arg0, byte[] arg1) {
		bukkitPlayer.setResourcePack(arg0, arg1);
	}
}