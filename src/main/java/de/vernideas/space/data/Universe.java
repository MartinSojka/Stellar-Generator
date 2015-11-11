package de.vernideas.space.data;

import java.util.Random;
import java.util.UUID;

/**
 * A global class holding all star systems, people and other universe-related things.
 */
public class Universe {
	public final long seed;
	public final Random random;
	public final double maxStellarAge = 1.35e10;
	
	public Universe() {
		this(UUID.randomUUID().getLeastSignificantBits());
	}
	
	public Universe(long seed) {
		this.seed = seed;
		this.random = new Random(seed);
	}
}
