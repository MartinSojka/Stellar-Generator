package de.vernideas.space.data;

import java.util.Random;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@EqualsAndHashCode(of={"name","mass","diameter"})
@Accessors(fluent = true)
public abstract class StellarObject {

	@NonNull public final String name;
	/** Mass in Yg (10^21 kg) */
	@Getter @Setter protected double mass;
	/** Diameter in m */
	@Getter @Setter protected double diameter;

	/** Randomiser data for consistent object building */
	@Getter private long seed;
	@Getter private Random random;

	@Getter @Setter protected Person owner;
	
	protected StellarObject(String name) {
		this.name = name;
		this.random = new Random();
	}
	
	public double radius() {
		return this.diameter / 2.0;
	}
	
	public StellarObject seed(long seed) {
		this.seed = seed;
		this.random.setSeed(seed);
		return this;
	}
	
	public StellarObject random(@NonNull Random random) {
		this.random = random;
		return this;
	}
}