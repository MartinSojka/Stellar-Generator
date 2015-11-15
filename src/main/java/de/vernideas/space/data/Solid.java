package de.vernideas.space.data;

/**
 * Abstract solids; parts of planets
 */
public class Solid {
	/** Generic high-temperature volatiles (mostly water ice) */
	public static final Solid ICE = new Solid("Ice", 1300, 4.5e-10);
	public static final Solid ICE_WATER = new Solid("Water Ice", 910, 4.5e-10); // Below 273K
	public static final Solid ICE_CO2 = new Solid("CO₂ Ice", 1562, 1e-8);
	// TODO: Find values for methane ice
	// public static final Solid ICE_METHANE = new Solid("Methane Ice", 1500, 4.5e-10);
	/** Generic rock (mostly silicates) */
	public static final Solid ROCK = new Solid("Rock", 3500, 3.3e-10);
	/** Generic metal (mostly iron) */
	public static final Solid METAL = new Solid("Metal", 7800, 1.8e-11);
	
	public final String name;
	public final double uncompressedDensity;
	public final double compressibility;
	
	protected Solid(String name, double uncompressedDensity, double compressibility) {
		this.name = name;
		this.uncompressedDensity = uncompressedDensity;
		this.compressibility = compressibility;
	}
}
