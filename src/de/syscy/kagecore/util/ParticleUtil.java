package de.syscy.kagecore.util;

import de.syscy.kagecore.KageCore;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

public class ParticleUtil {
	private final static int DEFAULT_RADIUS = 128;
	private static final int PARTICLES_PER_CUBE_LINE = 4;

	public static void drawBoundingBox(BoundingBox bb, Color color, List<Player> players) {
		Location min = bb.getMin();
		Location max = bb.getMax();

		World world = min.getWorld();

		int x = min.getBlockX();
		int y = min.getBlockY();
		int z = min.getBlockZ();

		int x2 = max.getBlockX() + 1;
		int y2 = max.getBlockY() + 1;
		int z2 = max.getBlockZ() + 1;

		int particleAmount = PARTICLES_PER_CUBE_LINE * Math.max(x2 - x, Math.max(y2 - y, z2 - z));

		drawLine(world, x, y, z, x2, y, z, particleAmount, color, players);
		drawLine(world, x, y, z, x, y2, z, particleAmount, color, players);
		drawLine(world, x, y, z, x, y, z2, particleAmount, color, players);
		drawLine(world, x2, y, z, x2, y2, z, particleAmount, color, players);
		drawLine(world, x2, y, z, x2, y, z2, particleAmount, color, players);
		drawLine(world, x, y2, z, x2, y2, z, particleAmount, color, players);
		drawLine(world, x, y2, z, x, y2, z2, particleAmount, color, players);
		drawLine(world, x, y, z2, x2, y, z2, particleAmount, color, players);
		drawLine(world, x, y, z2, x, y2, z2, particleAmount, color, players);
		drawLine(world, x2, y, z2, x2, y2, z2, particleAmount, color, players);
		drawLine(world, x2, y2, z, x2, y2, z2, particleAmount, color, players);
		drawLine(world, x, y2, z2, x2, y2, z2, particleAmount, color, players);
	}

	public static void drawCube(Location cubeLocation, Color color, List<Player> players) {
		World world = cubeLocation.getWorld();

		int x = cubeLocation.getBlockX();
		int y = cubeLocation.getBlockY();
		int z = cubeLocation.getBlockZ();

		drawLine(world, x, y, z, x + 1, y, z, PARTICLES_PER_CUBE_LINE, color, players);
		drawLine(world, x, y, z, x, y + 1, z, PARTICLES_PER_CUBE_LINE, color, players);
		drawLine(world, x, y, z, x, y, z + 1, PARTICLES_PER_CUBE_LINE, color, players);
		drawLine(world, x + 1, y, z, x + 1, y + 1, z, PARTICLES_PER_CUBE_LINE, color, players);
		drawLine(world, x + 1, y, z, x + 1, y, z + 1, PARTICLES_PER_CUBE_LINE, color, players);
		drawLine(world, x, y + 1, z, x + 1, y + 1, z, PARTICLES_PER_CUBE_LINE, color, players);
		drawLine(world, x, y + 1, z, x, y + 1, z + 1, PARTICLES_PER_CUBE_LINE, color, players);
		drawLine(world, x, y, z + 1, x, y + 1, z + 1, PARTICLES_PER_CUBE_LINE, color, players);
		drawLine(world, x, y, z + 1, x + 1, y, z + 1, PARTICLES_PER_CUBE_LINE, color, players);
		drawLine(world, x, y + 1, z + 1, x + 1, y + 1, z + 1, PARTICLES_PER_CUBE_LINE, color, players);
		drawLine(world, x + 1, y + 1, z, x + 1, y + 1, z + 1, PARTICLES_PER_CUBE_LINE, color, players);
		drawLine(world, x + 1, y, z + 1, x + 1, y + 1, z + 1, PARTICLES_PER_CUBE_LINE, color, players);
	}

	public static void drawLine(World world, int x, int y, int z, int x2, int y2, int z2, int particleAmount, Color color, List<Player> players) {
		Vector v1 = new Vector(x, y, z);
		Vector v2 = new Vector(x2, y2, z2);

		Vector difference = v2.subtract(v1);

		double increment = 1.0 / ((double) particleAmount + 1.0);

		double dx = difference.getX() * increment;
		double dy = difference.getY() * increment;
		double dz = difference.getZ() * increment;

		Location loc = new Location(world, x, y, z);

		for(int i = 0; i < particleAmount; i++) {
			Particle.REDSTONE.builder().receivers(players).location(loc).color(color).spawn();

			loc.add(dx, dy, dz);
		}
	}

	public static void playHelix(final Location loc, final float i, final Particle particle) {
		BukkitRunnable runnable = new BukkitRunnable() {
			double radius = 0;
			double step;
			double y = loc.getY();
			Location location = loc.clone().add(0, 3, 0);

			@Override
			public void run() {
				double inc = (2 * Math.PI) / 50;
				double angle = step * inc + i;
				Vector v = new Vector();
				v.setX(Math.cos(angle) * radius);
				v.setZ(Math.sin(angle) * radius);
				display(particle, location);
				location.subtract(v);
				location.subtract(0, 0.1d, 0);
				if(location.getY() <= y) {
					cancel();
				}
				step += 4;
				radius += 1 / 50f;
			}
		};
		runnable.runTaskTimer(KageCore.getInstance(), 0, 1);
	}

	public static void display(Particle particle, Location location, int amount, float speed) {
		particle.builder().extra(speed).location(location).count(amount).spawn();
	}

	public static void display(Particle particle, Location location, int amount) {
		particle.builder().location(location).count(amount).spawn();
	}

	public static void display(Particle particle, Location location) {
		display(particle, location, 1);
	}

	public static void display(Particle particle, double x, double y, double z, Location location, int amount) {
		particle.builder().location(location).offset(x, y, z).count(amount).spawn();
	}

	public static void display(Particle particle, int red, int green, int blue, Location location, int amount) {
		particle.builder().location(location).color(Color.fromRGB(red, green, blue)).count(amount).spawn();
	}

	public static void display(int red, int green, int blue, Location location) {
		Particle.REDSTONE.builder().location(location).color(Color.fromRGB(red, green, blue)).spawn();
	}

	public static void display(Particle particle, int red, int green, int blue, Location location) {
		display(particle, red, green, blue, location, 1);
	}
}