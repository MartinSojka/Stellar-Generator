package de.vernideas.space.data;

/**
 * Abstract solids and fluids; parts of planets
 */
public class Material {
	/** Generic high-temperature volatiles (mostly water ice) */
	public static final Material ICE = new Material("Ice", 1300, 4.5e-10);
	public static final Material ICE_WATER = new Material("Water Ice", 910, 4.5e-10); // Below 273K
	public static final Material ICE_CO2 = new Material("CO₂ Ice", 1562, 1e-8);
	// TODO: Find values for methane ice
	// public static final Solid ICE_METHANE = new Solid("Methane Ice", 1500, 4.5e-10);
	/** Generic rock (mostly silicates) */
	public static final Material ROCK = new Material("Rock", 2800, 3.3e-10);
	/** Generic metal (mostly iron) */
	public static final Material METAL = new Material("Metal", 7250, 1.25e-12);
	public static final Material METAL_IRON = new Material("Iron", 7874, 5.88e-12);
	public static final Material METAL_NICKEL = new Material("Nickel", 8908, 5.56e-12);
	// Generic core, mantle and crust material mixes (based on "ideal" Earth)
	public static final Material METAL_CORE = new Material("Core Material", 8376, 1.2291e-12);
	public static final Material METAL_MANTLE = new Material("Mantle Material", 4230, 1.4667e-12);
	public static final Material ROCK_CRUST = new Material("Mantle Material", 2300, 713.22e-12);

	public final String name;
	public final double uncompressedDensity;
	public final double compressibility;
	
	protected Material(String name, double uncompressedDensity, double compressibility) {
		this.name = name;
		this.uncompressedDensity = uncompressedDensity;
		this.compressibility = compressibility;
	}
	
	public double densityAtPressure(double pressure) {
		return uncompressedDensity * Math.exp(compressibility * pressure);
	}
}
