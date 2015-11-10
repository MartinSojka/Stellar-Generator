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
	public final double mass;
	/** Diameter in m */
	public final double diameter;

	/** Randomiser data for consistent object building */
	public final long seed;
	public final Random random;

	@Getter @Setter protected Person owner;
	
	protected StellarObject(String name, double mass, double diameter, long seed)
	{
		this.name = name;
		this.mass = mass;
		this.diameter = diameter;
		this.seed = seed;
		this.random = new Random(seed);
	}
}