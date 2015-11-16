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
	public static final Solid ROCK = new Solid("Rock", 2800, 3.3e-10);
	/** Generic metal (mostly iron) */
	public static final Solid METAL = new Solid("Metal", 7250, 1.25e-12);
	public static final Solid METAL_IRON = new Solid("Iron", 7874, 5.88e-12);
	public static final Solid METAL_NICKEL = new Solid("Nickel", 8908, 5.56e-12);
	// Generic core, mantle and crust material mixes (based on "ideal" Earth)
	public static final Solid METAL_CORE = new Solid("Core Material", 8453, 1.245e-12);
	public static final Solid METAL_MANTLE = new Solid("Mantle Material", 4392, 1.929e-12);
	public static final Solid ROCK_CRUST = new Solid("Mantle Material", 2600, 17.95e-12);

	public final String name;
	public final double uncompressedDensity;
	public final double compressibility;
	
	protected Solid(String name, double uncompressedDensity, double compressibility) {
		this.name = name;
		this.uncompressedDensity = uncompressedDensity;
		this.compressibility = compressibility;
	}
	
	public double densityAtPressure(double pressure) {
		return uncompressedDensity / Math.exp(- compressibility * pressure);
	}
}
