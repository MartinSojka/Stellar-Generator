package de.vernideas.space.data;

import java.util.Random;
import java.util.UUID;

import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * A global class holding all star systems, people and other universe-related things.
 */
@Accessors(fluent=true)
public class Universe {
	public final long seed;
	@Getter protected final Random random;
	
	public Universe() {
		this(UUID.randomUUID().getLeastSignificantBits());
	}
	
	public Universe(long seed) {
		this.seed = seed;
		this.random = new Random(seed);
	}
}
