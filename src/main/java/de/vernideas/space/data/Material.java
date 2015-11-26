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
	/** Relatively pure olivine-type rock, intrusions tend to dramatically increase compressibility */
	public static final Material ROCK_OLIVINE = new Material("Olivine", 3320, 7.8e-12);
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
	
	public Material(String name, double uncompressedDensity, double compressibility) {
		this.name = name;
		this.uncompressedDensity = uncompressedDensity;
		this.compressibility = compressibility;
	}
	
	public double densityAtPressure(double pressure) {
		return uncompressedDensity * Math.exp(compressibility * pressure);
	}
	
	private double compression(double mass) {
		double c = Math.log(compressibility * 1e12);
		double compression = -0.00000794181233235453 * mass * c
				+ 0.0000220753829612755 *  mass
				+ -0.0895616181274319 * c
				+ 0.0148607527093062 * c * c
				+ -0.00616550736971977 * Math.sqrt(mass);
		return compression * compression + 1.0;
	}

	/**
	 * Estimate compressed density
	 * 
	 * Regression from following data (maxx, density, temp, uncompressed density)
	 * <pre>
	 * 0.0553 5427 448 5400
	 * 0.815 5243 327.8 3970
	 * 1 5503 278.7 4030
	 * 0.0123 3340 278.7 3300 # Moon
	 * 0.107 3933 226.1 3710
	 * 317.83 1326 122.2 300
	 * 95.159 687 90 300
	 * 14.536 1271 63.6 500
	 * 17.147 1638 50.8 500
	 * 0.0022 1869 44.4 1800 # Pluto
	 * 0.00016 2160 167.5 2100 # Ceres
	 * </pre>
	 */
	public double estimateCompressedDensity(double mass) {
		return uncompressedDensity * compression(mass / Constant.YOTTAGRAM);
	}
}
